package com.xunmo.webs.dict.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xunmo.annotations.AutoTran;
import com.xunmo.base.move.XmSimpleMoveService;
import com.xunmo.base.move.XmSimpleMoveServiceImpl;
import com.xunmo.common.CustomException;
import com.xunmo.common.enums.SystemStatus;
import com.xunmo.common.utils.FileDownloadUtil;
import com.xunmo.common.utils.ImportExcelHelper;
import com.xunmo.common.utils.XmUtil;
import com.xunmo.core.utils.LamUtil;
import com.xunmo.utils.TranAfterUtil;
import com.xunmo.webs.dict.dto.DictGetPageDTO;
import com.xunmo.webs.dict.dto.DictImportExecl;
import com.xunmo.webs.dict.dto.DictUpdateDTO;
import com.xunmo.webs.dict.entity.Dict;
import com.xunmo.webs.dict.mapper.DictMapper;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.aspect.annotation.Service;
import org.noear.solon.core.handle.UploadedFile;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service: t_xm -- 项目
 *
 * @author zengyufei
 */
@Slf4j
@Service
@AutoTran
public class DictService extends XmSimpleMoveServiceImpl<DictMapper, Dict> implements XmSimpleMoveService<Dict> {

    /**
     * 获取列表
     *
     * @param dictGetPageDTO dict获取页面dto
     * @return {@link List}<{@link Dict}>
     */
    public List<Dict> getList(DictGetPageDTO dictGetPageDTO) {
        final Dict dict = dictGetPageDTO.toEntity();
        return XmUtil.startPage(Dict.class, () -> this.baseMapper.selectList(Wrappers.lambdaQuery(dict)));
    }

    /**
     * 获取树
     *
     * @param inputId inputId
     * @return {@link List}<{@link Tree}<{@link String}>>
     */
    public List<Tree<String>> getTree(String inputId) {
        inputId = StrUtil.blankToDefault(inputId, "-1");
        final List<Dict> dicts = this.baseMapper.selectList(Wrappers.<Dict>lambdaQuery().eq(Dict::getParentId, inputId));
        // 配置
        TreeNodeConfig treeNodeConfig = new TreeNodeConfig();
        // 自定义属性名 ，即返回列表里对象的字段名
        treeNodeConfig.setIdKey(LamUtil.getFieldName(Dict::getId));
        treeNodeConfig.setParentIdKey(LamUtil.getFieldName(Dict::getParentId));
        treeNodeConfig.setWeightKey(LamUtil.getFieldName(Dict::getSort));
        treeNodeConfig.setNameKey(LamUtil.getFieldName(Dict::getDicDescription));
        treeNodeConfig.setChildrenKey("children");

        return TreeUtil.build(dicts, "-1", treeNodeConfig, (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setWeight(treeNode.getSort());
            tree.setName(treeNode.getDicDescription());
            // 扩展属性 ...
//            tree.putExtra("sectionId", treeNode.getSectionId());
            for (Field field : ReflectUtil.getFields(Dict.class)) {
                final String fieldName = field.getName();
                if (StrUtil.equalsAnyIgnoreCase(fieldName, "id", "parentid", "weight", "name")) {
                    continue;
                }
                final Object fieldValue = ReflectUtil.getFieldValue(treeNode, field);
                tree.putExtra(fieldName, fieldValue);
            }
        });
    }

    /**
     * 添加
     *
     * @param dict dict
     * @return {@link Dict}
     */
//    @Tran
    public Dict addBean(Dict dict) throws Exception {
        {
            final boolean inTrans = TranUtils.inTrans();
            log.info("执行时输出, 事务状态:" + inTrans);
        }
        final String parentId = StrUtil.blankToDefault(dict.getParentId(), "-1");
        dict.setParentId(parentId);
        int max = this.baseMapper.getMax(dict);
        dict.setSort(max + 1);
        if (StrUtil.equals("-1", parentId)) {
            dict.setDicLevel(1);
        } else {
            final Dict parentDict = this.checkAndGet(parentId);
            Integer parentLevel = parentDict.getDicLevel();
            dict.setDicLevel(++parentLevel);
            dict.setParentName(parentDict.getParentName());
        }
        this.baseMapper.insert(dict);
        TranAfterUtil.execute(() -> {
            final boolean inTrans = TranUtils.inTrans();
            log.info("事务后执行输出, 事务状态:" + inTrans);
        });
        TranAfterUtil.executeSync(() -> {
            final boolean inTrans = TranUtils.inTrans();
            log.info("异步事务后执行输出, 事务状态:" + inTrans);
        });
        return dict;
    }

