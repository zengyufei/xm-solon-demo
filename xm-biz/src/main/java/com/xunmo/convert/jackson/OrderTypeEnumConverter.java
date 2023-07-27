package com.xunmo.convert.jackson;//package com.pj.admin.jackson;
//
//import com.fasterxml.jackson.databind.JavaType;
//import com.fasterxml.jackson.databind.type.TypeFactory;
//import com.fasterxml.jackson.databind.util.Converter;
//import com.pj.admin.enums.OrderTypeEnum;
//
///**
// * @author Evan
// */
//public class OrderTypeEnumConverter implements Converter<String, OrderTypeEnum> {
//
//    @Override
//    public OrderTypeEnum convert(String value) {
//        return OrderTypeEnum.of(value);
//    }
//
//    /**
//     * 输入值类型
//     * @param typeFactory
//     * @return
//     */
//    @Override
//    public JavaType getInputType(TypeFactory typeFactory) {
//        return typeFactory.constructType(String.class);
//    }
//
//    /**
//     * 转换器输出值类型
//     * @param typeFactory
//     * @return
//     */
//    @Override
//    public JavaType getOutputType(TypeFactory typeFactory) {
//        return typeFactory.constructType(OrderTypeEnum.class);
//    }
//}
