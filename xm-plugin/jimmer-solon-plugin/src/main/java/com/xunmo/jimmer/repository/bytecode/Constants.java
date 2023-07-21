package com.xunmo.jimmer.repository.bytecode;

import com.xunmo.jimmer.page.Pageable;
import com.xunmo.jimmer.page.Sort;
import com.xunmo.jimmer.repository.parser.Context;
import com.xunmo.jimmer.repository.parser.QueryMethod;
import com.xunmo.jimmer.repository.support.QueryExecutors;
import org.babyfish.jimmer.impl.asm.Type;
import org.babyfish.jimmer.meta.ImmutableType;
import org.babyfish.jimmer.sql.fetcher.Fetcher;
import org.babyfish.jimmer.sql.runtime.JSqlClientImplementor;

import java.lang.reflect.Method;

interface Constants {

    String CONTEXT_INTERNAL_NAME = Type.getInternalName(Context.class);

    String CONTEXT_DESCRIPTOR = Type.getDescriptor(Context.class);

    String QUERY_METHOD_INTERNAL_NAME = Type.getInternalName(QueryMethod.class);

    String QUERY_METHOD_DESCRIPTOR = Type.getDescriptor(QueryMethod.class);

    String IMMUTABLE_TYPE_INTERNAL_NAME = Type.getInternalName(ImmutableType.class);

    String IMMUTABLE_TYPE_DESCRIPTOR = Type.getDescriptor(ImmutableType.class);

    String METHOD_DESCRIPTOR = Type.getDescriptor(Method.class);

    String J_SQL_CLIENT_IMPLEMENTOR_DESCRIPTOR = Type.getDescriptor(JSqlClientImplementor.class);


    String QUERY_EXECUTORS_INTERNAL_NAME = Type.getInternalName(
            QueryExecutors.class);

    String QUERY_EXECUTORS_METHOD_DESCRIPTOR = '(' +
            J_SQL_CLIENT_IMPLEMENTOR_DESCRIPTOR +
            IMMUTABLE_TYPE_DESCRIPTOR +
            QUERY_METHOD_DESCRIPTOR +
            Type.getDescriptor(Pageable.class) +
            Type.getDescriptor(Sort.class) +
            Type.getDescriptor(Fetcher.class) +
            "[Ljava/lang/Object;)Ljava/lang/Object;";
}
