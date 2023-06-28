package com.xunmo.common.base;

import com.xunmo.common.entity.page.Page;
import com.xunmo.common.entity.page.Pageable;
import com.xunmo.common.entity.page.Sort;
import org.babyfish.jimmer.Input;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.meta.TypedProp;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.*;
import org.babyfish.jimmer.sql.ast.query.ConfigurableRootQuery;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface JRepository<E, ID> extends PagingAndSortingRepository<E, ID> {

    /*
     * For provider
     */

    JSqlClient sql();

    ImmutableType type();

    Class<E> entityType();

    Pager pager(Pageable pageable);

    Pager pager(int pageIndex, int pageSize);

    /*
     * For consumer
     */

    E findNullable(ID id);

    E findNullable(ID id, Fetcher<E> fetcher);

    @NotNull
    @Override
    default Optional<E> findById(ID id) {
        return Optional.ofNullable(findNullable(id));
    }

    default Optional<E> findById(ID id, Fetcher<E> fetcher) {
        return Optional.ofNullable(findNullable(id, fetcher));
    }

    List<E> findByIds(Iterable<ID> ids);

    List<E> findByIds(Iterable<ID> ids, Fetcher<E> fetcher);

    Map<ID, E> findMapByIds(Iterable<ID> ids);

    Map<ID, E> findMapByIds(Iterable<ID> ids, Fetcher<E> fetcher);

    @NotNull
    @Override
    List<E> findAll();

    @SuppressWarnings("unchecked")
    List<E> findAll(TypedProp.Scalar<?, ?>... sortedProps);

    @SuppressWarnings("unchecked")
    List<E> findAll(Fetcher<E> fetcher, TypedProp.Scalar<?, ?>... sortedProps);

    @NotNull
    @Override
    List<E> findAll(@NotNull Sort sort);

    List<E> findAll(Fetcher<E> fetcher, Sort sort);

    Page<E> findAll(int pageIndex, int pageSize);

    Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher);

    @SuppressWarnings("unchecked")
    Page<E> findAll(int pageIndex, int pageSize, TypedProp.Scalar<?, ?>... sortedProps);

    @SuppressWarnings("unchecked")
    Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher, TypedProp.Scalar<?, ?>... sortedProps);

    Page<E> findAll(int pageIndex, int pageSize, Sort sort);

    Page<E> findAll(int pageIndex, int pageSize, Fetcher<E> fetcher, Sort sort);

    @NotNull
    @Override
    Page<E> findAll(@NotNull Pageable pageable);

    Page<E> findAll(Pageable pageable, Fetcher<E> fetcher);

    @Override
    default boolean existsById(ID id) {
        return findNullable(id) != null;
    }

    @Override
    long count();

    @NotNull
    default E insert(@NotNull Input<E> input) {
        return save(input.toEntity(), SaveMode.INSERT_ONLY).getModifiedEntity();
    }

    @NotNull
    default E insert(@NotNull E entity) {
        return save(entity, SaveMode.INSERT_ONLY).getModifiedEntity();
    }

    @NotNull
    default E update(@NotNull Input<E> input) {
        return save(input.toEntity(), SaveMode.UPDATE_ONLY).getModifiedEntity();
    }

    @NotNull
    default E update(@NotNull E entity) {
        return save(entity, SaveMode.UPDATE_ONLY).getModifiedEntity();
    }

    @NotNull
    default E save(@NotNull Input<E> input) {
        return save(input.toEntity(), SaveMode.UPSERT).getModifiedEntity();
    }

    @NotNull
    @Override
    default <S extends E> S save(@NotNull S entity) {
        return save(entity, SaveMode.UPSERT).getModifiedEntity();
    }

    @NotNull
    default SimpleSaveResult<E> save(
            @NotNull Input<E> input,
            SaveMode mode
    ) {
        return save(input.toEntity(), mode);
    }

    @NotNull <S extends E> SimpleSaveResult<S> save(@NotNull S entity, SaveMode mode);

    @NotNull
    SimpleEntitySaveCommand<E> saveCommand(@NotNull Input<E> input);

    @NotNull <S extends E> SimpleEntitySaveCommand<S> saveCommand(@NotNull S entity);

    @NotNull
    @Override
    default <S extends E> Iterable<S> saveAll(@NotNull Iterable<S> entities) {
        return saveAll(entities, SaveMode.UPSERT)
                .getSimpleResults()
                .stream()
                .map(SimpleSaveResult::getModifiedEntity)
                .collect(Collectors.toList());
    }

    @NotNull <S extends E> BatchSaveResult<S> saveAll(@NotNull Iterable<S> entities, SaveMode mode);

    @NotNull <S extends E> BatchEntitySaveCommand<S> saveAllCommand(@NotNull Iterable<S> entities);

    @Override
    default void delete(@NotNull E entity) {
        delete(entity, DeleteMode.AUTO);
    }

    int delete(@NotNull E entity, DeleteMode mode);

    @Override
    default void deleteAll(@NotNull Iterable<? extends E> entities) {
        deleteAll(entities, DeleteMode.AUTO);
    }

    int deleteAll(@NotNull Iterable<? extends E> entities, DeleteMode mode);

    @Override
    default void deleteById(@NotNull ID id) {
        deleteById(id, DeleteMode.AUTO);
    }

    int deleteById(@NotNull ID id, DeleteMode mode);

    default void deleteByIds(Iterable<? extends ID> ids) {
        deleteByIds(ids, DeleteMode.AUTO);
    }

    int deleteByIds(Iterable<? extends ID> ids, DeleteMode mode);

    @Override
    void deleteAll();

    interface Pager {

        <T> Page<T> execute(ConfigurableRootQuery<?, T> query);
    }
}
