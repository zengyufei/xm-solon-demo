# xm-jimmer-solon-plugin



#### 介绍


Jimmer 官网文档：https://babyfish-ct.github.io/jimmer/zh/



#### 使用说明

1. 依赖版本管理

```xml
<!-- 版本管理 -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>vip.xunmo</groupId>
            <artifactId>xm-bom</artifactId>
            <version>1.0.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

```xml
<!-- 配置编译插件 -->
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.10.1</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <compilerArgument>-parameters</compilerArgument>
        <encoding>UTF-8</encoding>
        <annotationProcessorPaths>
            <path>
                <groupId>org.babyfish.jimmer</groupId>
                <artifactId>jimmer-apt</artifactId>
                <version>0.7.118</version>
            </path>
            <path>
                <groupId>org.projectlombok</groupId>
                <artifactId>lombok</artifactId>
                <version>1.18.26</version>
            </path>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
                <version>1.5.3.Final</version>
            </path>
        </annotationProcessorPaths>
    </configuration>
</plugin>
```

2. 普通依赖
```xml
<dependencies>

    <dependency>
        <groupId>vip.xunmo</groupId>
        <artifactId>xm-jimmer-solon-plugin</artifactId>
    </dependency>


    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.26</version>
    </dependency>
    <dependency>
        <groupId>com.zaxxer</groupId>
        <artifactId>HikariCP</artifactId>
        <version>4.0.3</version>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
    </dependency>


    <!-- 必须注释掉 -->
    <!--        <dependency>-->
    <!--            <groupId>org.noear</groupId>-->
    <!--            <artifactId>solon-api</artifactId>-->
    <!--        </dependency>-->

    <!-- 必须引入 -->
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-lib</artifactId>
    </dependency>
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon.web.cors</artifactId>
    </dependency>

    <!-- 必须引入，可选其他启动器 -->
    <!-- Http 启动器 -->
    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon.boot.jlhttp</artifactId>
    </dependency>


    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon.logging.simple</artifactId>
    </dependency>

    <dependency>
        <groupId>org.noear</groupId>
        <artifactId>solon-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```
   

2. 配置示例

``` yaml
datasource.db:
  type: "com.zaxxer.hikari.HikariDataSource"
  driverClassName: org.h2.Driver
  jdbcUrl: jdbc:h2:mem:test;MODE=MYSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=TRUE;IGNORECASE=TRUE;NON_KEYWORDS=user
```



3. 数据源配置示例

``` java
@Configuration
public class DbConfig {
    @Bean(value = "db", typed = true) // typed 表示可类型注入 //即默认
    public DataSource db(@Inject("${datasource.db}") HikariDataSource ds) {
                try(final Connection connection = ds.getConnection()){
                        connection.prepareStatement(ResourceUtil.getResourceAsString("init.sql")).execute();
                        connection.prepareStatement(ResourceUtil.getResourceAsString("data.sql")).execute();
                } catch (Exception e) {
                        throw new RuntimeException(e);
                }
                return ds;
    }
}

```


#### 数据库文件

1. init.sql

https://github.com/zengyufei/xm-solon-demo/blob/jim/xm-biz/src/main/resources/db/h2/init.sql

2. data.sql

https://github.com/zengyufei/xm-solon-demo/blob/jim/xm-biz/src/main/resources/db/h2/data.sql

3. 代码示例

- Entity

```java

@Entity
@Table(name = User.TABLE_NAME)
public interface User {
    
    String TABLE_NAME = "user";
    
    @Id
    String usersId();
    
    @Nullable
    @Column(name = Columns.userName)
    String userName();
    
    @Nullable
    @Column(name = Columns.isImported)
    Integer isImported();
    
    @Nullable
    @Column(name = Columns.importTime)
    LocalDateTime importTime();
    
    @Nullable
    @Column(name = Columns.isSystemDefault)
    Integer isSystemDefault();
    
    @Nullable
    @Column(name = Columns.status)
    String status();
    
    @Nullable
    @IdView
    String createId();

    @Nullable
    @ManyToOne
    @JoinColumn(name = Columns.createId)
    User create();
    
    @IdView
    @Nullable
    String updateId();

    @ManyToOne
    @Nullable
    @JoinColumn(name = Columns.updateId)
    User update();
    
