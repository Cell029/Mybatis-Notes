# MyBatis 学习笔记

## 项目列表

#### 1.**Demo1-FirstMybatis**：MyBatis 入门示例，配置和基础代码：[MyBatisIntroductionTest.java](./Demo1-FirstMybatis/src/main/java/com/cell/test/MyBatisIntroductionTest.java)

#### 2. **Demo2-Mybatis_CRUD**：MyBatis 的增删改查操作演示：[CRUD.package](./Demo2-Mybatis_CRUD/src/main/java/com/cell/test)

#### 3. **Demo3-Mybatis_config**:Mybatis 的核心配置文件详解

## 一、MyBatis 入门程序

### 1. 添加依赖

在 `pom.xml` 中添加依赖：

```xml
<!-- MyBatis 核心依赖 -->  
<dependency>  
  <groupId>org.mybatis</groupId>  
  <artifactId>mybatis</artifactId>  
  <version>3.5.15</version>  
</dependency>  
  
<!-- MySQL 驱动 -->  
<dependency>  
  <groupId>com.mysql</groupId>  
  <artifactId>mysql-connector-j</artifactId>  
  <version>8.2.0</version>  
</dependency>
```

---

### 2. 建表

```sql
CREATE TABLE `t_car` (
  `id` bigint(20) UNSIGNED ZEROFILL NOT NULL AUTO_INCREMENT,
  `car_num` varchar(255) DEFAULT NULL,
  `brand` varchar(255) DEFAULT NULL,
  `guide_price` decimal(10,2) DEFAULT NULL,
  `produce_time` char(10) DEFAULT NULL,
  `car_type` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
```

---

### 3. 配置 MyBatis 核心配置文件

在 `resources` 根目录下新建 `mybatis-config.xml` 配置文件：

```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<!DOCTYPE configuration  
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"  
        "http://mybatis.org/dtd/mybatis-3-config.dtd">  
<configuration>  
    <environments default="development">  
        <environment id="development">  
            <transactionManager type="JDBC"/>  
            <dataSource type="POOLED">  
                <property name="driver" value="com.mysql.cj.jdbc.Driver"/>  
                <property name="url" value="jdbc:mysql://localhost:3306/demo_01"/>  
                <property name="username" value="root"/>  
                <property name="password" value="123"/>  
            </dataSource>  
        </environment>  
    </environments>  
    <mappers>  
	    <!--执行 XxxMapper.xml 文件的路径-->  
        <!--resource 属性会自动从类的根路径下开始查找资源-->  
        <mapper resource="CarMapper.xml"/>  
    </mappers>  
</configuration>
```

注意：

- `mybatis` 核心配置文件的文件名不一定是 `mybatis-config.xml`，可以是其它名字
- `mybatis` 核心配置文件存放的位置也可以随意，这里选择放在 `resources` 根下，相当于放到了类的根路径下

---

### 4. 配置 XxxMapper.xml 文件

这里将 `CarMapper.xml` 与 `mybatis-config.xml` 放在同一个地方：

```xml
<?xml version="1.0" encoding="UTF-8" ?>  
<!DOCTYPE mapper  
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"  
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">  
<mapper namespace="car">  
    <!--insert sql：保存一个汽车信息-->  
    <insert id="insertCar">  
        insert into t_car  
            (id,car_num,brand,guide_price,produce_time,car_type)  
        values    
	        (null,'102','丰田mirai',40.30,'2014-10-05','氢能源')  
    </insert>  
</mapper>
```

---

### 5. 整体流程

> 每个基于 MyBatis 的应用都是以一个 `SqlSessionFactory` 的实例为核心的。`SqlSessionFactory` 的实例可以通过 `SqlSessionFactoryBuilder` 获得。而 `SqlSessionFactoryBuilder` 则可以从 XML 配置文件来构建出 `SqlSessionFactory` 实例

具体流程：

```
配置文件 → SqlSessionFactoryBuilder → SqlSessionFactory → SqlSession
```

1、`SqlSessionFactoryBuilder`：构建器（临时对象）

> 用来读取 MyBatis 配置文件，构建 `SqlSessionFactory` ，用完即丢，不需要保存

