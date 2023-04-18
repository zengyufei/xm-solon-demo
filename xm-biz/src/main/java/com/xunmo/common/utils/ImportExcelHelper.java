package com.xunmo.common.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.alibaba.excel.metadata.CellExtra;
import com.xunmo.common.CustomException;
import com.xunmo.common.enums.SystemStatus;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.core.handle.UploadedFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 导入excel工具类
 *
 * @param <T>
 * @author wenjinhang
 * @date 2022/08/27
 */
@Slf4j
public class ImportExcelHelper<T> {

    /**
     * 返回解析后的List
     * 试试不开线程的情况
     *
     * @param inputStream
     * @param sheetNo
     * @param headRowNumber
     * @return
     * @throws Exception
     * @author feitan
     * @since v1.0 2022/12/12 15:06
     */
    public List<T> getListSingleThread(InputStream inputStream, Integer sheetNo, Integer headRowNumber, Integer endRowNumber) throws Exception {
        ImportExcelListener<T> listener = new ImportExcelListener<>(headRowNumber, endRowNumber);

        EasyExcel.read(inputStream, listener)
                .autoTrim(true)
                .extraRead(CellExtraTypeEnum.MERGE)
                .sheet(sheetNo)
                .headRowNumber(headRowNumber)
                .doRead();

        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        //没有合并单元格情况，直接返回即可
        if (isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }

        AtomicReference<List<T>> data = new AtomicReference<>();

        //存在有合并单元格时，自动获取值，并校对
        data.set(explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber));

