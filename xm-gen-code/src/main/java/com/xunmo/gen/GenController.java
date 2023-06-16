package com.xunmo.gen;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fly.jdbc.SqlFly;
import com.fly.jdbc.cfg.FlyConfig;
import com.fly.jdbc.cfg.FlyObjects;
import com.fly.jdbc.datasource.FlyDataSource;
import com.xunmo.biz.data.GenData;
import com.xunmo.biz.data.GenDataVO;
import com.xunmo.biz.database.Database;
import com.xunmo.biz.field.GenField;
import com.xunmo.biz.gen.GenMapper;
import com.xunmo.biz.kv.DataKv;
import com.xunmo.biz.kv.GenKv;
import com.xunmo.biz.table.GenTable;
import com.xunmo.biz.table.GenTableUpdateVO;
import com.xunmo.biz.table.GenTableVO;
import com.xunmo.biz.table.TableInfo;
import com.xunmo.utils.AjaxJson;
import lombok.SneakyThrows;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.DownloadedFile;
import org.noear.solon.data.annotation.Tran;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Controller
@Mapping("/gen")
public class GenController {

    private final static ConcurrentHashMap<Long, SqlFly> cacheMap = new ConcurrentHashMap<>();

    @Inject
    private GenMapper genMapper;

    @Post
    @Mapping("/table/getAllTables")
    public AjaxJson queryAllTables(Map<String, Object> map) {
        if (map.containsKey("pageSize")) {
            final int pageSize = Convert.toInt(map.get("pageSize"));
            map.put("pageSize", pageSize);
            if (map.containsKey("pageNo")) {
                map.put("pageNo", (Convert.toInt(map.get("pageNo")) - 1) * pageSize);
                map.put("pageSize", pageSize);
            }
        }
        // ===================================  设置连接信息  ===================================
        final Long dataBaseId = Convert.toLong(map.get("dataBaseId"));
        if (dataBaseId == null) {
            long countAllTables = genMapper.countAllTables();
            if (countAllTables > 0) {
                List<TableInfo> tableInfos = genMapper.queryAllTables(map);
                return AjaxJson.getPageData(countAllTables, tableInfos);
            }
            return AjaxJson.getPageData(0L, Collections.emptyList());
        }
        Database database = genMapper.getDatabaseById(dataBaseId);
        if (database == null) {
            return AjaxJson.getPageData(0L, Collections.emptyList());
        }
        final String schema = database.getDataSchema();
        SqlFly sqlFly = getSqlFly(database);
        List<TableInfo> tableInfos = new ArrayList<>();
        try {
            final long count = sqlFly.getCount("SELECT count(*) as count FROM information_schema.TABLES WHERE table_schema = '" + schema + "' order by table_name desc, create_time desc");
            if (count > 0) {
                ResultSet rsList = sqlFly.getResultSet("SELECT" +
                        " table_name AS tableName," +
                        " ENGINE AS engine," +
                        " table_comment AS tableComment," +
                        " create_time   AS createTime" +
                        " FROM information_schema.TABLES WHERE table_schema = '" + schema + "' order by table_name desc, create_time desc" +
                        " limit " + map.get("pageNo") + ", " + map.get("pageSize"));
                while (rsList.next()) {
                    final String tableName = rsList.getString("tableName");
                    final String engine = rsList.getString("engine");
                    final String tableComment = rsList.getString("tableComment");
                    final Date createTime = rsList.getTimestamp("createTime");
                    final TableInfo tableInfo = new TableInfo();
                    tableInfo.setTableName(tableName);
                    tableInfo.setTableComment(tableComment);
                    tableInfo.setEngine(engine);
                    tableInfo.setCreateTime(createTime);
                    tableInfos.add(tableInfo);
                }
                return AjaxJson.getPageData((long) count, tableInfos);
            }
        } catch (Exception e) {
            return AjaxJson.getPageData(0L, Collections.emptyList());
        } finally {
            sqlFly.close();
        }
        return AjaxJson.getPageData(0L, Collections.emptyList());
    }

    @Post
    @Mapping("/table/getTable")
    public AjaxJson queryTable(String tableName) {
        TableInfo tableInfo = genMapper.queryTable(tableName);
        return AjaxJson.getSuccessData(tableInfo);
    }

    @Post
    @Mapping("/database/getAllDatabase")
    public AjaxJson getAllDatabase() {
        return AjaxJson.getSuccessData(genMapper.queryAllDatabases());
    }

    @Post
    @Mapping("/database/addDatabase")
    @Tran
    public AjaxJson addDatabase(@Body Database database) {
        if (database.getId() != null) {
            genMapper.updateDatabase(database);
            cacheMap.remove(database.getId());
        } else {
            genMapper.addDatabase(database);
        }
        return AjaxJson.getSuccessData(database);
    }