通过 IO 流读取本地文件，调用`build()` 方法解析 XML 文件，然后返回一个 `SqlSessionFactory` 对象：

```java
InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
SqlSessionFactory factory = builder.build(inputStream);
```

2、`SqlSessionFactory`：会话工厂（核心对象）

> 创建 `SqlSession` 对象来执行数据库操作，管理连接

```java
SqlSession session = factory.openSession();  // 获取一个 SqlSession
```

3、`SqlSession`：数据库会话

> 用于执行 SQL 操作（CRUD），每次的数据库操作都要创建一个新的 `SqlSession`

```java
SqlSession session = factory.openSession(); // 默认关闭自动提交
UserMapper mapper = session.getMapper(UserMapper.class); 
List<User> list = mapper.selectAll(); // 调用方法
session.close(); // 必须关闭
```

```
1. 加载配置文件（mybatis-config.xml）
         ↓
2. SqlSessionFactoryBuilder 解析 XML
         ↓
3. 构建 SqlSessionFactory（包含数据源、事务管理器、映射器等信息）
         ↓
4. 调用 factory.openSession() 获取 SqlSession
         ↓
5. 通过 SqlSession 获取 Mapper 接口代理
         ↓
6. 调用 mapper 中方法 → 执行 SQL → 返回结果
```

完整代码：

```java
public class MyBatisIntroductionTest {  
    public static void main(String[] args) {  
        SqlSession sqlSession = null;  
        try {  
            // 1.创建SqlSessionFactoryBuilder对象  
            SqlSessionFactoryBuilder sqlSessionFactoryBuilder = 
	        new SqlSessionFactoryBuilder();  
            // 2.创建SqlSessionFactory对象  
            SqlSessionFactory sqlSessionFactory =  
			sqlSessionFactoryBuilder
			.build(Resources.getResourceAsStream("mybatis-config.xml"));  
            // 3.创建SqlSession对象  
            SqlSession sqlSession = sqlSessionFactory.openSession();  
            // 4.执行SQL  
            int count = sqlSession.insert("insertCar"); // 返回执行的条数 
            System.out.println("更新了几条记录：" + count);  
            // 5.提交  
            sqlSession.commit();  
        } catch (Exception e) {  
            // 回滚  
            if (sqlSession != null) {  
                sqlSession.rollback();  
            }  
            e.printStackTrace();  
        } finally {  
            // 6.关闭  
            if (sqlSession != null) {  
                sqlSession.close();  
            }  
        }  
    }  
}
```

---

### 6. MyBatis 集成日志组件

> 使用 SLF4J + Logback

1、引入相关依赖

```xml
<dependency>  
  <groupId>org.slf4j</groupId>  
  <artifactId>slf4j-api</artifactId>  
  <version>1.7.36</version>  
</dependency>  
<dependency>  
  <groupId>ch.qos.logback</groupId>  
  <artifactId>logback-classic</artifactId>  
  <version>1.2.11</version>  
</dependency>
```

2、配置 Logback 配置文件(resources/logback.xml)

```xml
<?xml version="1.0" encoding="UTF-8"?>  
  
<configuration debug="false">  
    <!-- 控制台输出 -->  
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">  
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">  
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->  
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>  
        </encoder>  
    </appender>  
    <!-- 按照每天生成日志文件 -->  
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">  
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">  
            <!--日志文件输出的文件名-->  
            <FileNamePattern>${LOG_HOME}/TestWeb.log.%d{yyyy-MM-dd}.log</FileNamePattern>  
            <!--日志文件保留天数-->  
            <MaxHistory>30</MaxHistory>  
        </rollingPolicy>  
        <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">  
            <!--格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度%msg：日志消息，%n是换行符-->  
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{50} - %msg%n</pattern>  
        </encoder>  
        <!--日志文件最大的大小-->  
        <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">  
            <MaxFileSize>100MB</MaxFileSize>  
        </triggeringPolicy>  
    </appender>  
  
    <!--mybatis log configure-->  
    <logger name="com.apache.ibatis" level="TRACE"/>  
    <logger name="java.sql.Connection" level="DEBUG"/>  
    <logger name="java.sql.Statement" level="DEBUG"/>  
    <logger name="java.sql.PreparedStatement" level="DEBUG"/>  
  
    <!-- 日志输出级别,logback日志级别包括五个：TRACE < DEBUG < INFO < WARN < ERROR -->  
    <root level="DEBUG">  
        <appender-ref ref="STDOUT"/>  
        <appender-ref ref="FILE"/>  
    </root>  
  
</configuration>
```