        return data.get();
    }

    /**
     * 返回解析后的List
     * 试试不开线程的情况
     *
     * @param inputStream
     * @param clazz
     * @param sheetNo
     * @param headRowNumber
     * @return
     * @throws Exception
     * @author feitan
     * @since v1.0 2022/12/12 15:06
     */
    public List<T> getListSingleThread(InputStream inputStream, Class<T> clazz, Integer sheetNo, Integer headRowNumber) throws Exception {
        ImportExcelListener<T> listener = new ImportExcelListener<>(headRowNumber);

        EasyExcel.read(inputStream, clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();

        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        //没有合并单元格情况，直接返回即可
        if (isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }

        AtomicReference<List<T>> data = new AtomicReference<>();

        //存在有合并单元格时，自动获取值，并校对
        data.set(explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber));

        return data.get();
    }


    /**
     * 返回解析后的List
     * 试试不开线程的情况
     *
     * @return java.util.List<T> 解析后的List
     * @author feitan
     * @date 2022/10/22 15:17
     * @param: fileName 文件名
     * @param: clazz Excel对应属性名
     * @param: sheetNo 要解析的sheet
     * @param: headRowNumber 正文起始行
     */
    public List<T> getListSingleThread(UploadedFile file, Class<T> clazz, Integer sheetNo, Integer headRowNumber) throws Exception {
        ImportExcelListener<T> listener = new ImportExcelListener<>(headRowNumber);

        EasyExcel.read(file.getContent(), clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();

        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        //没有合并单元格情况，直接返回即可
        if (isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }

        AtomicReference<List<T>> data = new AtomicReference<>();

        //存在有合并单元格时，自动获取值，并校对
        data.set(explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber));

        return data.get();
    }


    /**
     * 返回解析后的List
     *
     * @return java.util.List<T> 解析后的List
     * @param: fileName 文件名
     * @param: clazz Excel对应属性名
     * @param: sheetNo 要解析的sheet
     * @param: headRowNumber 正文起始行
     */
    public List<T> getList(UploadedFile file, Class<T> clazz, Integer sheetNo, Integer headRowNumber) {
        ImportExcelListener<T> listener = new ImportExcelListener<>(headRowNumber);
        try {
            EasyExcel.read(file.getContent(), clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        //没有合并单元格情况，直接返回即可
        if (isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }
        AtomicReference<List<T>> data = new AtomicReference<>();
        //存在有合并单元格时，自动获取值，并校对
        data.set(explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber));
        return data.get();
    }


    /**
     * 返回解析后的List
     *
     * @return java.util.List<T> 解析后的List
     * @param: fileName 文件名
     * @param: clazz Excel对应属性名
     * @param: sheetNo 要解析的sheet
     * @param: headRowNumber 正文起始行
     */
    public List<T> getListByInputStream(FileInputStream file, Class<T> clazz, Integer sheetNo, Integer headRowNumber) {
        ImportExcelListener<T> listener = new ImportExcelListener<>(headRowNumber);
        try {
            EasyExcel.read(file, clazz, listener).extraRead(CellExtraTypeEnum.MERGE).sheet(sheetNo).headRowNumber(headRowNumber).doRead();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        List<CellExtra> extraMergeInfoList = listener.getExtraMergeInfoList();
        //没有合并单元格情况，直接返回即可
        if (isEmpty(extraMergeInfoList)) {
            return listener.getData();
        }
        AtomicReference<List<T>> data = new AtomicReference<>();
        //存在有合并单元格时，自动获取值，并校对
        try {
            data.set(explainMergeData(listener.getData(), extraMergeInfoList, headRowNumber));
        } catch (Exception e) {
            throw new RuntimeException("模板错误");
        }
        return data.get();
    }

    /**
     * 处理合并单元格
     *
     * @param data               解析数据
     * @param extraMergeInfoList 合并单元格信息
     * @param headRowNumber      起始行
     * @return 填充好的解析数据
     */
    private List<T> explainMergeData(List<T> data, List<CellExtra> extraMergeInfoList, Integer headRowNumber) {
        final int size = data.size();
        //循环所有合并单元格信息
        extraMergeInfoList.forEach(cellExtra -> {
            int firstRowIndex = cellExtra.getFirstRowIndex() - headRowNumber;
            int lastRowIndex = cellExtra.getLastRowIndex() - headRowNumber;
            int firstColumnIndex = cellExtra.getFirstColumnIndex();
            int lastColumnIndex = cellExtra.getLastColumnIndex();
            if (firstRowIndex > size - 1) {
                return;
            }
            if (lastRowIndex > size - 1) {
                lastRowIndex = size - 1;
            }
            //获取初始值
            Object initValue = getInitValueFromList(firstRowIndex, firstColumnIndex, data);
            //设置值
            for (int i = firstRowIndex; i <= lastRowIndex; i++) {
                for (int j = firstColumnIndex; j <= lastColumnIndex; j++) {
                    setInitValueToList(initValue, i, j, data);
                }
            }
        });
        return data;
    }

    /**
     * 设置合并单元格的值
     *
     * @param filedValue  值
     * @param rowIndex    行
     * @param columnIndex 列
     * @param data        解析数据
     */
    private void setInitValueToList(Object filedValue, Integer rowIndex, Integer columnIndex, List<T> data) {
        T object = data.get(rowIndex);
        if (object instanceof Map) {
            final Map<Integer, String> map = (Map<Integer, String>) object;
            final Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
            for (Map.Entry<Integer, String> entry : entrySet) {
                final Integer key = entry.getKey();
                if (key.equals(columnIndex)) {
                    map.put(key, (String) filedValue);
                    break;
                }
            }
        } else {
            for (Field field : object.getClass().getDeclaredFields()) {
                //提升反射性能，关闭安全检查
                field.setAccessible(true);
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                if (annotation != null) {
                    if (annotation.index() == columnIndex) {
                        try {
                            field.set(object, filedValue);
                            break;
                        } catch (IllegalAccessException e) {
                            log.error("设置合并单元格的值异常：{}", e.getMessage());
                        }
                    }
                }
            }
        }
    }


    /**
     * 获取合并单元格的初始值
     * rowIndex对应list的索引
     * columnIndex对应实体内的字段
     *
     * @param firstRowIndex    起始行
     * @param firstColumnIndex 起始列
     * @param data             列数据
     * @return 初始值
     */
    private Object getInitValueFromList(Integer firstRowIndex, Integer firstColumnIndex, List<T> data) {
        Object filedValue = null;
        T object = data.get(firstRowIndex);
        if (object instanceof Map) {
            final Map<Integer, String> map = (Map<Integer, String>) object;
            final Set<Map.Entry<Integer, String>> entrySet = map.entrySet();
            for (Map.Entry<Integer, String> entry : entrySet) {
                final Integer key = entry.getKey();
                if (key.equals(firstColumnIndex)) {
                    filedValue = map.get(key);
                    break;
                }
            }
        } else {
            for (Field field : object.getClass().getDeclaredFields()) {
                //提升反射性能，关闭安全检查
                field.setAccessible(true);
                ExcelProperty annotation = field.getAnnotation(ExcelProperty.class);
                if (annotation != null) {
                    if (annotation.index() == firstColumnIndex) {
                        try {
                            filedValue = field.get(object);
                            break;
                        } catch (IllegalAccessException e) {
                            log.error("设置合并单元格的初始值异常：{}", e.getMessage());
                        }
                    }
                }
            }
        }
        return filedValue;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    private boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    /**
     * 检查上传文件存在及后缀
     *
     * @param file 文件
     * @throws CustomException 自定义异常
     */
    public static void checkFile(UploadedFile file) throws CustomException {
        XmUtil.throwError(Objects.isNull(file), SystemStatus.NO_MULTIPART_FILE::getMsg);
        String fileExtension = file.getExtension();
        if (StrUtil.isBlank(fileExtension)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "只能上传有后缀的文件");
        }
        if (!"xls".equals(fileExtension) && !"xlsx".equals(fileExtension)) {
            throw new CustomException(SystemStatus.IN_VALIDA_PARAM, "只能上传格式为xls或xlsx的文件");
        }
    }

}
