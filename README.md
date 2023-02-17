# 基于 Solon 开发的脚手架


## 食用方式

```
1. 运行 mysql
2. 创建 xunmo 库 utf8mb4
3. 导入 xm-biz/db/xunmo.sql
4. 运行 redis
5. 启动 xm-biz 的 BizApp主类
6. 访问 http://localhost:8667
```

#### 进度 90%

## 项目定制功能:
    1. xm-biz-base 插件包提供: 

        A. 项目本身自定义 部分基类接口和实现;
        B. 项目本身自定义 输出工具和输出实现;
        C. 项目本身自定义 异常类;
        D. 项目本身自定义 异常枚举;
        E. 项目本身自定义 常量;
        F. 项目本身自定义 配置实体;
        G. 项目本身自定义 抽象父类;

    2. xm-exception-record 插件包提供: 
        PS: 可关闭, 引入后直接生效.

        A. 项目本身自定义 系统异常持久化功能实现, 增伤改成;
        B. 项目本身自定义 系统异常持久化 rabbitMq 通讯功能实现;

## 脚手架自带功能:
    1. xm-orm-mp-mysql 插件包提供: 
        PS: 引入后需搭配配置才能正确生效.

        A. 提供 mybatis-plus 其他边角能力;
            * 关闭缓存
            * 下划线自动转驼峰
            * mp自带分页插件
            * 关闭 Banner
            * 打开 SqlRunner
            * 增加 mapper 默认方法, 批量插入和批量修改(删除部分原生方法)
            * 设置逻辑删除字段 disabled
            * 逻辑删除字段 disabled=1 为已删除类型
            * 设置默认的 MetaObjectHandler, 可通过配置重写

        B. 提供 Pagehelper 能力;
            * 分页合理化
            * pageSize=0 时不分页(重要理解)

    2. xm-p6spy 插件包提供: 
        PS: 引入后需搭配配置才能正确生效.

        A. 代替 mybatis sql 语句打印;
        B. 接管 logback 日志打印 sql语句;

    3. xm-auto-tran 插件包提供: 
        PS: 可关闭, 引入后直接生效. 可修改内置参数.

        A. 自动给符合的方法名自动进入事务;
        B. 提供事务后执行方法, TranAfterUtil 工具类;
        
    4. xm-trace-id 插件包提供: 
        PS: 可关闭, 引入后直接生效.

        A. 在 param/attr/header 均能获取到一个 "reqId" 的 uuid 值;
        B. 设置一个 reqId 到 MDC 类中, 可传给子线程(特殊情况下无法传递);
        
    5. xm-core 插件包提供: 
        PS: 引入后直接生效.

        A. 解决 cros 跨域 问题;
        B. 处理参数左右空白, 空字符串入参设置为null;
        C. 处理 json 字段左右空白, 空字符串入参设置为null;
        D. 定制化 json 对日期类型的处理;
        E. 提供 XmMap/XmUtil 两个重要基础工具类;
            * XmMap 提供类似 mp lambda 字段方法名功能;
            * XmUtil 提供 lambda 变形快捷方法;
            * XmUtil 提供 mp-mate 对账 功能;
            * XmUtil 提供 中文数字英文混排序 功能;
            * XmUtil 提供 前端 sort 字符串切割, 自动设置进 mp Wrapper对象的功能;
            * XmUtil 通过 XmUtilExt 接口 SPI 方式实现被依赖方的方法重写;
        F. 提供 BigDecimalUtil 加减乘除封装工具类;
        G. 提供 AjaxError 异常抛出类;
        H. 提供 UnFileUtil 解压 7z/rar/zip 工具类;
        
    6. xm-rabbitmq-util 插件包提供: 

        A. 封装的池化的 rabbitmq 工具类;
        
    7. xm-exception 插件包提供: 
        PS: 引入后直接生效.

        A. 重现 springboot 统一异常处理器的写法;
        
    8. xm-request-xss-filter 插件包提供: 
        PS: 可关闭, 引入后直接生效, 可选 filter/handler 方式实现.

        A. 过滤 请求header/请求param/请求body 的可能存在跨站脚本攻击参数;
        B. 提供 RequestXssFilterExt/RequestXssHandlerExt 接口, 实现后自动覆盖默认实现类;
        
    9. xm-request-times-console 插件包提供: 
        PS: 可关闭, 引入后直接生效, 可选 filter/handler 方式实现.

        A. 简单打印请求时间差;
        B. 提供 RequestTimesConsoleFilterExt/RequestTimesConsoleHandlerExt 接口, 实现后自动覆盖默认实现类;
        
    10. xm-request-args-console 插件包提供: 
        PS: 可关闭, 引入后直接生效, 可选 filter/handler 方式实现.

        A. 简单打印请求参数/请求body;
        B. 提供 ArgsConsoleFilterExt/ArgsConsoleHandlerExt 接口, 实现后自动覆盖默认实现类;
        
    11. xm-health 插件包提供: 
        PS: 可关闭, 引入后直接生效.

        A. 提供 /health 对外接口, 部署脚本时, 检查是否部署成功使用;

    12 xm-core-web 插件包提供: 
        PS: 部分可关闭, 部分引入后直接生效, 部分需要配置才生效.

        A. 自动扫描 Controller 父类接口上面的方法信息, 可实现不同的 API 前缀;
        B. 自动扫描 Controller 父类接口方法, 泛型参数会被擦除， 实现进行自动参数绑定;
        C. 提供缓存实现, 可关闭;
        D. 提供 AjaxJson 统一输出类;
        E. 提供默认父类 entity mapper service controller;
        F. 提供自带方法父类, 如继承 SortController, 如拥有排序字段的实体, 则拥有;
            * /upTop 置顶值的位置
            * /upMove 上移值的一个位置
            * /downMove 下移值的一个位置
            * /changeSort 交换两个值的位置
            PS: 继承自带方法父类后, 需要确定唯一性由哪些字段组成, 可以在实体字段上添加 @SortUni 确定唯一性组成成员
        G. 提供自带方法父类, 如继承 RelateController, 如拥有上下级关系的两种实体, 则拥有;
            * 包含 SortController 基础的 4个接口方法
            * 部门存在则返回否则报错
            * 根据下级code获取上级部门对象
            * 指定区间获取部门（不包括beginCode和endCode）
            * 指定区间获取部门（包括beginCode和endCode）
            * 获取直接下级部门
            * 获取所有上级部门（不包括自己）
            * 获取所有上级部门（包括自己）
            * 获取所有下级部门（不包括自己）
            * 获取所有下级部门（包括自己）
            * 删除部门 > 删除所有关联此部门子节点的闭包关系
            * 获取多个部门的下级部门共集（不包括自己）
            * 获取多个部门的下级部门共集（包括自己）
            PS: 继承自带方法父类后, 需要确定唯一性由哪些字段组成, 可以在实体字段上添加 @SortUni 确定唯一性组成成员
        
    13. xm-solon 插件包提供: 

        A. 重写 solon 的部分类适应扫描 controller 父类接口;
        B. 重写 solon 的部分类解决 controller 父类接口方法参数泛型擦除后, 参数绑定问题;
        
    14. xm-annotation 插件包提供: 

        A. 提供 公共枚举;
        B. 提供 公共接口;
        
    15. xm-constant 插件包提供: 

        A. 提供 公共常量;
        
    16. xm-bom 插件包提供: 

        A. 提供 公共依赖版本号;

## 脚手架其他自带功能:
        
    1. 可以通过 EventBus 打印日志 或者直接 log.info 打印日志,自行选择
    2. 提供复制模块并重命名 CopyAndRenameTest 类,可以快速拷贝Dict文件夹变成其他文件夹
    3. bin/ 目录下 windows 开发使用.
    4. deploy/ 目录下 linux 部署使用.


## 未来计划

```
1. mp-join 接入
2. mp-dataScope 接入
3. easy_tran 接入
4. sa-token 接入
5. 流程框架 接入
6. 用户操作日志 接入
7. execl 改造
8. 代码生成器改造
```
