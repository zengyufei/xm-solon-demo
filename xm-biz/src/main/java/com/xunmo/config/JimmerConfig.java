package com.xunmo.config;

import com.xunmo.BizApp;
import org.babyfish.jimmer.jackson.ImmutableModule;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.runtime.ConnectionManager;
import org.babyfish.jimmer.sql.runtime.Executor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.serialization.jackson.JacksonRenderFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.function.Function;

@Configuration
public class JimmerConfig {

    @Bean
    public void jsonInit(@Inject JacksonRenderFactory factory) {
        final ImmutableModule immutableModule = new ImmutableModule();
        BizApp.initModule(immutableModule);
        factory.config()
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .registerModule(immutableModule);
    }

    @Bean
    public JSqlClient sqlClient(@Inject DataSource dataSource) {
        return JSqlClient
                .newBuilder()
//                .setConnectionManager(
//                        ConnectionManager
//                                .simpleConnectionManager(dataSource)
//                )
                .setConnectionManager(
                        new ConnectionManager() {
                            @Override
                            public <R> R execute(
                                    Function<Connection, R> block
                            ) {
                                Connection con = null;
                                R var3;
                                try {
                                    con = dataSource.getConnection();
                                    con.setAutoCommit(false);
                                    var3 = block.apply(con);
                                    con.commit();
                                } catch (Throwable var6) {
                                    if (con != null) {
                                        try {
                                            con.rollback();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    throw new RuntimeException(var6);
                                } finally {
                                    if (con != null) {
                                        try {
                                            con.close();
                                        } catch (SQLException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                                return var3;
                            }
                        }
                )
                .setExecutor(Executor.log())
                .setDefaultBatchSize(256)
                .setDefaultListBatchSize(32)
                .build();
    }

}
