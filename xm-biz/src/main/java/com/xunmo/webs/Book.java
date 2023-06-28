package com.xunmo.webs;

import com.xunmo.common.base.BaseEntity;
import com.xunmo.common.base.ProcessEntity;
import com.xunmo.common.base.TenantEntity;
import com.xunmo.common.base.VersionEntity;
import com.xunmo.config.jimmer.SnowflakeIdGenerator;
import org.babyfish.jimmer.sql.*;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.List;

@Entity
public interface Book extends BaseEntity, ProcessEntity, TenantEntity, VersionEntity {

    @Id
    @GeneratedValue(generatorType = SnowflakeIdGenerator.class)
    long id();

    @Key
    String name();

    @Key
    int edition();

    BigDecimal price();

    @Null
    @ManyToOne
    BookStore store();

    @ManyToMany
    @JoinTable(
            name = "BOOK_AUTHOR_MAPPING",
            joinColumnName = "BOOK_ID",
            inverseJoinColumnName = "AUTHOR_ID"
    )
    List<Author> authors();
}