    /**
     * 更新
     *
     * @param dictUpdateDTO dict更新dto
     * @return boolean
     */
    @Tran
    public boolean updateBean(DictUpdateDTO dictUpdateDTO) throws Exception {
        final Dict inputDict = toBean(dictUpdateDTO, Dict.class);
        final String id = inputDict.getId();
        final String inputParentId = StrUtil.blankToDefault(inputDict.getParentId(), "-1");

        final Dict oldDict = checkAndGet(id);
        if (!StrUtil.equalsIgnoreCase(oldDict.getParentId(), inputParentId)) {
            int max = this.baseMapper.getMax(oldDict);
            inputDict.setSort(max + 1);
            inputDict.setParentId(inputParentId);

            if (StrUtil.equals("-1", inputParentId)) {
                inputDict.setDicLevel(1);
            } else {
                final Dict parentDict = this.checkAndGet(inputParentId);
                Integer parentLevel = parentDict.getDicLevel();
                inputDict.setDicLevel(++parentLevel);
                inputDict.setParentName(parentDict.getParentName());
            }
        }
        this.baseMapper.updateNotNullById(inputDict);
        return true;
    }

    /**
     * 删除
     *
     * @param id id
     * @return boolean
     */
    @Tran
    public boolean delById(String id) {
        final List<Dict> dicts = this.baseMapper.selectList(Wrappers.<Dict>lambdaQuery().eq(Dict::getParentId, id));
        if (CollUtil.isNotEmpty(dicts)) {
            this.baseMapper.deleteBatchIds(LamUtil.mapToList(dicts, Dict::getId));
        }
        this.baseMapper.deleteById(id);
        return true;
    }


    /**
     * 下载模板
     *
     * @throws Exception 异常
     */
    public void downloadTemplate() throws Exception {
        String fileName = "字典导入模板.xlsx";
        try {
            FileDownloadUtil.downloadExeclTemplate(DictImportExecl.class, fileName);
        } catch (Exception e) {
            log.error("导出{}文件失败！", fileName);
            throw e;
        }
    }


    /**
     * 导入文件
     *
     * @param file 文件
     * @return boolean
     * @throws Exception 异常
     */
    @Tran
    public boolean importFile(UploadedFile file) throws Exception {
        String fileExtension = file.getExtension();
        if (StrUtil.isBlank(fileExtension)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "只能上传有后缀的文件");
        }
        if (!"xls".equals(fileExtension) && !"xlsx".equals(fileExtension)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "只能上传格式为xls或xlsx的文件");
        }
        // 读取文件
        ImportExcelHelper<DictImportExecl> helper = new ImportExcelHelper<>();
        List<DictImportExecl> excelList = helper.getList(file, DictImportExecl.class, 0, 1);
        if (CollUtil.isEmpty(excelList)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "解析为空文件,解析失败!");
        }

        // 移除空列
        excelList = excelList.stream()
                .filter(data -> !StrUtil.isBlank(data.getDicValue()) && !StrUtil.isBlank(data.getDicDescription())
                        && !StrUtil.isBlank(data.getDicCode())).collect(Collectors.toList());
        if (CollUtil.isEmpty(excelList)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "解析为空文件,解析失败!");
        }

        // 检查表内特定列值重复
        // 检查特定列值不为空
        int dataLine = 0;
        List<Integer> errorList = new ArrayList<>();
        Set<String> codeSet = new HashSet<>();
        for (DictImportExecl dictImportExecl : excelList) {
            dataLine++;
            String parentName = dictImportExecl.getParentName();
            String code = dictImportExecl.getDicCode();
            final String key = parentName + "_" + code;
            if (StrUtil.isBlank(code)) {
                errorList.add(dataLine);
            } else {
                if (codeSet.contains(key)) {
                    throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "导入文件中存在字典编码重复!");
                }
                codeSet.add(key);
            }
        }
        if (CollUtil.isNotEmpty(errorList)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "第" + errorList + "行的字典编码不能为空");
        }
        List<Dict> readDictList = BeanUtil.copyToList(excelList, Dict.class);
        if (CollUtil.isEmpty(readDictList)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "解析失败!");
        }

        // 根据现有容量，判断走轻量方案
