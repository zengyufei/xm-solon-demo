package com.xunmo.webs;

import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;
import org.babyfish.jimmer.sql.OneToMany;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Entity
public interface BookStore {

    @Id
    long id();

    @Key
    String name();

    @Nullable
    String website();

    @OneToMany(mappedBy = "store")
    List<Book> books();
}
