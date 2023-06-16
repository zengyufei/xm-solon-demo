package com.xunmo.webs;

import org.babyfish.jimmer.sql.*;

import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.List;

@Entity
public interface Book {

    @Id
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