3、mybatis-config.xml 的日志配置为“显式优先”行为（可选）

只有在想要强制指定日志实现的时候才需要写，这不是必须的，默认自动识别即可用，很多项目都不配这一项：

```xml
<configuration>
  <settings>
    <setting name="logImpl" value="SLF4J"/>
  </settings>
</configuration>
```

此时控制台就会打印相应的日志信息，可以通过这些信息查看执行的 SQL 语句。

---

### 7. MyBatis 工具类 SqlSessionUtil 的封装

```java
public class SqlSessionUtil {  
    /*  
     * 类加载时初始化 sqlSessionFactory 对象  
     */    private static SqlSessionFactory sqlSessionFactory;  
  
    static {  
        try {  
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    private SqlSessionUtil(){}  
  
    /*  
     * 每调用一次 openSession() 可获取一个新的会话  
     */    public static SqlSession openSession() {  
        return sqlSessionFactory.openSession(); // 手动提交  
    }  
  
    public static void closeSession(SqlSession session) {  
        if (session != null) {  
            session.close();  
        }  
    }  
}
```

---

## 二、MyBatis 的 CRUD

### 1. INSERT

#### 1.1 使用 Map 集合进行传参

```java
// 准备数据  
Map<String, Object> map = new HashMap<>();  
map.put("carNum", "103");  
map.put("brand", "奔驰E300L");  
map.put("guidePrice", 50.3);  
map.put("produceTime", "2020-10-01");  
map.put("carType", "燃油车");  
// 获取SqlSession对象  
SqlSession sqlSession = SqlSessionUtil.openSession();  
// 执行SQL语句
// 第一个参数：sqlId；
// 第二个参数：封装数据的对象，将对象中的数据映射到 sql 语句的占位符中
int count = sqlSession.insert("insertCar", map);  
sqlSession.commit();  
sqlSession.close();  
System.out.println("插入了几条记录：" + count);
```

SQL 中使用 `#{key}` 获取对应的键值：

```xml
<mapper namespace="car">  
    <insert id="insertCar">  
        insert into t_car(car_num,brand,guide_price,produce_time,car_type) values(#{carNum},#{brand},#{guidePrice},#{produceTime},#{carType})  
    </insert>  
</mapper>
```

> 如果占位符中写的与 Map 中的 key 不同的话并不会导致程序出错，但是会导致插入的数据为 Null

---

#### 1.2 使用 POJO 类进行传参

> 使用这种方法需要创建对应的 POJO 实体类，每个字段的名称要与数据库中的对应，以此提高可读性

```java
Car car = new Car();  
car.setCarNum("104");  
car.setBrand("奔驰C200");  
car.setGuidePrice(33.23);  
car.setProduceTime("2020-10-11");  
car.setCarType("燃油车");  
// 获取SqlSession对象  
SqlSession sqlSession = SqlSessionUtil.openSession();  
// 执行SQL，传数据  
int count = sqlSession.insert("insertCarByPOJO", car);  
sqlSession.commit();  
sqlSession.close();  
System.out.println("插入了几条记录" + count);
```

> 需要注意的是：这里的占位符中的 key 是必须和 POJO 实体类的 getter() 方法的名字一样（去掉 get 然后首字母小写），否则就会因为找不到 getter 方法而无法传递参数（如果修改了 getter 方法的名字，那么传参时也要修改成对应的）

```xml
<mapper namespace="car">  
    <insert id="insertCarByPOJO">  
        <!--#{} 里写的是POJO的属性名-->  
        insert into t_car(car_num,brand,guide_price,produce_time,car_type) values(#{carNum},#{brand},#{guidePrice},#{produceTime},#{carType})  
    </insert>  
</mapper>
```

