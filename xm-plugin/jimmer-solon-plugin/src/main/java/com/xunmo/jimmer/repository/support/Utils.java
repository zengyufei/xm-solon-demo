package com.xunmo.jimmer.repository.support;

import com.xunmo.jimmer.page.Sort;
import com.xunmo.jimmer.repository.SpringConnectionManager;
import com.xunmo.jimmer.repository.SpringTransientResolverProvider;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.query.OrderMode;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.PropExpressionImplementor;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.meta.EmbeddedColumns;
import org.babyfish.jimmer.sql.meta.MetadataStrategy;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Utils {

    private Utils() {
    }

    public static <E> Collection<E> toCollection(Iterable<E> iterable) {
        if (iterable instanceof Collection<?>) {
            return (Collection<E>) iterable;
        }
        if (iterable == null) {
            return Collections.emptyList();
        }
        List<E> list = new ArrayList<>();
        for (E e : iterable) {
            list.add(e);
        }
        return list;
    }

    public static JSqlClientImplementor validateSqlClient(JSqlClient sqlClient) {
        JSqlClientImplementor implementor = (JSqlClientImplementor) sqlClient;
        if (!(implementor.getConnectionManager() instanceof SpringConnectionManager)) {
            throw new IllegalArgumentException(
                    "The connection manager of sql client must be instance of \"" +
                            SpringConnectionManager.class.getName() +
                            "\""
            );
        }
        if (!SpringTransientResolverProvider.class.isAssignableFrom(implementor.getResolverProviderClass())) {
            throw new IllegalArgumentException(
                    "The transient resolver provider of sql client must be instance of \"" +
                            SpringConnectionManager.class.getName() +
                            "\""
            );
        }
        ConnectionManager slaveConnectionManager = implementor.getSlaveConnectionManager(false);
        if (slaveConnectionManager != null && !(slaveConnectionManager instanceof SpringConnectionManager)) {
            throw new IllegalArgumentException(
                    "The slave connection manager of sql client must be null or instance of \"" +
                            SpringConnectionManager.class.getName() +
                            "\""
            );
        }
        return implementor;
    }

    public static Sort toSort(List<Order> orders, MetadataStrategy strategy) {
        if (orders == null || orders.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> springOrders = new ArrayList<>(orders.size());
        for (Order order : orders) {
            if (order.getExpression() instanceof PropExpression<?>) {
                PropExpressionImplementor<?> propExpr = (PropExpressionImplementor<?>) order.getExpression();
                String prefix = prefix(propExpr.getTable());
                EmbeddedColumns.Partial partial = propExpr.getPartial(strategy);
                String path = partial != null ? partial.path() : propExpr.getProp().getName();
                if (prefix != null) {
                    path = prefix + '.' + path;
                }
                Sort.NullHandling nullHandling;
                switch (order.getNullOrderMode()) {
                    case NULLS_FIRST:
                        nullHandling = Sort.NullHandling.NULLS_FIRST;
                        break;
                    case NULLS_LAST:
                        nullHandling = Sort.NullHandling.NULLS_LAST;
                        break;
                    default:
                        nullHandling = Sort.NullHandling.NATIVE;
                        break;
                }
                springOrders.add(
                        new Sort.Order(
                                order.getOrderMode() == OrderMode.DESC ?
                                        Sort.Direction.DESC :
                                        Sort.Direction.ASC,
                                path,
                                nullHandling
                        )
                );
            }
        }
        return Sort.by(springOrders);
    }

    private static String prefix(Table<?> table) {
        ImmutableProp prop = table instanceof TableProxy<?> ?
                ((TableProxy<?>) table).__prop() :
                ((TableImplementor<?>) table).getJoinProp();
        if (prop == null) {
            return null;
        }

        String name = prop.getName();

        boolean inverse = table instanceof TableProxy<?> ?
                ((TableProxy<?>) table).__isInverse() :
                ((TableImplementor<?>) table).isInverse();
        if (inverse) {
            name = "`←" + name + '`';
        }

        Table<?> parent = table instanceof TableProxy<?> ?
                ((TableProxy<?>) table).__parent() :
                ((TableImplementor<?>) table).getParent();
        if (parent == null) {
            return name;
        }
        String parentPrefix = prefix(parent);
        if (parentPrefix == null) {
            return name;
        }
        return parentPrefix + '.' + name;
    }
}
