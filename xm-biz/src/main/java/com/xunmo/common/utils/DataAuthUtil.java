package com.xunmo.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.xunmo.core.utils.LamUtil;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.update.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataAuthUtil {


    public static PlainSelect parsePlainSelect(String sql) {
        PlainSelect sourcePlainSelect;
        try {
            final Statement sourceSqlStatement = CCJSqlParserUtil.parse(sql);
            Select sourceSelect = (Select) sourceSqlStatement;
            sourcePlainSelect = (PlainSelect) sourceSelect.getSelectBody();
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        return sourcePlainSelect;
    }


    /**
     * 获取值类型
     *
     * @param value 价值
     * @return {@link EqualsTo}
     */
    public static Expression getStringValue(String value) {
        return new StringValue(value);
    }

    /**
     * 获取 X=Y
     *
     * @param alias     别名
     * @param fieldName 字段名
     * @param value     价值
     * @return {@link EqualsTo}
     */
    public static EqualsTo getEqualsToString(final String alias, String fieldName, String value) {
        final Column left = DataAuthUtil.getColumn(alias, fieldName);
        return new EqualsTo(left, new StringValue(value));
    }

    /**
     * 获取左列名
     *
     * @param alias     别名
     * @param fieldName 字段名
     * @return {@link Column}
     */
    public static String getColumnStr(final String alias, String fieldName) {
        String left = fieldName;
        if (StrUtil.isNotBlank(alias)) {
            left = alias + "." + fieldName;
        }
        return left;
    }

    /**
     * 获取左列名
     *
     * @param alias     别名
     * @param fieldName 字段名
     * @return {@link Column}
     */
    public static Column getColumn(final String alias, String fieldName) {
        Column left = new Column(fieldName);
        if (StrUtil.isNotBlank(alias)) {
            left = new Column(alias + "." + fieldName);
        }
        return left;
    }

    /**
     * 获取 exists 语句
     *
     * @param sql sql
     * @return {@link ExistsExpression}
     */
    public static ExistsExpression getExistsExpression(String sql) {
        final ExistsExpression existsExpression = new ExistsExpression();
        try {
            final Statement parse = CCJSqlParserUtil.parse(sql);
            Select select = (Select) parse;
            SelectBody selectBody = select.getSelectBody();
            final SubSelect subSelect = new SubSelect();
            subSelect.setSelectBody(selectBody);
            existsExpression.setRightExpression(subSelect);
        } catch (JSQLParserException e) {
            throw new RuntimeException(e);
        }
        return existsExpression;
    }

    /**
     * 括号包起来
     *
     * @param expression 表达式
     * @return {@link Expression}
     */
    public static Expression pack(Expression expression) {
        return new Parenthesis(expression);
    }


    /**
     * where条件是否存在某字段
     *
     * @param plainSelectWhere 简单选择
     * @param fieldName        字段名
     * @return boolean
     */
    public static boolean hasFieldForWhere(Expression plainSelectWhere, String fieldName) {
        final boolean[] isExistsBuildUnitId = {false};
        plainSelectWhere.accept(new ExpressionVisitorAdapter() {
            @Override
            protected void visitBinaryExpression(BinaryExpression expr) {
                if (!isExistsBuildUnitId[0]) {
                    isExistsBuildUnitId[0] = StrUtil.equalsIgnoreCase(expr.getLeftExpression().toString(), fieldName);
                }
                super.visitBinaryExpression(expr);
            }
        });
        return isExistsBuildUnitId[0];
    }


    /**
     * 设置where条件
     *
     * @param statement  声明
     * @param expression 表达式
     */
    public static void setStatementWhere(Statement statement, Expression expression) {
        if (statement instanceof Update) {
            final Update update = (Update) statement;
            update.setWhere(expression);
        }
        if (statement instanceof Delete) {
            final Delete delete = (Delete) statement;
            delete.setWhere(expression);
        }
    }

    public static void setWhereIfAnd(Statement statement, Expression expression, boolean isPack) {
        final Expression plainSelectWhere = DataAuthUtil.getWhere(statement);
        Expression tempExpression = expression;
        // 与sql拼接
        if (null == plainSelectWhere) {
            if (isPack) {
                // 不存在 where 条件
                tempExpression = DataAuthUtil.pack(expression);
            }
        } else {
            // 存在 where 条件 and 处理
            if (isPack) {
                tempExpression = DataAuthUtil.pack(new AndExpression(plainSelectWhere, expression));
            } else {
                tempExpression = new AndExpression(plainSelectWhere, expression);
            }
        }
        DataAuthUtil.setStatementWhere(statement, tempExpression);
    }

    public static void setWhereIfAnd(PlainSelect plainSelect, Expression expression) {
        setWhereIfAnd(plainSelect, expression, true);
    }

    public static void setNoAuthQuery(PlainSelect plainSelect) {
        DataAuthUtil.setWhereIfAnd(plainSelect, DataAuthUtil.getEqualsToString(null, "1", "2"));
    }

    public static void setNoAuthQuery(Statement statement) {
        DataAuthUtil.setWhereIfAnd(statement, DataAuthUtil.getEqualsToString(null, "1", "2"), false);
    }

    public static void setWhereIfAnd(PlainSelect plainSelect, Expression expression, boolean isPack) {
        final Expression plainSelectWhere = plainSelect.getWhere();
        Expression tempExpression = expression;
        // 与sql拼接
        if (null == plainSelectWhere) {
            // 不存在 where 条件
            if (isPack) {
                tempExpression = DataAuthUtil.pack(expression);
            }
            plainSelect.setWhere(tempExpression);
        } else {
            // 存在 where 条件 and 处理
            if (isPack) {
                tempExpression = DataAuthUtil.pack(new AndExpression(plainSelectWhere, expression));
            } else {
                tempExpression = new AndExpression(plainSelectWhere, expression);
            }
        }
        plainSelect.setWhere(tempExpression);
    }

    public static void setIn(PlainSelect plainSelect, String columnName, List<String> values) {
        Expression expression = getExpression(columnName, values);
        if (expression != null) {
            // 拼接权限语句
            DataAuthUtil.setWhereIfAnd(plainSelect, expression, true);
        }
    }

    public static void setIn(Statement statement, String columnName, List<String> values) {
        Expression expression = getExpression(columnName, values);
        if (expression != null) {
            // 拼接权限语句
            DataAuthUtil.setWhereIfAnd(statement, expression, true);
        }
    }

    protected static Expression getExpression(String columnName, List<String> values) {
            Expression expression = null;
        List<ExpressionList> expressionListList = new ArrayList<>(); // 创建一个空的ExpressionList列表，用于存储后面需要构建的ExpressionList

        if (CollUtil.isNotEmpty(values)) { // 如果values不为空
            // 使用CollUtil.split方法将原有的values拆分成长度不超过1000的多个子数组，遍历这些子数组，将每个子数组构建成ExpressionList并添加进expressionListList中
            CollUtil.split(values, 1000).forEach(list -> expressionListList.add(new ExpressionList(XmUtil.listFiltersBlankDistinctMapToList(list, StringValue::new))));
            if (CollUtil.isNotEmpty(expressionListList)) { // 如果 expressionListList 不为空
                List<Expression> inExpressionList = expressionListList.stream() // 将 expressionListList 转换成 Expression 列表
                        .map(expressionList -> new InExpression(new Column(columnName), expressionList)) // 针对每个 ExpressionList 新建 InExpression
                        .collect(Collectors.toList()); // 将上一步操作得到的 InExpression 列表转换成 List<Expression> 类型
                expression = inExpressionList.stream().reduce(OrExpression::new).orElse(null); // 将 InExpression 列表使用 OrExpression 进行合并
            }
        }
        return new Parenthesis(expression); // 返回构建完成的条件表达式加上圆括号
    }

    public static void setOr(PlainSelect plainSelect, Expression expression, boolean isPack) {
        final Expression plainSelectWhere = plainSelect.getWhere();
        // 与sql拼接
        if (null == plainSelectWhere) {
            Expression tempExpression = expression;
            if (isPack) {
                tempExpression = DataAuthUtil.pack(expression);
            }
            // 不存在 where 条件
            plainSelect.setWhere(tempExpression);
        } else {
            Expression tempExpression;
            if (isPack) {
                tempExpression = DataAuthUtil.pack(new OrExpression(plainSelectWhere, expression));
            } else {
                tempExpression = new OrExpression(plainSelectWhere, expression);
            }
            // 存在 where 条件 and 处理
            plainSelect.setWhere(tempExpression);
        }
    }

    /**
     * 拼接 XX = YY and ZZ = BB
     *
     * @param expression1 表达式1
     * @param expression2 表达式2
     * @return {@link AndExpression}
     */
    public static AndExpression appendAnd(Expression expression1, Expression expression2) {
        return new AndExpression(expression1, expression2);
    }

    public static Expression getWhere(Statement statement) {
        Expression plainSelectWhere = null;
        if (statement instanceof Update) {
            plainSelectWhere = ((Update) statement).getWhere();
        } else if (statement instanceof Delete) {
            plainSelectWhere = ((Delete) statement).getWhere();
        }
        return plainSelectWhere;
    }
}