//        final long allDictCount = baseMapper.selectCount(Wrappers.emptyWrapper());
        // 需要处理 parentId 问题， sort 问题， dicLevel 问题


        // 查全表
        final List<Dict> allDict = baseMapper.selectList(Wrappers.emptyWrapper());
//        Map<String, Dict> nameByDictMap = LamUtil.listToBeanMap(allDict, Dict::getDicDescription);
//        for (Dict dict : readDictList) {
//            final String dicDescription = dict.getDicDescription();
//            if (nameByDictMap.containsKey(dicDescription)) {
//                throw new CustomException(SystemStatus.IN_VALIDA_PARAM, dicDescription + " 字典名字重复!");
//            }
//        }


        final Map<String, Dict> oldCodeByDictMap = LamUtil.listToBeanMap(allDict, Dict::getDicCode);

        // 获取所有父子结构的最大值 sort
        final Map<String, Integer> parentIdByMaxSortMap = LamUtil.listFilterToMap(allDict,
                dict -> dict.getParentId().equals("-1"),
                Dict::getId, Dict::getSort,
                (t1, t2) -> t1.compareTo(t2) > 0 ? t1 : t2);

        // 获取 root 节点 sort 最大值
        int rootMaxSort = allDict.stream()
                .filter(dict -> dict.getParentId().equals("-1"))
                .map(Dict::getSort)
                .max(Integer::compareTo)
                .orElse(0);

        // 导入的数据没有上级，就是 root 节点
        List<Dict> levelFirstByInputDictList = LamUtil.filterToList(readDictList, readDict -> StrUtil.isBlank(readDict.getParentName()));
        // 给 root 节点设置上级 -1，设置最大 sort 值
        for (Dict levelFirstInputDict : levelFirstByInputDictList) {
            final String levelFirstInputCode = levelFirstInputDict.getDicCode();
            // 已存在则不改变其三个问题
            if (oldCodeByDictMap.containsKey(levelFirstInputCode)) {
                final Dict oldDict = oldCodeByDictMap.get(levelFirstInputCode);
                levelFirstInputDict.setParentId("-1");
                levelFirstInputDict.setDicLevel(oldDict.getDicLevel());
                levelFirstInputDict.setSort(oldDict.getSort());
                continue;
            }
            // 全新增则处理三个问题
            levelFirstInputDict.setParentId("-1");
            levelFirstInputDict.setDicLevel(1);
            rootMaxSort = rootMaxSort + 1;
            levelFirstInputDict.setSort(rootMaxSort);
        }


        // 拆分新增和修改 -- 与上面三个问题处理分离开来，逻辑清晰点
        List<String> addIds = new ArrayList<>();
        final List<Dict> stayAddDictList = new ArrayList<>();
        final List<Dict> stayUpdateDictList = new ArrayList<>();
        final Snowflake snowflake = IdUtil.getSnowflake();
        for (Dict readDict : readDictList) {
            final String readDicCode = readDict.getDicCode();
            // 已存在
            if (oldCodeByDictMap.containsKey(readDicCode)) {
                final Dict oldDict = oldCodeByDictMap.get(readDicCode);
                readDict.setId(oldDict.getId());
                readDict.setParentId(oldDict.getParentId());
                readDict.setParentName(oldDict.getParentName());
                stayUpdateDictList.add(readDict);
            } else {
                // 全新增
                final String newId = snowflake.nextIdStr();
                addIds.add(newId);
                readDict.setId(newId);
                stayAddDictList.add(readDict);
            }
        }

        // 与全表合并方便中文上级转换id
        allDict.addAll(readDictList);
        final Map<String, Dict> nameByDictMap = LamUtil.listToBeanMap(allDict, Dict::getDicDescription);

        // 因为全部值有了id， 这里把所有中文上级转化为id
        for (Dict readDict : readDictList) {
            final String parentName = readDict.getParentName();
            // 根据名字找到上级
            if (nameByDictMap.containsKey(parentName)) {
                final Dict parentDict = nameByDictMap.get(parentName);
                final String parentId = parentDict.getId();
                readDict.setParentId(parentId);
                readDict.setParentName(parentName);
            }
        }

        // 从level==1开始设置level和sort
        final Map<String, List<Dict>> parentIdGroupByDictMap = LamUtil.groupByToMap(allDict, Dict::getParentId);
        handlerAllOrderByLevelAsc(LamUtil.filterToList(allDict,  dict -> dict.getParentId().equals("-1")), parentIdGroupByDictMap, Dict::getId, levelFirstDict -> {
            final String id = levelFirstDict.getId();
//            if (!addIds.contains(id)) {
//                return;
//            }
            // 处理上级sort
            if (levelFirstDict.getSort() == null) {
                final String parentId = levelFirstDict.getParentId();
                final Integer maxSort = Convert.toInt(parentIdByMaxSortMap.get(parentId), 0);
                final int newMaxSort = maxSort + 1;
                parentIdByMaxSortMap.put(parentId, newMaxSort);
                levelFirstDict.setSort(newMaxSort);
            }
        }, (levelFirstDict, levelSecondDictList) -> {
            // 处理下级sort
            for (Dict dict : levelSecondDictList) {
                final String id = dict.getId();
//                if (!addIds.contains(id)) {
//                    continue;
//                }
                dict.setDicLevel(levelFirstDict.getDicLevel() + 1);
                final String parentId = dict.getParentId();
                final Integer maxSort = Convert.toInt(parentIdByMaxSortMap.get(parentId), 0);
                final int newMaxSort = maxSort + 1;
                parentIdByMaxSortMap.put(parentId, newMaxSort);
                dict.setSort(newMaxSort);
            }
        });

        // 批量保存
        if (CollUtil.isNotEmpty(stayAddDictList)) {
            for (List<Dict> addList : CollUtil.split(stayAddDictList, 1000)) {
                baseMapper.insertBatch(addList);
            }
        }
        if (CollUtil.isNotEmpty(stayUpdateDictList)) {
            for (List<Dict> updateList : CollUtil.split(stayUpdateDictList, 1000)) {
                baseMapper.updateBatch(updateList);
            }
        }

        return true;
    }


    /**
     * 从1级开始处理下级
     *
     * @param levelFirstDictList     水平第一dict类型列表
     * @param parentIdGroupByDictMap 父id group by dict地图
     * @param parentDictConsumer     父母dict消费者
     * @param childListConsumer      儿童消费清单
     */
    public void handlerAllOrderByLevelAsc(List<Dict> levelFirstDictList,
                                          Map<String, List<Dict>> parentIdGroupByDictMap,
                                          Function<Dict, String> fieldFunc,
                                          Consumer<Dict> parentDictConsumer,
                                          BiConsumer<Dict, List<Dict>> childListConsumer) {
        List<Dict> levelNextDictList = new ArrayList<>();
        int isThrowCount = 20;
        while (true) {
            if (isThrowCount == 0) {
                break;
//                throw new RuntimeException("超出循环限制次数");
            }
            for (Dict levelFirstDict : levelFirstDictList) {
                final String levelFirstDictId = fieldFunc.apply(levelFirstDict);
                parentDictConsumer.accept(levelFirstDict);
                if (parentIdGroupByDictMap.containsKey(levelFirstDictId)) {
                    final List<Dict> levelSecondDictList = parentIdGroupByDictMap.get(levelFirstDictId);
                    childListConsumer.accept(levelFirstDict, levelSecondDictList);
                    levelNextDictList.addAll(levelSecondDictList);
                }
            }
            if (CollUtil.isNotEmpty(levelNextDictList)) {
                levelFirstDictList = levelNextDictList;
                levelNextDictList = new ArrayList<>();
            } else {
                break;
            }
            isThrowCount++;
        }
    }


}