    @Post
    @Mapping("/database/delDatabase")
    @Tran
    public AjaxJson delDatabase(Long id) {
        genMapper.delDatabase(id);
        cacheMap.remove(id);
        return AjaxJson.getSuccess();
    }

    @Post
    @Mapping("/genData/addGenData")
    @Tran
    public AjaxJson addData(@Body GenDataVO genDataVO) {
        final List<DataKv> dataKvList = genDataVO.getDataKvList();
        for (DataKv dataKv : dataKvList) {
            genMapper.updateDataKv(dataKv);
        }
        genMapper.addGenData(genDataVO.getGenData());
        return AjaxJson.getSuccessData(genDataVO.getGenData());
    }

    @Post
    @Mapping("/genData/delGenData")
    @Tran
    public AjaxJson delGenData(Long id) {
        genMapper.delGenData(id);
        genMapper.delDataKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = 6191398306029707817L;

            {
                put("dataId", id);
            }
        });
        genMapper.delGenKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -1119007778150322820L;

            {
                put("dataId", id);
            }
        });
        return AjaxJson.getSuccess();
    }

    @Post
    @Mapping("/genData/updateGenData")
    @Tran
    public AjaxJson updateGenData(@Body GenDataVO genDataVO) {
        final List<DataKv> dataKvList = genDataVO.getDataKvList();
        for (DataKv dataKv : dataKvList) {
            genMapper.updateDataKv(dataKv);
        }
        genMapper.updateGenData(genDataVO.getGenData());
        return AjaxJson.getSuccessData(genDataVO.getGenData());
    }

    @Post
    @Mapping("/genData/genDataById")
    public AjaxJson genDataById(Long genDataId) {
        return AjaxJson.getSuccessData(genMapper.getGenDataById(genDataId));
    }

    @Post
    @Mapping("/genData/copyGenDataById")
    @Tran
    public AjaxJson copyGenDataById(Long genDataId) {
        final GenData sourceGenData = genMapper.getGenDataById(genDataId);
        sourceGenData.setId(null);
        sourceGenData.setName(sourceGenData.getName() + "_copy" + IdUtil.fastSimpleUUID());
        genMapper.addGenData(sourceGenData);
        final List<DataKv> dataKvList = genMapper.getDataKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -569738484249696572L;

            {
                put("dataId", genDataId);
            }
        });
        for (DataKv dataKv : dataKvList) {
            dataKv.setDataId(sourceGenData.getId());
            dataKv.setId(null);
            genMapper.addDataKv(dataKv);
        }
        return AjaxJson.getSuccessData(sourceGenData);
    }

    @Post
    @Mapping("/genData/genDataList")
    public AjaxJson genDataList(Map<String, Object> map) {
        final List<GenData> genTables = genMapper.getGenDataList(map);
        return AjaxJson.getSuccessData(genTables);
    }

    @Post
    @Mapping("/genTable/getGenTable")
    public AjaxJson getGenTable(Map<String, Object> map) {
        final long countGenTable = genMapper.countGenTable(map);
        if (countGenTable > 0) {
            if (map.containsKey("pageNo")) {
                final int pageSize = Convert.toInt(map.get("pageSize"));
                map.put("pageNo", (Convert.toInt(map.get("pageNo")) - 1) * pageSize);
                map.put("pageSize", pageSize);
            }
            final List<GenTable> genTables = genMapper.getGenTable(map);
            return AjaxJson.getPageData(countGenTable, genTables);
        }
        return AjaxJson.getPageData(0L, Collections.emptyList());
    }

    @Post
    @Mapping("/genTable/delGenTable")
    @Tran
    public AjaxJson delGenTable(Long id) {
        genMapper.delGenField(id);
        genMapper.delGenKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -2611939459529971899L;

            {
                put("tableId", id);
            }
        });
        genMapper.delGenTable(id);
        return AjaxJson.getSuccess();
    }

    @Post
    @Mapping("/genTable/updateGenTable")
    @Tran
    public AjaxJson updateGenTable(@Body GenTableUpdateVO genTableUpdateVO) {
        final GenTable genTable = genTableUpdateVO.getGenTable();
        final List<GenKv> genKvList = genTableUpdateVO.getGenKvList();
        genMapper.delGenKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -6226180470114642865L;

            {
                put("tableId", genTable.getId());
            }
        });
        genMapper.addGenKv(genKvList);
        genMapper.updateGenTable(genTable);
        return AjaxJson.getSuccessData(genTable);
    }

    @Post
    @Mapping("/genTable/addGenTable")
    @Tran
    public AjaxJson addTable(@Body GenTableVO vo) {
        final Long genDataId = vo.getGenDataId();
        if (genDataId == null) {
            return AjaxJson.getError("dataId不能为空");
        }
        final Long dataBaseId = vo.getDataBaseId();
        if (dataBaseId == null) {
            return AjaxJson.getError("dataBaseId不能为空");
        }
        GenData genData = genMapper.getGenDataById(genDataId);
        List<GenTable> tables = vo.getTables();
        for (GenTable genTable : tables) {
            final String tableName = genTable.getTableName();
            final String varName = StrUtil.toCamelCase(tableName);
            final String entityName = StrUtil.upperFirst(varName);
            final String tableComment = genTable.getTableComment();
            genTable.setDataId(genDataId);
            genTable.setEntityName(entityName);
            genTable.setVarName(varName);
            genTable.setSwaggerComment(tableComment);
            genTable.setMenuName(tableComment);
            if (genData != null) {
                final CopyOptions copyOptions = new CopyOptions();
                copyOptions.setIgnoreNullValue(true);
                copyOptions.setPropertiesFilter((field, o) -> {
                    if (o instanceof String) {
                        return StrUtil.isNotBlank((String) o);
                    }
                    return true;
                });
//                copyOptions.setIgnoreProperties(new ());
//                copyOptions.setFieldMapping(new HashMap<String,String>());
//                copyOptions.setFieldValueEditor(new BiFunction<String,Object,Object>());
                copyOptions.setTransientSupport(false);

                BeanUtil.copyProperties(genData, genTable, copyOptions);
                if (StrUtil.isBlank(genData.getAppAddBtnCode())) {
                    genTable.setAppAddBtnCode(varName + "-add");
                }
                if (StrUtil.isBlank(genData.getAppUpdateBtnCode())) {
                    genTable.setAppUpdateBtnCode(varName + "-update");
                }
                if (StrUtil.isBlank(genData.getAppDelBtnCode())) {
                    genTable.setAppDelBtnCode(varName + "-del");
                }
                if (StrUtil.isBlank(genData.getAppCopyBtnCode())) {
                    genTable.setAppCopyBtnCode(varName + "-copy");
                }
                if (StrUtil.isBlank(genData.getAppShowBtnCode())) {
                    genTable.setAppShowBtnCode(varName + "-detail");
                }
                genTable.setGenFlag("0");
            } else {
                genTable.setGenFlag("0");
                genTable.setAuthorName("zengyufei");
                genTable.setCreateTime(new Date());
                genTable.setPackageName("com.pj.biz");
                genTable.setModuleName("table");
                genTable.setEntitySuperClassName("");
                genTable.setMapperSuperClassName("");
                genTable.setServiceSuperClassName("");
                genTable.setServiceImplSuperClassName("");
                genTable.setConstFlag("0");
                genTable.setDsFlag("1");
                genTable.setTenderFlag("1");
                genTable.setLombokFlag("1");
                genTable.setChainFlag("1");
                genTable.setPrefixPath("");
                genTable.setSuffixPath("");
                genTable.setRemark(tableComment);
                genTable.setAppPackageName("");
                genTable.setAppModuleName(varName);
                genTable.setAppPopupType("1");
                genTable.setAppTemplateType("1");
                genTable.setAppAddBtnFlag("1");
                genTable.setAppUpdateBtnFlag("1");
                genTable.setAppDelBtnFlag("1");
                genTable.setAppCopyBtnFlag("1");
                genTable.setAppShowBtnFlag("1");
                genTable.setAppAddBtnCode(varName + "_add");
                genTable.setAppUpdateBtnCode(varName + "_update");
                genTable.setAppDelBtnCode(varName + "_del");
                genTable.setAppCopyBtnCode(varName + "_copy");
                genTable.setAppShowBtnCode(varName + "_show");
                genTable.setParentSystemId(1L);
                genTable.setParentMenuId(1L);
                genTable.setMenuIcon("el-icon-s-grid");
                genTable.setParentIdName("");
                genTable.setSlaveTalbeName("");
                genTable.setSlaveFieldName("");
            }
        }
        genMapper.addGenTable(tables);

        List<GenField> genFields = new ArrayList<>();
        for (GenTable table : tables) {
            final List<DataKv> dataKvList = genMapper.getDataKv(new HashMap<String, Object>() {
                private static final long serialVersionUID = -8170603731725784430L;

                {
                    put("dataId", genDataId);
                }
            });
            List<GenKv> genKvList = new ArrayList<>();
            for (DataKv dataKv : dataKvList) {
                final GenKv e = new GenKv();
                e.setKvId(dataKv.getId());
                e.setKey(dataKv.getKey());
                e.setDataId(genDataId);
                e.setTableId(table.getId());
                e.setSource(dataKv.getSource());
                e.setType(dataKv.getType());
                e.setValue(dataKv.getValue().getStr("value"));
                e.setLabel(dataKv.getValue().getStr("name"));
                genKvList.add(e);
            }
            genMapper.addGenKv(genKvList);

            final String tableName = table.getTableName();
            Database database = genMapper.getDatabaseById(dataBaseId);
            final String schema = database.getDataSchema();
            SqlFly sqlFly = getSqlFly(database);
            final List<Map<String, Object>> mapList = sqlFly.getMapList("SELECT\n" +
                    "            table_name as tableName,\n" +
                    "            column_name as columnName,\n" +
                    "            ordinal_position as ordinalPosition,\n" +
                    "            column_default as columnDefault,\n" +
                    "            is_nullable as isNullable,\n" +
                    "            data_type as dataType,\n" +
                    "            character_maximum_length as length,\n" +
                    "            numeric_precision as numericPrecision,\n" +
                    "            numeric_scale as numericScale,\n" +
                    "            column_type as columnType,\n" +
                    "            column_key as columnKey,\n" +
                    "            extra as extra,\n" +
                    "            column_comment as columnComment\n" +
                    "        FROM\n" +
                    "            information_schema.COLUMNS\n" +
                    "        WHERE\n" +
                    "            table_name = '" + tableName + "' and table_schema = '" + schema + "' order by ordinal_position");
//            final List<Map<String, Object>> mapList = genMapper.getAllFieldByTableName(tableName);
            if (CollUtil.isEmpty(mapList)) {
                continue;
            }
            for (Map<String, Object> map : mapList) {
                final GenField genField = new GenField();
                genField.setGenTableId(table.getId());
                genField.setTableName(tableName);
                final String dataType = StrUtil.blankToDefault((String) map.get("dataType"), (String) map.get("DATA_TYPE"));
                final String columnName = StrUtil.blankToDefault((String) map.get("columnName"), (String) map.get("COLUMN_NAME"));
                final String javaField = StrUtil.toCamelCase(columnName);
                genField.setJavaField(javaField);
                if (StrUtil.isNotBlank(dataType)) {
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "varchar", "char", "text")) {
                        genField.setJavaType("String");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "tinyint")) {
                        genField.setJavaType("Integer");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "double")) {
                        genField.setJavaType("Double");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "float")) {
                        genField.setJavaType("Float");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "int", "bigint")) {
                        genField.setJavaType("Long");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "date", "datetime", "timestamp")) {
                        genField.setJavaType("Date");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "blob")) {
                        genField.setJavaType("String");
                    }
                    if (StrUtil.equalsAnyIgnoreCase(dataType, "json")) {
                        genField.setJavaType("JSONObject");
                    }
                }
                genField.setColumnName(columnName);
                final String columnComment = StrUtil.blankToDefault((String) map.get("columnComment"), (String) map.get("COLUMN_COMMENT"));
                genField.setColumnComment(columnComment);
                genField.setSwaggerComment(columnComment);
                genField.setColumnType(StrUtil.blankToDefault((String) map.get("columnType"), (String) map.get("COLUMN_TYPE")));
                genField.setTsType("");
                Object position = map.get("ordinalPosition");
                if (position == null) {
                    position = map.get("ORDINAL_POSITION");
                    if (position instanceof BigInteger) {
                        final long value = ((BigInteger) position).longValue();
                        genField.setSort(value);
                    } else {
                        genField.setSort((Long) position);
                    }
                } else {
                    genField.setSort((Long) position);
                }
                genField.setLogicDeleteFlag("0");
                genField.setVersionFlag("0");
                genField.setFillType(null);
                genField.setAddFlag("1");
                genField.setEditFlag("1");
                genField.setListFlag("1");
                genField.setQueryFlag("1");
                genField.setDefaultValue(StrUtil.blankToDefault((String) map.get("columnDefault"), (String) map.get("COLUMN_DEFAULT")));
                genField.setDataType(dataType);
                Object length = map.get("length");
                if (length == null) {
                    length = map.get("CHARACTER_MAXIMUM_LENGTH");
                    if (length instanceof BigInteger) {
                        final long longValue = ((BigInteger) length).longValue();
                        genField.setLength(longValue);
                    } else {
                        genField.setLength(Convert.toLong(length));
                    }
                } else {
                    genField.setLength(Convert.toLong(length));
                }
                genField.setWidth("");
                genField.setDictType("");
                genField.setQueryType("1");
                genField.setEnumStr("");
                genField.setEditHelpMessage("");
                genField.setListHelpMessage("");
                genField.setNumericPrecision("");
                genField.setNumericScale("");

                final String isNullable = StrUtil.blankToDefault((String) map.get("isNullable"), (String) map.get("IS_NULLABLE"));
                genField.setIsNullable(isNullable);
                if (StrUtil.isNotBlank(isNullable) && StrUtil.equalsIgnoreCase("NO", isNullable)) {
                    genField.setMustFlag("1");
                }

                final String columnKey = StrUtil.blankToDefault((String) map.get("columnKey"), (String) map.get("COLUMN_KEY"));
                genField.setColumnKey(columnKey);
                if (StrUtil.isNotBlank(columnKey) && StrUtil.equalsIgnoreCase("PRI", columnKey)) {
                    genField.setPkFlag("1");
                }
                final String extra = StrUtil.blankToDefault((String) map.get("extra"), (String) map.get("EXTRA"));
                genField.setExtra(extra);
                if (StrUtil.isNotBlank(extra) && StrUtil.equalsIgnoreCase("auto_increment", extra)) {
                    genField.setIncFlag("1");
                }
                genField.setCreateTime(new Date());
                genField.setUpdateTime(new Date());

                genFields.add(genField);
            }
        }
        genMapper.addGenField(genFields);
        return AjaxJson.getSuccess();
    }

    @Post
    @Mapping("/genField/getGenField")
    public AjaxJson getGenField(Map<String, Object> map) {
        final long countGenField = genMapper.countGenField(map);
        if (countGenField > 0) {
            if (map.containsKey("pageNo")) {
                final int pageSize = Convert.toInt(map.get("pageSize"));
                map.put("pageNo", (Convert.toInt(map.get("pageNo")) - 1) * pageSize);
                map.put("pageSize", pageSize);
            }
            final List<GenField> genFields = genMapper.getGenField(map);
            return AjaxJson.getPageData(countGenField, genFields);
        }
        return AjaxJson.getPageData(0L, Collections.emptyList());
    }

    @Post
    @Mapping("/genField/updateGenField")
    @Tran
    public AjaxJson updateGenField(@Body GenField genField) {
        genField.setColumnComment(StrUtil.blankToDefault(genField.getColumnComment(), ""));
        genMapper.updateGenField(genField);
        return AjaxJson.getSuccessData(genField);
    }

    @Post
    @Mapping("/system/getAllSystem")
    public AjaxJson getAllSystem(Map<String, Object> map) {
        final List<Map<String, Object>> genTables = genMapper.getAllSystem(map);
        return AjaxJson.getSuccessData(genTables);
    }

    @Post
    @Mapping("/kv/addDataKv")
    @Tran
    public AjaxJson addDataKv(@Body DataKv dataKv) {
        try {
            genMapper.addDataKv(dataKv);
        } catch (Exception e) {
            if (e instanceof DuplicateKeyException) {
                return AjaxJson.getError("key 不能与现有的前后端值相等,请重新起一个key");
            }
            return AjaxJson.getError(e.getLocalizedMessage());
        }
        return AjaxJson.getSuccessData(dataKv);
    }

    @Post
    @Mapping("/kv/delDataKv")
    @Tran
    public AjaxJson delDataKv(Long id) {
        genMapper.delGenKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = 5036047468868856900L;

            {
                put("kvId", id);
            }
        });
        genMapper.delDataKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -5065586897795017236L;

            {
                put("id", id);
            }
        });
        return AjaxJson.getSuccess();
    }

    @Post
    @Mapping("/kv/getDataKv")
    public AjaxJson getDataKv(Map<String, Object> map) {
        final List<DataKv> dataKvList = genMapper.getDataKv(map);
        return AjaxJson.getSuccessData(dataKvList);
    }

    @Post
    @Mapping("/kv/getGenKv")
    public AjaxJson getGenKv(Map<String, Object> map) {
        final List<GenKv> kvList = genMapper.getGenKv(map);
        return AjaxJson.getSuccessData(kvList);
    }

    @Post
    @Mapping("/system/getParentMenus")
    public AjaxJson getParentMenus(Map<String, Object> map) {
        final List<Map<String, Object>> genTables = genMapper.getParentMenus(map);
        return AjaxJson.getSuccessData(genTables);
    }

    @Post
    @Mapping("/template/getAllTemplate")
    public AjaxJson getAllTemplate() throws Exception {
        List<XmFileVO> vos = new ArrayList<>();
        final String templateDirName = "template";
        List<XmFile> fileList = getXmFiles(templateDirName);

        final Map<String, Map<String, List<XmFile>>> groupByMap = fileList.stream().collect(Collectors.groupingBy(XmFile::getSecondDirName,
                Collectors.groupingBy(XmFile::getThreeDirName)));

        final Set<Map.Entry<String, Map<String, List<XmFile>>>> entries = groupByMap.entrySet();
        for (Map.Entry<String, Map<String, List<XmFile>>> entry : entries) {
            final String key = entry.getKey();
            final Map<String, List<XmFile>> value = entry.getValue();

            final XmFileVO zyfFileVO = new XmFileVO();
            zyfFileVO.setName(key);
            List<XmFileVO> children = new ArrayList<>();
            final Set<Map.Entry<String, List<XmFile>>> cEntries = value.entrySet();
            for (Map.Entry<String, List<XmFile>> cEntry : cEntries) {
                final String cEntryKey = cEntry.getKey();
                final List<XmFile> cEntryValue = cEntry.getValue();

                final XmFileVO cXmFileVO = new XmFileVO();
                cXmFileVO.setName(cEntryKey);
                cXmFileVO.setChildren(cEntryValue);
                children.add(cXmFileVO);
            }
            zyfFileVO.setChildren(children);
            vos.add(zyfFileVO);
        }
        return AjaxJson.getSuccessData(vos);
    }

    @Post
    @Mapping("/template/getTemplateAllParams")
    public AjaxJson getTemplateAllParams(Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();
        final Long genTableId = Convert.toLong(map.get("genTableId"));
        final GenTable genTable = genMapper.getGenTableById(genTableId);
        final JSONObject parse = JSONUtil.parseObj(genTable);
        handlerJson(parse, "t_table");

        final List<GenField> genFields = genMapper.getGenField(map);
        final GenField genField = genFields.get(0);
        final JSONObject cParse = JSONUtil.parseObj(genField);
        handlerJson(cParse, "t_field");

        genFields.stream()
                .filter(s -> StrUtil.equals("1", s.getPkFlag()))
                .findFirst().ifPresent(pkGenField -> result.put("pk", pkGenField));
        final List<GenKv> genKvList = genMapper.getGenKv(map);

        if (CollUtil.isNotEmpty(genKvList)) {
            final JSONObject jsonObject = new JSONObject();
            for (GenKv genKv : genKvList) {
                jsonObject.set(genKv.getKey(), genKv.getLabel());
            }
            result.put("kv", jsonObject);
//            final GenKv genKv = genKvList.get(0);
//            final JSONObject kvParse = JSONUtil.parseObj(genKv);
//            handlerJson(kvParse, "t_gen_kv");
//            result.put("kv", jsonObject);
        }
        result.put("t", parse);
        result.put("cs", cParse);
        return AjaxJson.getSuccessData(result);
    }

    private void handlerJson(JSONObject parse, String tableName) {
        List<Map<String, Object>> allFieldByTableName = genMapper.getAllFieldByTableName(tableName);
        final Map<String, Map<String, Object>> columnNameMap = allFieldByTableName.stream().collect(Collectors.toMap(s -> Convert.toStr(s.get("columnName")), v -> v));
        final List<String> keys = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = parse.entrySet();
        entries.forEach(stringObjectEntry -> {
            final String key = stringObjectEntry.getKey();
            keys.add(key);
        });
        keys.forEach(key -> {
            final Map<String, Object> objectMap = columnNameMap.get(key);
            final String columnComment = Convert.toStr(objectMap.get("columnComment"));
            parse.set(key + "_comment", columnComment);
        });
    }

    /**
     * 预览代码
     */
    @Post
    @Mapping("/preview")
    public AjaxJson previewCode(Map<String, Object> map) throws IOException {
        final Long genTableId = Convert.toLong(map.get("genTableId"));
        if (genTableId == null) {
            throw new IllegalArgumentException("genTableId 不能为空");
        }
//        final String tableName = (String) map.get("tableName");
        final String endPoint = StrUtil.blankToDefault((String) map.get("endPoint"), "backEnd"); // frontEnd


        Map<String, Object> parameMap = getParameMap(genTableId);

        List<GenView> list = new ArrayList<>();
        final String templateDirName = "template";
        List<XmFile> fileList = getXmFiles(templateDirName);
        for (XmFile zyfFile : fileList) {
            final String fileName = zyfFile.getFileName();
            final String fullPath = zyfFile.getFullPath();
            final String parentDir = zyfFile.getParentDir();
//            final String parentDirs = zyfFile.getParentDirs();
//            final List<String> paths = zyfFile.getPaths();

            if (!StrUtil.equals(zyfFile.getSecondDirName(), endPoint)) {
                continue;
            }
            // 这部分自定义 ---------------  begin
            String language = "java";
            final GenView genView = new GenView();
            if (StrUtil.equalsAnyIgnoreCase(endPoint, "后端")) {
                if (StrUtil.containsIgnoreCase(fileName, "xml")) {
                    language = "xml";
                }
            } else {
                language = "javascript";
            }
            // 这部分自定义 --------------- end

            genView.setLanguage(language);
            genView.setParentDir(parentDir);
            genView.setName(genView.getParentDir() + "/" + fileName);
            genView.setFileName(fileName);

            String code = FreeMarkerUtil.getCode(fullPath, parameMap);        // 内容
            genView.setCode(code);
            // 模块
          /*  final List<DbTable> tableList = GenCfgManager.cfg.tableList;
            for (DbTable t : tableList) {
                String code = FreeMarkerUtil.getResult(fullPath, "t", t);        // 内容
                genView.setCode(code);
            }*/
            list.add(genView);
        }

        return AjaxJson.getSuccessData(list);
    }


    /**
     * 生成代码
     */
    @SneakyThrows
    @Post
    @Mapping("/complieCode")
    public AjaxJson complieCode(@Body Map<String, Object> map) {
        final Long genTableId = Convert.toLong(map.get("genTableId"));
        if (genTableId == null) {
            throw new IllegalArgumentException("genTableId 不能为空");
        }
        final String value = Convert.toStr(map.get("value"));
        Map<String, Object> parameMap = getParameMap(genTableId);

        try {
            String code = FreeMarkerUtil.getCodeLine(value, parameMap);
            return AjaxJson.getSuccessData(code);
        } catch (Exception e) {
            return AjaxJson.getSuccess();
        }
    }

    /**
     * 生成代码
     */
    @SneakyThrows
    @Post
    @Mapping("/download")
    public DownloadedFile download(@Body Map<String, Object> map) {
        final String tableName = (String) map.get("tableName");
        final String endPoint = StrUtil.blankToDefault((String) map.get("endPoint"), "all"); // frontEnd
        final Map<String, String> templates = (Map<String, String>) map.get("templates");
        final Map<String, String> downloadSetting = (Map<String, String>) map.get("downloadSetting");
        final Long genTableId = Convert.toLong(map.get("genTableId"));
        if (genTableId == null) {
            throw new IllegalArgumentException("genTableId 不能为空");
        }
        Map<String, Object> parameMap = getParameMap(genTableId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream(outputStream);

//        List<GenView> list = new ArrayList<>();
        final String templateDirName = "template";
        List<XmFile> fileList = getXmFiles(templateDirName);

        for (XmFile zyfFile : fileList) {
            String fileName = zyfFile.getFileName();
            final String fullPath = zyfFile.getFullPath();
            final String parentDir = zyfFile.getParentDir();
//            final String parentDirs = zyfFile.getParentDirs();
//            final List<String> paths = zyfFile.getPaths();
            String firstDirName = zyfFile.getFirstDirName();
            String secondDirName = zyfFile.getSecondDirName();

            if (!StrUtil.equalsIgnoreCase(endPoint, "all") && !StrUtil.equals(secondDirName, endPoint)) {
                continue;
            }

            if (downloadSetting.containsKey(parentDir)) {
                final String flag = downloadSetting.get(parentDir);
                if (!StrUtil.equals(flag, "1")) {
                    continue;
                }
            }
            if (!StrUtil.equalsIgnoreCase(endPoint, "all") && !templates.containsValue(fileName)) {
                continue;
            }
            // 这部分自定义 ---------------  begin
            String language = "java";
            String suffix = ".java";
            final GenView genView = new GenView();
            if (StrUtil.equalsAnyIgnoreCase(secondDirName, "后端")) {
                if (StrUtil.containsIgnoreCase(parentDir, "xml") || StrUtil.containsIgnoreCase(fileName, "xml")) {
                    language = "xml";
                    suffix = ".xml";
                }
            } else {
                language = "javascript";
                suffix = ".js";
            }
            // 这部分自定义 --------------- end

            genView.setLanguage(language);
            genView.setSuffix(suffix);
            genView.setParentDir(parentDir);
            genView.setFileName(fileName);
            String newFileName = FileUtil.mainName(fileName) + suffix;
            final GenTable t = (GenTable) parameMap.get("t");
            newFileName = StrUtil.replace(newFileName, "XXX", t.getEntityName());
            genView.setName(genView.getParentDir() + "/" + newFileName);
            String code = FreeMarkerUtil.getCode(fullPath, parameMap);        // 内容

            zip.putNextEntry(new ZipEntry(Objects.requireNonNull(genView.getName())));
            IoUtil.write(zip, StandardCharsets.UTF_8, false, code);
            zip.closeEntry();
        }

        IoUtil.close(zip);
        byte[] data = outputStream.toByteArray();

        return new DownloadedFile("application/octet-stream; charset=UTF-8", data, tableName + "-" + endPoint);
    }

    private List<XmFile> getXmFiles(String templateDirName) throws IOException {
        List<XmFile> fileList = new ArrayList<>();
        final ClassPathResource classPathResource = new ClassPathResource(templateDirName);
        if (URLUtil.isJarFileURL(classPathResource.getUrl())) {
            final Enumeration<JarEntry> entries = URLUtil.getJarFile(classPathResource.getUrl()).entries();
            while (entries.hasMoreElements()) {
                final JarEntry jarEntry = entries.nextElement();
                if (!jarEntry.isDirectory()) {
                    final String fullPath = jarEntry.getName();
                    if (fullPath.startsWith(templateDirName)) {
                        final String fileName = StrUtil.subAfter(fullPath, "/", true);
                        final String parentDirs = StrUtil.subBefore(fullPath, "/", true);
                        final String parentDir = StrUtil.subAfter(parentDirs, "/", true);
                        final XmFile zyfFile = new XmFile();
                        zyfFile.setFileName(fileName);
                        zyfFile.setParentDirs(parentDirs);
                        zyfFile.setParentDir(parentDir);
                        zyfFile.setFullPath(fullPath);
                        final List<String> paths = StrUtil.split(fullPath, '/');
                        zyfFile.setPaths(paths);
                        zyfFile.setFirstDirName(paths.get(0));
                        zyfFile.setSecondDirName(paths.get(1));
                        zyfFile.setThreeDirName(paths.get(2));
                        fileList.add(zyfFile);
                    }
                }
            }
        } else {
            final File file = classPathResource.getFile();
            final List<File> loopFiles = FileUtil.loopFiles(file);
            for (File loopFile : loopFiles) {
                final String path = loopFile.getPath();
                final String fullPath = StrUtil.subAfter(path, "classes" + File.separator, true);
                final String fileName = loopFile.getName();
                final String parentDirs = StrUtil.subBefore(fullPath, File.separator, true);
                final String parentDir = StrUtil.subAfter(parentDirs, File.separator, true);
                final XmFile zyfFile = new XmFile();
                zyfFile.setFileName(fileName);
                zyfFile.setParentDirs(parentDirs);
                zyfFile.setParentDir(parentDir);
                zyfFile.setFullPath(fullPath);
                final List<String> paths = StrUtil.split(fullPath, File.separatorChar);
                zyfFile.setPaths(paths);
                zyfFile.setFirstDirName(paths.get(0));
                zyfFile.setSecondDirName(paths.get(1));
                zyfFile.setThreeDirName(paths.get(2));
                fileList.add(zyfFile);
            }
        }
        return fileList;
    }


    private SqlFly getSqlFly(Database database) {
        final Long dataBaseId = database.getId();
        final String schema = database.getDataSchema();
        // mysql
        if (StrUtil.equals(database.getType(), "1")) {
            FlyConfig config = new FlyConfig();
            config.setDriverClassName("com.mysql.cj.jdbc.Driver");
            config.setUrl("jdbc:mysql://" + database.getHost() + ":" + database.getPort() + "/" + schema + "?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC");
            config.setUsername(database.getUsername());
            config.setPassword(database.getPassword());
            config.setPrintSql(true);        // 是否打印sql
            FlyObjects.setConfig(config);    // 注入到框架中
            final FlyDataSource dataSource = new FlyDataSource();
            dataSource.setDriverClassName(config.getDriverClassName());
            dataSource.setUrl(config.getUrl());
            dataSource.setUsername(config.getUsername());
            dataSource.setPassword(config.getPassword());
            FlyObjects.setDataSource(dataSource);
        }
        SqlFly sqlFly;
        if (cacheMap.containsKey(dataBaseId)) {
            sqlFly = cacheMap.get(dataBaseId);
        } else {
            sqlFly = new SqlFly();
            cacheMap.put(dataBaseId, sqlFly);
        }
        return sqlFly;
    }

    private Map<String, Object> getParameMap(Long genTableId) {
        GenTable genTable = genMapper.getGenTableById(genTableId);
        final List<GenField> genFields = genMapper.getGenField(new HashMap<String, Object>() {
            private static final long serialVersionUID = -3488824529445122526L;

            {
                put("genTableId", genTableId);
            }
        });

        final GenField pkGenField = genFields.stream()
                .filter(s -> StrUtil.equals("1", s.getPkFlag()))
                .findFirst()
                .orElse(null);


        Map<String, Object> parameMap = new HashMap<>();
        final List<GenKv> genKvList = genMapper.getGenKv(new HashMap<String, Object>() {
            private static final long serialVersionUID = -5505360382883768375L;

            {
                put("tableId", genTableId);
            }
        });
        if (CollUtil.isNotEmpty(genKvList)) {
            final JSONObject jsonObject = new JSONObject();
            for (GenKv genKv : genKvList) {
                jsonObject.set(genKv.getKey(), genKv.getValue());
            }
            parameMap.put("kv", jsonObject);
        }
        parameMap.put("t", genTable);
        parameMap.put("cs", genFields);
        parameMap.put("pk", pkGenField);
        return parameMap;
    }
}
