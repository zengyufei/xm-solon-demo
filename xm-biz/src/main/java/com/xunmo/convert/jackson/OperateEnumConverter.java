package com.xunmo.convert.jackson;// package com.pj.admin.jackson;

//
// import com.fasterxml.jackson.databind.JavaType;
// import com.fasterxml.jackson.databind.type.TypeFactory;
// import com.fasterxml.jackson.databind.util.Converter;
// import com.pj.admin.enums.OperateEnum;
//
/// **
// * @author Evan
// */
// public class OperateEnumConverter implements Converter<String, OperateEnum> {
//
// @Override
// public OperateEnum convert(String value) {
// return OperateEnum.of(value);
// }
//
// /**
// * 输入值类型
// * @param typeFactory
// * @return
// */
// @Override
// public JavaType getInputType(TypeFactory typeFactory) {
// return typeFactory.constructType(String.class);
// }
//
// /**
// * 转换器输出值类型
// * @param typeFactory
// * @return
// */
// @Override
// public JavaType getOutputType(TypeFactory typeFactory) {
// return typeFactory.constructType(OperateEnum.class);
// }
// }