    @Column(name = Columns.createTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime();
    
    @Nullable
    @Column(name = Columns.updateTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime();
    
    interface Columns {
        String usersId = "user_id"; // 用户ID
        String userName = "user_name"; // 用户名
        String createTime = "create_time"; // 创建时间
        String updateTime = "update_time"; // 修改时间
        String createId = "create_id"; // 创建人ID
        String updateId = "update_id"; // 修改人ID
        String isImported = "is_imported"; // 是否导入
        String importTime = "import_time"; // 导入时间
        String isSystemDefault = "is_system_default"; // 是否系统默认
        String status = "status"; // 状态
    }
    
    interface FieldNames {
        String usersId = "usersId"; // 用户ID
        String userName = "userName"; // 用户名
        String createTime = "createTime"; // 创建时间
        String updateTime = "updateTime"; // 修改时间
        String createId = "createId"; // 创建人ID
        String updateId = "updateId"; // 修改人ID
        String createName = "createName"; // 创建人用户名
        String updateName = "updateName"; // 修改人用户名
        String isImported = "isImported"; // 是否导入
        String importTime = "importTime"; // 导入时间
        String isSystemDefault = "isSystemDefault"; // 是否系统默认
        String status = "status"; // 状态
    }
}
```

    

- Query

```java
@Data
public class UserQuery {

    private String usersId;
    private String userName;

    private LocalDateTime createTime;
    private LocalDateTime beginCreateTime;
    private LocalDateTime endCreateTime;

    private LocalDateTime updateTime;
    private LocalDateTime beginUpdateTime;
    private LocalDateTime endUpdateTime;

    private String createId;
    private String createName;

    private String updateId;
    private String updateName;

    private String approvalStatus;
    private String approverId;
    private String approverName;
    private String approvalComment;
    private LocalDateTime approvalTime;
    private LocalDateTime beginApprovalTime;
    private LocalDateTime endApprovalTime;

    private Integer isImported;

    private LocalDateTime importTime;
    private LocalDateTime beginImportTime;
    private LocalDateTime endImportTime;

    private Integer isSystemDefault;
    private String status;
}
```

    

- Input

```java
@Data
public class UserInput implements Input<User> {

    private static final Converter CONVERTER = Mappers.getMapper(Converter.class);

    private String usersId;
    private String userName;

    private String createId;
    private String createName;
    private LocalDateTime createTime;

    private String updateId;
    private String updateName;
    private LocalDateTime updateTime;

    private String approvalStatus;
    private String approverId;
    private String approverName;
    private String approvalComment;
    private LocalDateTime approvalTime;


    private Integer isImported;
    private LocalDateTime importTime;

    private Integer isSystemDefault;
    private String tenantId;
    private Integer version;
    private String status;

    @Override
    public User toEntity() {
        return CONVERTER.toUser(this);
    }
    @Mapper
    interface Converter {
        @BeanMapping(unmappedTargetPolicy = ReportingPolicy.IGNORE)
        User toUser(UserInput input);
    }
}
```
    

- Repository

```java
@Db
public interface UserRepository extends JRepository<User, String> {
}
```

   
- Controller

```java

@Slf4j
@Valid
@Controller
@Mapping("/user")
public class UserController {

    private final static UserTable TABLE = UserTable.$;

    private final static UserFetcher FETCHER = UserFetcher.$;

    @Db
    private JSqlClient sqlClient;

    @Db
    private UserRepository userRepository;

    @Post
    @Mapping("/list")
    public Page<User> list(@Validated UserQuery query, PageRequest pageRequest) throws Exception {
        final String usersId = query.getUserId();
        final String userName = query.getUserName();
        final LocalDateTime beginCreateTime = query.getBeginCreateTime();
        final LocalDateTime endCreateTime = query.getEndCreateTime();
        return userRepository.pager(pageRequest)
                .execute(sqlClient.createQuery(TABLE)
                        // 根据 用户id 查询
                        .whereIf(StrUtil.isNotBlank(usersId), () -> TABLE.usersId().eq(usersId))
                        // 根据 用户名称 模糊查询
                        .whereIf(StrUtil.isNotBlank(userName), () -> TABLE.userName().like(userName))
                        // 根据 创建时间 大于等于查询
                        .whereIf(beginCreateTime != null, () -> TABLE.createTime().ge(beginCreateTime))
                        // 根据 创建时间 小于等于查询
                        .whereIf(endCreateTime != null, () -> TABLE.createTime().le(endCreateTime))
                        // 默认排序 创建时间 倒排
                        .orderBy(TABLE.createTime().desc())
                        .select(TABLE.fetch(
                                // 查询 用户表 所有属性（非对象）
                                FETCHER.allScalarFields()
                                        // 查询 创建者 对象，只显示 姓名
                                        .create(UserFetcher.$.userName())
                                        // 查询 修改者 对象，只显示 姓名
                                        .update(UserFetcher.$.userName())
                        )));
    }

    @Post
    @Mapping("/getById")
    public User getById(@NotNull @NotBlank String id) throws Exception {
//        final User user = userRepository.findById(id).orElse(null);
        final User user = this.sqlClient.findById(User.class, id);
        return user;
    }

    @Post
    @Mapping("/add")
    public User add(@Validated UserInput input) throws Exception {
//        final User modifiedEntity = userRepository.save(input);
        final SimpleSaveResult<User> result = this.sqlClient.save(input);
        final User modifiedEntity = result.getModifiedEntity();
        return modifiedEntity;
    }

    @Post
    @Mapping("/update")
    public User update(@Validated UserInput input) throws Exception {
//        final User modifiedEntity = userRepository.update(input);
        final SimpleSaveResult<User> result = this.sqlClient.update(input);
        final User modifiedEntity = result.getModifiedEntity();
        return modifiedEntity;
    }

    @Post
    @Mapping("/deleteByIds")
    @Tran
    public int deleteByIds(List<String> ids) throws Exception {
//        final int totalAffectedRowCount = userRepository.deleteByIds(ids, DeleteMode.AUTO);
        final DeleteResult result = this.sqlClient.deleteByIds(User.class, ids);
        final int totalAffectedRowCount = result.getTotalAffectedRowCount();
        return totalAffectedRowCount;
    }

    /**
     * 主动抛出异常 - 用于测试
     */
    @Get
    @Mapping("/exception")
    public Boolean exception() throws Exception {
        throw new NullPointerException("主动抛出异常 - 用于测试 " + DateUtil.now());
    }

}
```



4. 运行测试示例
```java
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HelloTest extends HttpTester {
    @Test
    public void hello() throws IOException {
        final String result = path("/user/list?page=1&size=10")
                .bodyJson("{}")
                .post();
        System.out.println(JSONUtil.toJsonPrettyStr(result));
    }
}
```
日志抓取
```java
INFO  2023-08-06 16:15:58.057 [-jlhttp-1][*][c.x.j.i.JimmerAdapterDefault]: Execute sql : select count(tb_1_.USER_ID) from user tb_1_, variables: [], purpose: QUERY
INFO  2023-08-06 16:15:58.090 [-jlhttp-1][*][c.x.j.i.JimmerAdapterDefault]: Execute sql : select tb_1_.USER_ID, tb_1_.user_name, tb_1_.is_imported, tb_1_.import_time, tb_1_.is_system_default, tb_1_.status, tb_1_.create_time, tb_1_.update_time, tb_1_.create_id, tb_1_.update_id from user tb_1_ order by tb_1_.create_time desc limit ? offset ?, variables: [10, 10], purpose: QUERY
INFO  2023-08-06 16:15:58.129 [-jlhttp-1][*][c.x.j.i.JimmerAdapterDefault]: Execute sql : select tb_1_.USER_ID, tb_1_.user_name from user tb_1_ where tb_1_.USER_ID in (?, ?, ?, ?, ?, ?, ?, ?, ?, ?), variables: [user_56, user_24, user_48, user_9, user_36, user_20, user_12, user_13, user_71, user_19], purpose: LOAD
INFO  2023-08-06 16:15:58.136 [-jlhttp-1][*][c.x.j.i.JimmerAdapterDefault]: Execute sql : select tb_1_.USER_ID, tb_1_.user_name from user tb_1_ where tb_1_.USER_ID in (?, ?, ?, ?, ?, ?, ?, ?, ?), variables: [user_3, user_26, user_50, user_92, user_6, user_82, user_97, user_42, user_32], purpose: LOAD
```

5. 扩展示例
```java
@MappedSuperclass
public interface BaseEntity {
    
    @Nullable
    @IdView
    String createId();

    @Nullable
    @ManyToOne
    @JoinColumn(name = Columns.createId)
    User create();
    
    @IdView
    @Nullable
    String updateId();

    @ManyToOne
    @Nullable
    @JoinColumn(name = Columns.updateId)
    User update();
    
    @Column(name = Columns.createTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createTime();
    
    @Nullable
    @Column(name = Columns.updateTime)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime updateTime();

    interface Columns {
        String createTime = "create_time"; // 创建时间
        String updateTime = "update_time"; // 修改时间
        String createId = "create_id"; // 创建人ID
        String updateId = "update_id"; // 修改人ID
    }

    interface FieldNames {
        String createId = "createId"; // 创建人ID
        String updateId = "updateId"; // 修改人ID
        String createTime = "createTime"; // 创建时间
        String updateTime = "updateTime"; // 修改时间
    }
}
```

```java
@Component
public class BaseEntityDraftInterceptor implements DraftInterceptor<BaseEntityDraft> {

    @Override
    public void beforeSave(BaseEntityDraft draft, boolean isNew) {
                if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.UPDATE_TIME)) {
                        draft.setUpdateTime(LocalDateTime.now());
                }
                if (isNew) {
                        if (!ImmutableObjects.isLoaded(draft, BaseEntityProps.CREATE_TIME)) {
                                draft.setCreateTime(LocalDateTime.now());
                        }
                }
    }
}
```