---

### 2. DELETE

```java
SqlSession sqlSession = SqlSessionUtil.openSession();  
int count = sqlSession.delete("deleteByCarNum", "102");  
sqlSession.commit();  
sqlSession.close();  
System.out.println("删除了几条记录：" + count);
```

> 当占位符只有一个的时候，${} 里面的内容可以随便写，不过还是建议和对象的字段保持一致

```xml
<delete id="deleteByCarNum">  
    delete from t_car where car_num = #{xx}  
</delete>
```

---

### 3. UPDATE

```java
Car car = new Car();  
car.setId(12L);  
car.setCarNum("102");  
car.setBrand("比亚迪汉");  
car.setGuidePrice(30.23);  
car.setProduceTime("2018-09-10");  
car.setCarType("电车");  
SqlSession sqlSession = SqlSessionUtil.openSession();  
int count = sqlSession.update("updateCarByPOJO", car);  
sqlSession.commit();  
sqlSession.close();  
System.out.println("更新了几条记录：" + count);
```

```xml
<update id="updateCarByPOJO">  
    update t_car set car_num = #{carNum}, brand = #{brand},guide_price = #{guidePrice},produce_time = #{produceTime},car_type = #{carType} where id = #{id}</update>
```

---

### 4. SELECT（RETRIVE）

在 MyBatis 中，`<select>` 语句会返回一个结果集（resultset），为了让 MyBatis 能够正确地把数据库中的查询结果映射为 Java 对象，必须告诉它结果类型或映射关系，这就涉及到 `resultType` 或 `resultMap` 的配置

#### 4.1 `resultType`

`resultType` 是 MyBatis `<select>` 语句中用来指定查询结果返回值类型的属性：

> 它通常用于将查询结果集自动封装为某个 Java 类型（比如基本类型、pojo、Map 等），本质上 MyBatis 会根据数据库返回的列名，去反射调用 Java 对象中对应的 setter 方法，然后将数据库中查询到的记录封装进对象

resultType 的字段映射规则：

- 数据库字段名和 Java 属性名一致（或符合驼峰转换规则）时自动映射；
- 数据库字段名为 `car_num`，Java 属性名为 `carNum`，也可以映射（前提是开启了驼峰命名自动映射）：

```xml
<settings>
    <setting name="mapUnderscoreToCamelCase" value="true"/>
</settings>
```

> 当字段名和属性名差异太大；涉及嵌套对象、多表联查；需要更精确地控制字段到属性的映射时，就不适合用 `resultType`，需要使用 `<resultMap>`

---

#### 4.2 查询一条数据

```java
SqlSession sqlSession = SqlSessionUtil.openSession();  
Object car = sqlSession.selectOne("selectCarById", 1);  
System.out.println(car);
```

```xml
<select id="selectCarById" resultType="com.cell.pojo.Car">  
    select * from t_car where id = #{id}  
</select>
```

此时的打印结果是这样的，只有 id 和 brand 字段有值，其余为空，这就是因为 Java 底层通过数据库的列名进行映射时没有找到该对象的 setter 方法导致的，所以要么查询时起别名，要么添加驼峰命名自动映射组件

```java
Car{id=1, carNum='null', brand='宝马520Li', guidePrice=null, produceTime='null', carType='null'}
```

---

#### 4.3 查询多条数据

> 使用 `selectList` 方法将查询到的多个对象封装成集合，然后遍历打印

```java
SqlSession sqlSession = SqlSessionUtil.openSession();  
List<Object> cars = sqlSession.selectList("selectCarAll");  
cars.forEach(car -> System.out.println(car));
```

```xml
<!--虽然结果是List集合，但是resultType属性需要指定的是List集合中元素的类型。-->  
<select id="selectCarAll" resultType="com.cell.pojo.Car">   
    select * from t_car  
</select>
```

---

## 三、MyBatis 的两种事务管理器

在 mybatis-config.xml 文件中，可以通过以下配置进行 mybatis 的事务管理：

