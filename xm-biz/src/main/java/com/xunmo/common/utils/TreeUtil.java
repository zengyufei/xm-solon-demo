package com.xunmo.common.utils;

import cn.hutool.core.util.ReflectUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树 工具类
 *
 * @author zengyufei
 * @date 2022/5/14 16:15
 */
public class TreeUtil {


    /**
     * 构建树结构数据集合（根据字段最小值构建根节点）
     *
     * @param list  集合
     * @param field 构建根节点的字段（按最小值构建）
     * @param <T>   泛型
     * @return 树机构数据集合
     */
    public static <T> List<T> build4rootField(List<T> list, String field) {
        if (Objects.isNull(field) || "".equals(field.trim())) {
            throw new RuntimeException("字段不能为空");
        }

        return build(list, l -> {
            Set<Object> valSet = new HashSet<>(l.size());

            // 字段值为空的集合
            List<T> rootList = l.stream()
                    .filter(item -> {
                        Object val = ReflectUtil.getFieldValue(item, field);
                        valSet.add(val);
                        return Objects.isNull(val);
                    })
                    .collect(Collectors.toList());
            if (!rootList.isEmpty()) {
                return rootList;
            }

            // 最小值
            Object minVal = valSet.stream().min(new TreeNodeComparator<>(null)).orElse(null);

            return l.stream()
                    .filter(item -> {
                        Object val = ReflectUtil.getFieldValue(item, field);
                        return Objects.equals(minVal, val);
                    })
                    .collect(Collectors.toList());
        }, TreeNodeConfig.DEFAULT_CONFIG);
    }


