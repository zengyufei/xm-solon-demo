package com.xunmo.webs.user;

import cn.hutool.core.collection.CollUtil;
import com.xunmo.common.base.JRepository;
import com.xunmo.common.entity.page.*;
import org.babyfish.jimmer.ImmutableObjects;
import org.babyfish.jimmer.Input;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.PropExpression;
import org.babyfish.jimmer.sql.ast.impl.mutation.Mutations;
import org.babyfish.jimmer.sql.ast.impl.query.MutableRootQueryImpl;
import org.babyfish.jimmer.sql.ast.impl.table.TableImplementor;
import org.babyfish.jimmer.sql.ast.mutation.*;
import org.babyfish.jimmer.sql.ast.query.ConfigurableRootQuery;
import org.babyfish.jimmer.sql.ast.query.Order;
import org.babyfish.jimmer.sql.ast.query.OrderMode;
import org.babyfish.jimmer.sql.ast.query.PagingQueries;
import org.babyfish.jimmer.sql.ast.table.Table;
import org.babyfish.jimmer.sql.ast.table.spi.PropExpressionImplementor;
import org.babyfish.jimmer.sql.ast.table.spi.TableProxy;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.babyfish.jimmer.sql.meta.EmbeddedColumns;
import org.babyfish.jimmer.sql.meta.MetadataStrategy;
import org.babyfish.jimmer.sql.runtime.ExecutionPurpose;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;
import org.jetbrains.annotations.NotNull;
import org.noear.solon.core.util.GenericUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JRepositoryImpl<E, ID> implements JRepository<E, ID> {

	private static final TypedProp.Scalar<?, ?>[] EMPTY_SORTED_PROPS = new TypedProp.Scalar[0];

	protected final JSqlClientImplementor sqlClient;

	protected final Class<E> entityType;

	protected final ImmutableType immutableType;

	protected JRepositoryImpl(JSqlClient sqlClient) {
		this(sqlClient, null);
	}

	@SuppressWarnings("unchecked")
	public JRepositoryImpl(JSqlClient sqlClient, Class<E> entityType) {
		this.sqlClient = validateSqlClient(sqlClient);
		if (entityType != null) {
			this.entityType = entityType;
		} else {
			Class<?>[] typeArguments = GenericUtil.resolveTypeArguments(this.getClass(), JRepository.class);
			if (typeArguments == null) {
				throw new IllegalArgumentException(
						"The class \"" + this.getClass() + "\" " +
								"does not explicitly specify the type arguments of \"" +
								JRepository.class.getName() +
								"\" so that the entityType must be specified"
				);
			}
			this.entityType = entityType = (Class<E>) typeArguments[0];
		}
		this.immutableType = ImmutableType.get(entityType);
		if (!immutableType.isEntity()) {
			throw new IllegalArgumentException(
					"\"" +
							entityType +
							"\" is not entity type decorated by @" +
							Entity.class.getName()
			);
		}
	}

	@Override
	public JSqlClient sql() {
		return sqlClient;
	}

	@Override
	public ImmutableType type() {
		return immutableType;
	}

	@Override
	public Class<E> entityType() {
		return entityType;
	}

	@Override
	public Pager pager(Pageable pageable) {
		return new PagerImpl(pageable.getPageNumber(), pageable.getPageSize());
	}

	@Override
	public Pager pager(int pageIndex, int pageSize) {
		return new PagerImpl(pageIndex, pageSize);
	}

	@Override
	public E findNullable(ID id) {
		return sqlClient.getEntities().findById(entityType, id);
	}

	@Override
	public E findNullable(ID id, Fetcher<E> fetcher) {
		if (fetcher == null) {
			return findNullable(id);
		}
		return sqlClient.getEntities().findById(fetcher, id);
	}

	@Override
	public List<E> findByIds(Iterable<ID> ids) {
		return sqlClient.getEntities().findByIds(entityType, CollUtil.toCollection(ids));
	}

	@Override
	public List<E> findByIds(Iterable<ID> ids, Fetcher<E> fetcher) {
		if (fetcher == null) {
			return findByIds(ids);
		}
		return sqlClient.getEntities().findByIds(fetcher, CollUtil.toCollection(ids));
	}

	@Override
	public Map<ID, E> findMapByIds(Iterable<ID> ids) {
		return sqlClient.getEntities().findMapByIds(entityType, CollUtil.toCollection(ids));
	}

	@Override
	public Map<ID, E> findMapByIds(Iterable<ID> ids, Fetcher<E> fetcher) {
		if (fetcher == null) {
			return findMapByIds(ids);
		}
		return sqlClient.getEntities().findMapByIds(fetcher, CollUtil.toCollection(ids));
	}

	@NotNull
	@Override
	public List<E> findAll() {
		return sqlClient.getEntities().findAll(entityType);
	}

	@Override
	public List<E> findAll(TypedProp.Scalar<?, ?>... sortedProps) {
		return sqlClient.getEntities().findAll(entityType, sortedProps);
	}

	@Override
	public List<E> findAll(Fetcher<E> fetcher, TypedProp.Scalar<?, ?>... sortedProps) {
		if (fetcher == null) {
			return findAll(sortedProps);
		}
		return sqlClient.getEntities().findAll(fetcher, sortedProps);
	}

	@NotNull
	@Override
	public List<E> findAll(@NotNull Sort sort) {
		return findAll(null, sort);
	}

	@Override
	public List<E> findAll(Fetcher<E> fetcher, Sort sort) {
		MutableRootQueryImpl<Table<E>> query =
				new MutableRootQueryImpl<>(
						sqlClient,
						immutableType,
						ExecutionPurpose.QUERY,
						false
				);
		Table<E> table = query.getTable();
		query.orderBy(SpringOrders.toOrders(table, sort));
		return query.select(table.fetch(fetcher)).execute();
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize) {
		return findAll(pageIndex, pageSize, null, EMPTY_SORTED_PROPS);
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher) {
		return findAll(pageIndex, pageSize, fetcher, EMPTY_SORTED_PROPS);
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize, TypedProp.Scalar<?, ?>... sortedProps) {
		return findAll(pageIndex, pageSize, null, sortedProps);
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher, TypedProp.Scalar<?, ?>... sortedProps) {
		return pager(pageIndex, pageSize).execute(createQuery(fetcher, sortedProps));
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize, Sort sort) {
		return findAll(PageRequest.of(pageIndex, pageSize, sort), null);
	}

	@Override
	public Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher, Sort sort) {
		return findAll(PageRequest.of(pageIndex, pageSize, sort), fetcher);
	}

	@NotNull
	@Override
	public Page<E> findAll(@NotNull Pageable pageable) {
		return findAll(pageable, null);
	}

	@Override
	public Page<E> findAll(Pageable pageable, Fetcher<E> fetcher) {
		MutableRootQueryImpl<Table<E>> query =
				new MutableRootQueryImpl<>(
						sqlClient,
						immutableType,
						ExecutionPurpose.QUERY,
						false
				);
		Table<E> table = query.getTable();
		query.orderBy(SpringOrders.toOrders(table, pageable.getSort()));
		return pager(pageable).execute(
				query.select(table.fetch(fetcher))
		);
	}

	@Override
	public long count() {
		return createQuery(null, EMPTY_SORTED_PROPS).count();
	}

	@NotNull
	@Override
	public <S extends E> SimpleSaveResult<S> save(@NotNull S entity, SaveMode mode) {
		return sqlClient
				.getEntities()
				.saveCommand(entity)
				.setMode(mode)
				.execute();
	}

	@NotNull
	@Override
	public SimpleEntitySaveCommand<E> saveCommand(@NotNull Input<E> input) {
		return sqlClient.getEntities().saveCommand(input);
	}

	@NotNull
	@Override
	public <S extends E> SimpleEntitySaveCommand<S> saveCommand(@NotNull S entity) {
		return sqlClient.getEntities().saveCommand(entity);
	}

	@NotNull
	@Override
	public <S extends E> BatchSaveResult<S> saveAll(@NotNull Iterable<S> entities, SaveMode mode) {
		return sqlClient
				.getEntities()
				.batchSaveCommand(CollUtil.toCollection(entities))
				.setMode(mode)
				.execute();
	}

	@NotNull
	@Override
	public <S extends E> BatchEntitySaveCommand<S> saveAllCommand(@NotNull Iterable<S> entities) {
		return sqlClient
				.getEntities()
				.batchSaveCommand(CollUtil.toCollection(entities));
	}

	@Override
	public int delete(@NotNull E entity, DeleteMode mode) {
		return sqlClient.getEntities().delete(
				entityType,
				ImmutableObjects.get(entity, immutableType.getIdProp().getId()),
				mode
		).getAffectedRowCount(AffectedTable.of(immutableType));
	}

	@Override
	public int deleteAll(@NotNull Iterable<? extends E> entities, DeleteMode mode) {
		return sqlClient.getEntities().batchDelete(
				entityType,
				CollUtil
						.toCollection(entities)
						.stream()
						.map(it -> ImmutableObjects.get(it, immutableType.getIdProp().getId()))
						.collect(Collectors.toList()),
				mode
		).getAffectedRowCount(AffectedTable.of(immutableType));
	}

	@Override
	public int deleteById(@NotNull ID id, DeleteMode mode) {
		return sqlClient
				.getEntities()
				.delete(entityType, id, mode)
				.getAffectedRowCount(AffectedTable.of(immutableType));
	}

	@Override
	public int deleteByIds(Iterable<? extends ID> ids, DeleteMode mode) {
		return sqlClient
				.getEntities()
				.batchDelete(entityType, CollUtil.toCollection(ids), mode)
				.getAffectedRowCount(AffectedTable.of(immutableType));
	}

	@Override
	public void deleteAll() {
		Mutations
				.createDelete(sqlClient, immutableType, (d, t) -> {
				})
				.execute();
	}

	private ConfigurableRootQuery<?, E> createQuery(Fetcher<E> fetcher, TypedProp.Scalar<?, ?>[] sortedProps) {
		MutableRootQueryImpl<Table<E>> query =
				new MutableRootQueryImpl<>(sqlClient, immutableType, ExecutionPurpose.QUERY, false);
		Table<E> table = query.getTable();
		for (TypedProp.Scalar<?, ?> sortedProp : sortedProps) {
			if (!sortedProp.unwrap().getDeclaringType().isAssignableFrom(immutableType)) {
				throw new IllegalArgumentException(
						"The sorted field \"" +
								sortedProp +
								"\" does not belong to the type \"" +
								immutableType +
								"\" or its super types"
				);
			}
			PropExpression<?> expr = table.get(sortedProp.unwrap().getName());
			Order astOrder;
			if (sortedProp.isDesc()) {
				astOrder = expr.desc();
			} else {
				astOrder = expr.asc();
			}
			if (sortedProp.isNullsFirst()) {
				astOrder = astOrder.nullsFirst();
			}
			if (sortedProp.isNullsLast()) {
				astOrder = astOrder.nullsLast();
			}
			query.orderBy(astOrder);
		}
		query.freeze();
		return query.select(fetcher != null ? table.fetch(fetcher) : table);
	}

	private static class PagerImpl implements Pager {

		private final int pageIndex;

		private final int pageSize;

		PagerImpl(int pageIndex, int pageSize) {
			this.pageIndex = pageIndex;
			this.pageSize = pageSize;
		}

		@Override
		public <T> Page<T> execute(ConfigurableRootQuery<?, T> query) {
			return PagingQueries.execute(
					query,
					pageIndex,
					pageSize,
					(entities, totalCount, queryImplementor) ->
							new PageImpl<>(
									entities,
									PageRequest.of(
											pageIndex,
											pageSize,
											toSort(
													queryImplementor.getOrders(),
													queryImplementor.getSqlClient().getMetadataStrategy()
											)
									),
									totalCount
							)
			);
		}
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

	public static JSqlClientImplementor validateSqlClient(JSqlClient sqlClient) {
		JSqlClientImplementor implementor = (JSqlClientImplementor) sqlClient;
//        if (!(implementor.getConnectionManager() instanceof SpringConnectionManager)) {
//            throw new IllegalArgumentException(
//                    "The connection manager of sql client must be instance of \"" +
//                            SpringConnectionManager.class.getName() +
//                            "\""
//            );
//        }
//        if (!SpringTransientResolverProvider.class.isAssignableFrom(implementor.getResolverProviderClass())) {
//            throw new IllegalArgumentException(
//                    "The transient resolver provider of sql client must be instance of \"" +
//                            SpringConnectionManager.class.getName() +
//                            "\""
//            );
//        }
//        ConnectionManager slaveConnectionManager = implementor.getSlaveConnectionManager(false);
//        if (slaveConnectionManager != null && !(slaveConnectionManager instanceof SpringConnectionManager)) {
//            throw new IllegalArgumentException(
//                    "The slave connection manager of sql client must be null or instance of \"" +
//                            SpringConnectionManager.class.getName() +
//                            "\""
//            );
//        }
		return implementor;
	}
}