```xml
<transactionManager type="JDBC"/>      <!-- 默认使用 -->
<transactionManager type="MANAGED"/>   <!-- Spring 环境中常用 -->
```

---

### 1. JDBC 事务管理器

> mybatis 框架自己管理事务，采用原生的 JDBC 代码去管理事务

```java
SqlSession sqlSession = sqlSessionFactory.openSession(); 
// 底层使用的是 conn.setAutoCommit(false); 开启事务
...业务处理...
sqlsession.commit(); // 底层使用的是 conn.commit();手动提交事务
```

`openSession()` 方法默认的 `autoCommit` 是 false，也就是默认要自动提交：

```java
public SqlSession openSession() {
        return this.openSessionFromDataSource(this.configuration.getDefaultExecutorType(), (TransactionIsolationLevel)null, false);
    }
```

这里进入事务管理工厂，通过它来创建事务管理器：

```java
    private SqlSession openSessionFromDataSource(ExecutorType execType, TransactionIsolationLevel level, boolean autoCommit) {
        Transaction tx = null;

        DefaultSqlSession var8;
        try {
            Environment environment = this.configuration.getEnvironment();
            TransactionFactory transactionFactory = this.getTransactionFactoryFromEnvironment(environment);
            tx = transactionFactory.newTransaction(environment.getDataSource(), level, autoCommit);
            Executor executor = this.configuration.newExecutor(tx, execType);
            var8 = new DefaultSqlSession(this.configuration, executor, autoCommit);
        } catch (Exception var12) {
            Exception e = var12;
            this.closeTransaction(tx);
            throw ExceptionFactory.wrapException("Error opening session.  Cause: " + e, e);
        } finally {
            ErrorContext.instance().reset();
        }

        return var8;
    }
```

调用 `transactionFactory.newTransaction()` 来创建具体的事务管理器，进入后可以看到它创建的就是 JDBC 的事务管理器:

```java
    public Transaction newTransaction(DataSource ds, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(ds, level, autoCommit, this.skipSetAutoCommitOnClose);
    }
```

进入 JDBC 具体的构造方法后可以发现此时的 `autoCommit = false`:

```java
    public JdbcTransaction(DataSource ds, TransactionIsolationLevel desiredLevel, boolean desiredAutoCommit, boolean skipSetAutoCommitOnClose) {
        this.dataSource = ds;
        this.level = desiredLevel;
        this.autoCommit = desiredAutoCommit;
        this.skipSetAutoCommitOnClose = skipSetAutoCommitOnClose; // 此时 autoCommit = false
    }
```

然后它会一步一步地走到 openConnection() 方法：

```java
    protected void openConnection() throws SQLException {
        if (log.isDebugEnabled()) {
            log.debug("Opening JDBC Connection");
        }

        this.connection = this.dataSource.getConnection();
        if (this.level != null) {
            this.connection.setTransactionIsolation(this.level.getLevel());
        }

        this.setDesiredAutoCommit(this.autoCommit);
    }
```

然后进入 setDesiredAutoCommit() 方法，设置连接为 false：

```java
this.connection.setAutoCommit(desiredAutoCommit);
```

即 MyBatis 在默认情况下是主动将连接设为非自动提交，也就是可以看作开启事务，但必须在操作后手动 commit

---

如果使用了：

```java
SqlSession sqlSession = sqlSessionFactory.openSession(true); 
// 底层使用的是 conn.setAutoCommit(false); 开启事务
...业务处理...
sqlsession.commit(); // 底层使用的是 conn.commit();手动提交事务
```

那在这个源码的部分，`if (!this.skipSetAutoCommitOnClose && !this.connection.getAutoCommit())` 就会因为 `true != true` 而进不去 `connection.setAutoCommit()` 方法，那么 JDBC 底层就会按照默认的情况，开启自动提交，此时就不需要再手动使用 `sqlsession.commit()`

---

### 2. MANAGED 事务管理器

> mybatis 不再负责事务的管理，事务管理交给其他容器负责，对于当前单纯只有 mybatis 的代码来说，如果配置了 MANAGED 那么事务就是没人管理的，即事务没有打开，那也就不用提交事务

