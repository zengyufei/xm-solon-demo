package com.xunmo.common.base;

import com.xunmo.jimmer.page.Page;
import com.xunmo.jimmer.page.PageImpl;
import com.xunmo.jimmer.page.PageRequest;
import com.xunmo.jimmer.page.Sort;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.impl.query.ConfigurableRootQueryImplementor;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.query.ConfigurableRootQuery;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.query.OrderMode;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.PropExpressionImplementor;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.meta.EmbeddedColumns;
import org.babyfish.jimmer.sql.meta.MetadataStrategy;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseController {

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
				springOrders.add(new Sort.Order(
						order.getOrderMode() == OrderMode.DESC ? Sort.Direction.DESC : Sort.Direction.ASC, path,
						nullHandling));
			}
		}
		return Sort.by(springOrders);
	}

	private static String prefix(Table<?> table) {
		ImmutableProp prop = table instanceof TableProxy<?> ? ((TableProxy<?>) table).__prop()
				: ((TableImplementor<?>) table).getJoinProp();
		if (prop == null) {
			return null;
		}

		String name = prop.getName();

		boolean inverse = table instanceof TableProxy<?> ? ((TableProxy<?>) table).__isInverse()
				: ((TableImplementor<?>) table).isInverse();
		if (inverse) {
			name = "`←" + name + '`';
		}

		Table<?> parent = table instanceof TableProxy<?> ? ((TableProxy<?>) table).__parent()
				: ((TableImplementor<?>) table).getParent();
		if (parent == null) {
			return name;
		}
		String parentPrefix = prefix(parent);
		if (parentPrefix == null) {
			return name;
		}
		return parentPrefix + '.' + name;
	}

	protected Pager pager(PageRequest pageable) {
		return new PagerImpl(pageable);
	}

	protected Pager pager(int pageIndex, int pageSize) {
		return new PagerImpl(pageIndex, pageSize);
	}

	public static class PagerImpl extends Pager {

		private final Integer pageIndex;

		private final Integer pageSize;

		PagerImpl(PageRequest pageRequest) {
			this.pageIndex = pageRequest.getPageNumber();
			this.pageSize = pageRequest.getPageSize();
		}

		PagerImpl(Integer pageIndex, Integer pageSize) {
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
		}

		@Override
		public <T> Page<T> execute(ConfigurableRootQuery<?, T> query) {
			if (pageSize == null || pageSize == 0) {
				return new PageImpl<>(query.execute());
			}
			long offset = (long) pageIndex * pageSize;
			if (offset > Integer.MAX_VALUE - pageSize) {
				throw new IllegalArgumentException("offset is too big");
			}
			int total = query.count();
			List<T> content = query.limit(pageSize, (int) offset).execute();
			ConfigurableRootQueryImplementor<?, ?> queryImplementor = (ConfigurableRootQueryImplementor<?, ?>) query;
			return new PageImpl<>(content, PageRequest.of(pageIndex, pageSize,
					toSort(queryImplementor.getOrders(), queryImplementor.getSqlClient().getMetadataStrategy())),
					total);
		}

	}

	public abstract static class Pager {

		public abstract <T> Page<T> execute(ConfigurableRootQuery<?, T> query);

	}

}
