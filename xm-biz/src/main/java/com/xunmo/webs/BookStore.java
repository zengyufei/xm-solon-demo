package com.xunmo.webs;

import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.OneToMany;

import javax.validation.constraints.Null;
import java.util.List;

@Entity
public interface BookStore {

    @Id
    long id();

    @Key
    String name();

    @Null
    String website();

    @OneToMany(mappedBy = "store")
    List<Book> books();
}