也就是说 `ManagedTransaction` 根本不会控制事务的提交和回滚，它的实现是空操作，所有事务行为都依赖外部容器（如 Spring、JEE 容器等），如果没有外部容器接管，它就默认保持数据库连接的默认行为，通常是 `autoCommit = true`

---

### 3. 总结

> JDBC 中，如果 `autoCommit = true`，则每条 SQL 都是一个隐式事务，执行完立即提交，就不存在可控制的事务边界，此时的 `rollback()` 调用就会变得无效，当业务逻辑中包含多条写操作（update、insert、delete）组合为一个原子操作时，无法保证这些语句的原子性，一旦失败就不能回滚，这是十分不安全的

---
## 四、MyBatis 核心配置文件详解

### 1. SQL Mapper.xml 文件

SQL Mapper 文件（也叫 Mapper 映射文件）是 MyBatis 的核心配置之一，它用于映射 SQL 语句，将 SQL 与 Java 方法进行绑定并定义 SQL 的输入参数和返回结果

例如：

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
    PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.example.mapper.CarMapper">

    <insert id="insertCar" parameterType="com.example.model.Car">
        INSERT INTO car (id, name, type)
        VALUES (#{id}, #{name}, #{type});
    </insert>

</mapper>
```

****
#### 1. namespace 属性的作用

1、`namespace` 是 Mapper 文件的命名空间，用于唯一标识一个 Mapper 接口，它的本质作用是告诉 MyBatis 这个 <mapper> 中的 SQL，
是与哪个 Java 接口进行绑定的，这样 MyBatis 才能将 XML 中的 SQL 映射到 Java 的 CarMapper 接口上


没有写 Mapper 接口也可以使用，但是如果有相同的 SQL id 就需要通过 `namespace` 来区分具体的 SQL ，例如：

```java
List<Object> cars = sqlSession.selectList("car.selectCarAll"); // 此时没有使用 Mapper 接口
```

>所以 `namespace` 是用于唯一标识某个 Mapper 接口，避免命名冲突，并根据 namespace 找到对应的接口来生成代理对象

****
### 2. mybatis-config 配置文件

#### 2.1 environment 标签

`<environment>`标签用于定义一个数据库运行环境，它是 `<environments>` 标签的子元素，代表一个独立的数据库连接配置,可以通过指定 id 选择具体使用的数据库:

```xml
<!--开发环境-->
<environment id="dev">
    <transactionManager type="JDBC"/>
    <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/demo"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </dataSource>
</environment>
        <!--生产环境-->
<environment id="production">
    <transactionManager type="JDBC"/>
    <dataSource type="POOLED">
        <property name="driver" value="com.mysql.cj.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://localhost:3306/mybatis"/>
        <property name="username" value="root"/>
        <property name="password" value="root"/>
    </dataSource>
</environment>
```

>一般一个数据库就对应一个 `SqlSessionFactory` 对象,所以一个环境 `environment` 就对应一个 `SqlSessionFactory` ,

使用时在 `build` 方法里指定 id 即可, `<environments default="development">` 代表默认使用 `id = development` 的数据库:

```java
SqlSessionFactory sqlSessionFactory1 = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"), "dev");
```

****
#### 2.2 dataSource 标签

该标签为程序提供 `Connection` 对象,只要给程序提供 `Connection` 对象的都叫数据源,而数据源实际上是一套规范,由 JDK 规定,也可以手动编写一套数据源组件,
只要实现 `javax.sql.DataSource`接口就行,然后实现接口中的所有方法,这样就拥有了自己的数据源.
比如可以写一个属于自己的数据库连接池(数据库连接池就是提供 `Connection` 对象的,所以它也是一个数据源)

##### 1. 分类

虽然数据源配置是可选的，但如果要启用延迟加载特性，就必须配置数据源。有三种内建的数据源类型（也就是 type="[UNPOOLED|POOLED|JNDI]"）：

1. `UNPOOLED`:不适用连接池

每次获取连接时都会新建一个 `Connection` 对象,用完立即关闭,只适合单元测试或非常轻量级的应用

2. `POOLED`：使用 MyBatis 内置连接池（默认推荐）

会维护一个连接池,避免频繁创建销毁连接,提高性能,并且会自动复用连接

`UNPOOLED` 和 `POOLED` 的区别:

具体见代码:[pooledAndUnpooled.java](./Demo3-Mybatis_config/src/main/java/com/cell/test/pooledAndUnpooled.java)

3.  `JNDI`：使用第三方提供的连接池

`JNDI` 是一套规范,大部分的 Web 容器都实现了这套规范,例如:Tomcat,Jetty,WebLogic等.

****
##### 2. property 标签

`<property>` 标签是 MyBatis 配置文件中常用的配置单元，用来为某个对象或组件指定属性值。其作用类似于 Java 中调用 `setXxx()` 方法对 Java Bean 进行赋值

```xml
<property name="属性名" value="属性值"/>
```

以 `POOLED` 中的属性举例:

- `poolMaximumActiveConnections` – 在任意时间可存在的活动（正在使用）连接数量，默认值：10
- `poolMaximumIdleConnections` – 任意时间可能存在的空闲连接数。
- `poolMaximumCheckoutTime` – 在被强制返回之前，池中连接被检出（checked out）时间，默认值：20000 毫秒（即 20 秒）
- `poolTimeToWait` – 这是一个底层设置，如果获取连接花费了相当长的时间，连接池会打印状态日志并重新尝试获取一个连接（避免在误配置的情况下一直失败且不打印日志），
默认值：20000 毫秒（即 20 秒）。
- `poolMaximumLocalBadConnectionTolerance` – 这是一个关于坏连接容忍度的底层设置， 作用于每一个尝试从缓存池获取连接的线程。 
如果这个线程获取到的是一个坏的连接，那么这个数据源允许这个线程尝试重新获取一个新的连接，
但是这个重新尝试的次数不应该超过 poolMaximumIdleConnections 与 poolMaximumLocalBadConnectionTolerance 之和。 默认值：3（新增于 3.4.5）

```xml
<!--最大连接数-->
<property name="poolMaximumActiveConnections" value="3"/>
<property name="poolTimeToWait" value="20000"/>
<property name="poolMaximumCheckoutTime" value="20000"/>
<property name="poolMaximumIdleConnections" value="1"/>
```

****
##### 3. properties 标签

`<properties>` 标签用于引入外部属性文件或定义内部属性变量，以便在整个配置文件中动态引用变量，从而实现配置的灵活性、可维护性和环境适配性

例如引入外部配置文件:

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mybatis-notes?useSSL=false&amp;serverTimezone=UTC&amp;allowPublicKeyRetrieval=true
jdbc.username=root
jdbc.password=123
```

```xml
<configuration>
    <properties resource="db.properties"/> <!--从当前跟路径查找资源-->
    <!--<properties url="file:///绝对路径"/> --><!--从绝对路径查找资源-->
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="${driver}"/>
                <property name="url" value="${url}"/>
                <property name="username" value="${username}"/>
                <property name="password" value="${password}"/>
            </dataSource>
        </environment>
    </environments>
</configuration>
```

****
##### 4. mapper 标签

`<mapper>` 标签的主要作用是注册 Mapper 文件或 Mapper 接口，使 MyBatis 能够找到并执行其中定义的 SQL 映射语句

```xml

<mappers>
    <mapper resource="CarMapper.xml"/>
</mappers>
```

****
## 五. 手写 MyBatis 框架（掌握原理）

### 1. dom4j 解析 XML 文件

第一步：引入 dom4j 和 jaxen 的依赖

```xml
<!--dom4j依赖-->
<dependency>
    <groupId>org.dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>2.1.3</version>
</dependency>

<!--jaxen依赖-->
<dependency>
<groupId>jaxen</groupId>
<artifactId>jaxen</artifactId>
<version>1.2.0</version>
</dependency>
```

第二步:配置配置文件

第三步:解析 mybatis-config.xml 文件:[ParseXmlByDomej.java](./Demo4-Mybatis_parseXml/src/main/java/com/cell/test/ParseXmlByDome4j.java)





