    /**
     * 构建树结构数据集合
     *
     * @param list        集合
     * @param getRootList 获取根节点集合方法
     * @param nodeConfig  节点配置
     * @param <T>         泛型
     * @return 树机构数据集合
     */
    public static <T> List<T> build(List<T> list,
                                    Function<List<T>, List<T>> getRootList,
                                    TreeNodeConfig<T> nodeConfig) {
        // 判断集合是否为空
        if (Objects.isNull(list) || list.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取根集合
        List<T> rootList = getRootList.apply(list);
        if (Objects.isNull(rootList) || rootList.isEmpty()) {
            return new ArrayList<>();
        }

        // 容错处理
        if (Objects.isNull(nodeConfig)) {
            nodeConfig = TreeNodeConfig.DEFAULT_CONFIG;
        }

        // 遍历根集合，添加其子集合
        TreeNodeConfig<T> finalNodeConfig = nodeConfig;
        rootList.forEach(root -> {
            List<T> children = getChildren(root, list, finalNodeConfig, finalNodeConfig.getDeep());
            ReflectUtil.setFieldValue(root, finalNodeConfig.getChildrenKey(), children);
        });

        return rootList.stream()
                .sorted(Objects.nonNull(nodeConfig.getComparator()) ? nodeConfig.getComparator()
                        : new TreeNodeComparator<>(nodeConfig.getSortKey())
                )
                .collect(Collectors.toList());
    }


    /**
     * 获取子集合
     *
     * @param parent     父节点
     * @param list       集合
     * @param nodeConfig 节点配置
     * @param deep       递归层级
     * @param <T>        泛型
     * @return 子集合
     */
    public static <T> List<T> getChildren(T parent, List<T> list, TreeNodeConfig<T> nodeConfig, Integer deep) {
        // 校验
        if (Objects.isNull(parent)
                || Objects.isNull(list) || list.isEmpty()
                || Objects.isNull(nodeConfig)) {
            return new ArrayList<>();
        }

        // 递归层级小于等于0，直接返回空
        if (Objects.nonNull(deep) && deep <= 0) {
            return null;
        }

        // 父节点ID
        Object id = ReflectUtil.getFieldValue(parent, nodeConfig.getIdKey());

        // 子集合
        List<T> children = new ArrayList<>();

        // 遍历集合
        list.forEach(node -> {
            // 该节点的父ID
            Object parentId = ReflectUtil.getFieldValue(node, nodeConfig.getParentIdKey());
            // 该节点的父ID是否和父ID一致，不一致直接结束
            if (!Objects.equals(id, parentId)) {
                return;
            }
            // 设置该节点的子集合
            ReflectUtil.setFieldValue(node, nodeConfig.getChildrenKey(), getChildren(node, list, nodeConfig, Objects.isNull(deep) ? null : deep - 1));
            // 放入子集合
            children.add(node);
        });

        return children.stream()
                .sorted(Objects.nonNull(nodeConfig.getComparator()) ? nodeConfig.getComparator()
                        : new TreeNodeComparator<>(nodeConfig.getSortKey())
                )
                .collect(Collectors.toList());
    }


    /**
     * 获取所有节点集合
     *
     * @param tree       树
     * @param nodeConfig 节点配置
     * @param <T>        泛型
     * @return 所有节点集合
     */
    public static <T> List<T> getAllNode(List<T> tree, TreeNodeConfig<T> nodeConfig) {
        if (Objects.isNull(tree) || tree.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> list = new ArrayList<>();
        tree.forEach(item -> push2list(list, item, nodeConfig));
        return list;
    }

    /**
     * 把节点放入集合中（递归）
     *
     * @param list       集合
     * @param node       节点
     * @param nodeConfig 节点配置
     * @param <T>        泛型
     */
    public static <T> void push2list(List<T> list, T node, TreeNodeConfig<T> nodeConfig) {
        if (Objects.isNull(node)) {
            return;
        }

        list.add(node);

        // 是否有子集合
        if (Objects.isNull(nodeConfig)
                || Objects.isNull(nodeConfig.getChildrenKey())
                || "".equals(nodeConfig.getChildrenKey().trim())
        ) {
            return;
        }

        // 获取子集合
        List<T> children = (List<T>) ReflectUtil.getFieldValue(node, nodeConfig.getChildrenKey());
        if (Objects.isNull(children) || children.isEmpty()) {
            return;
        }

        // 父ID值
        Object parentId = (Objects.isNull(nodeConfig.getIdKey()) || "".equals(nodeConfig.getIdKey().trim())) ? null :
                ReflectUtil.getFieldValue(node, nodeConfig.getIdKey().trim());

        // 遍历子集合
        children.forEach(item -> {
            if (Objects.nonNull(parentId)
                    && Objects.nonNull(nodeConfig.getParentIdKey()) && !"".equals(nodeConfig.getParentIdKey().trim())) {
                ReflectUtil.setFieldValue(item, nodeConfig.getParentIdKey().trim(), parentId);
            }
            push2list(list, node, nodeConfig);
        });
    }


    /**
     * 树节点配置
     */
    @Data
    @Accessors(chain = true)
    public static class TreeNodeConfig<T> {
        public static TreeNodeConfig DEFAULT_CONFIG = new TreeNodeConfig();
        private String idKey = "id";
        private String parentIdKey = "parentId";
        private String childrenKey = "children";
        private String sortKey = "sort";
        /**
         * 递归层级，为空时无层级限制
         */
        private Integer deep;
        /**
         * 对比器，为空时使用树系欸但通用对比器{@link TreeNodeComparator}
         */
        private Comparator<T> comparator;
    }

    /**
     * 树节点通用对比器
     * <p>
     * a, b
     * 返回结果说明：
     * -1: a小于b
     * 0:  a等于b
     * 1:  d大于b
     *
     * @param <T> 泛型
     */
    @Data
    @AllArgsConstructor
    public static class TreeNodeComparator<T> implements Comparator<T> {
        private String compareField;

        @Override
        public int compare(T o1, T o2) {
            Object v1, v2;
            if (Objects.isNull(compareField) || "".equals(compareField.trim())) {
                v1 = o1;
                v2 = o2;
            } else {
                v1 = ReflectUtil.getFieldValue(o1, compareField.trim());
                v2 = ReflectUtil.getFieldValue(o2, compareField.trim());
            }

            // 一致
            if (Objects.equals(v1, v2)) {
                return 0;
            }

            if (Objects.isNull(v1)) {
                return 1;
            }
            if (Objects.isNull(v2)) {
                return -1;
            }

            if (v1 instanceof Integer) {
                return Integer.compare((Integer) v1, (Integer) v2);
            } else if (v1 instanceof Float) {
                return Float.compare((Float) v1, (Float) v2);
            } else if (v1 instanceof Long) {
                return Long.compare((Long) v1, (Long) v2);
            } else if (v1 instanceof BigInteger) {
                return ((BigInteger) v1).compareTo((BigInteger) v2);
            } else if (v1 instanceof BigDecimal) {
                return ((BigDecimal) v1).compareTo((BigDecimal) v2);
            } else if (v1 instanceof Date) {
                return Long.compare(((Date) v1).getTime(), ((Date) v2).getTime());
            } else {
                return v1.toString().compareTo(v2.toString());
            }
        }
    }

}
