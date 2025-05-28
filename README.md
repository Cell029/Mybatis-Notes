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

### 8. Mybatis 的三大对象作用域

1. SqlSessionFactoryBuilder

>负责解析 MyBatis 的核心配置文件（如 mybatis-config.xml）,并创建 SqlSessionFactory 实例,是一种构建工具类，用完即丢，不可复用

- 该类可以被自由实例化、使用和丢弃，一旦成功创建了 SqlSessionFactory，就不再需要保留 SqlSessionFactoryBuilder 实例

- 虽然可以用它来创建多个 SqlSessionFactory 实例，但不建议长时间保留该对象，以便及时释放底层 XML 解析资源

- 作用域建议：局部变量（方法内部作用域），每次使用都临时创建并及时销毁

2. SqlSessionFactory

>负责创建 SqlSession 实例，是 MyBatis 与数据库交互的核心入口之一,是线程安全的，可全局复用

- 内部保存了全部配置（如环境信息、数据源、Mapper 映射等）

- 通常在项目启动阶段初始化一次，后续通过单例复用

- 创建一次 SqlSessionFactory 的成本较高,不应频繁创建

- 线程安全，可多线程并发使用，无需额外同步控制

- 作用域建议：全局作用域（应用级别），推荐采用单例模式或将其注册为容器 Bean（如 Spring 中的 @Bean）

3. SqlSession

>用于执行 SQL 语句（select、insert、update、delete）、控制事务、获取 Mapper 接口代理对象等,是短生命周期、非线程安全,每次用完必须关闭

- 每次操作数据库都需获取一个新的 SqlSession 实例

- 它不是线程安全的，不能在多个线程之间共享

- 每个线程、每个请求应独立使用自己的 SqlSession,所以利用 ThreadLocal 与线程绑定

- 使用完毕必须调用 close() 方法关闭，以释放连接资源

- 默认不自动提交事务，需要开发者手动调用 commit() 或使用配置项设置自动提交

- 作用域建议：方法级或请求级（如 HTTP 请求作用域）

****
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

---

#### 1. namespace 属性的作用

1、`namespace` 是 Mapper 文件的命名空间，用于唯一标识一个 Mapper 接口，它的本质作用是告诉 MyBatis 这个 <mapper> 中的 SQL，
是与哪个 Java 接口进行绑定的，这样 MyBatis 才能将 XML 中的 SQL 映射到 Java 的 CarMapper 接口上

没有写 Mapper 接口也可以使用，但是如果有相同的 SQL id 就需要通过 `namespace` 来区分具体的 SQL ，例如：

```java
List<Object> cars = sqlSession.selectList("car.selectCarAll"); // 此时没有使用 Mapper 接口
```

> 所以 `namespace` 是用于唯一标识某个 Mapper 接口，避免命名冲突，并根据 namespace 找到对应的接口来生成代理对象

---

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

> 一般一个数据库就对应一个 `SqlSessionFactory` 对象,所以一个环境 `environment` 就对应一个 `SqlSessionFactory` ,

使用时在 `build` 方法里指定 id 即可, `<environments default="development">` 代表默认使用 `id = development` 的数据库:

```java
SqlSessionFactory sqlSessionFactory1 = sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"), "dev");
```

---

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

3. `JNDI`：使用第三方提供的连接池

`JNDI` 是一套规范,大部分的 Web 容器都实现了这套规范,例如:Tomcat,Jetty,WebLogic等.

---

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

---

##### 3. properties 标签

`<properties>` 标签用于引入外部属性文件或定义内部属性变量，以便在整个配置文件中动态引用变量，从而实现配置的灵活性、可维护性和环境适配性

例如引入外部配置文件:

```properties
jdbc.driver=com.mysql.cj.jdbc.Driver
jdbc.url=jdbc:mysql://localhost:3306/mybatis-notes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
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

---

##### 4. mapper 标签

`<mapper>` 标签的主要作用是注册 Mapper 文件或 Mapper 接口，使 MyBatis 能够找到并执行其中定义的 SQL 映射语句

```xml

<mappers>
    <mapper resource="CarMapper.xml"/>
</mappers>
```

---

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

第四步:解析 sqlMapper.xml 文件:[ParseSqlMapperXML.java](./Demo4-Mybatis_parseXml/src/main/java/com/cell/test/ParseSqlMapperXML.java)

...

---

## 六. 在 web 应用中使用 Mybatis

### 1. MVC 架构模式

#### 1.1 Model（模型）

模型是应用程序的核心部分，它负责处理业务逻辑,封装数据和状态,与数据库进行交互,模型不关心用户界面是如何展示的,也不处理用户的输入

例如:

```java
public class User {
    private String name;
    private int age;
  
    // getters and setters
}
```

---

#### 1.2 View（视图）

视图负责数据的呈现,是用户看到和交互的界面部分.从 Model 获取数据并将数据显示给用户,但不处理业务逻辑.在 Web 应用中，视图通常是 HTML 页面、JSP 页面等

---

#### 1.3 Controller（控制器）

控制器是连接视图和模型的桥梁，它负责接收用户请求并处理请求逻辑,然后调用相应的 Model ,最后选择 View 来展示结果

---

整体流程:

1.用户在视图中发起请求（如点击按钮、提交表单）

2.控制器（Controller）接收到请求

3.控制器调用相应的模型（Model）处理数据

4.模型返回处理结果

5.控制器将数据传递给视图（View）

6.视图将数据展示给用户

---

#### 1.2 具体代码流程[bank](./Demo5-Mybatis_web/src/main/java/com/cell/bank)

##### 1. 项目分层结构

1. dao:数据访问层

该层主要负责数据访问，属于 Model 的一部分:

定义了一个 `AccountDao` 接口,声明了两个方法,一个查询账户(将数据库中查询到的数据封装到 `Account` 对象中),一个更新账户, `AccountDaoImpl` 则是对应接口的实现类,具体实现那两个方法

2. service:业务逻辑层

该层主要负责业务逻辑的封装,降低 Controller 和 Dao 层的耦合度,属于 Model 的一部分:

定义了一个 `AccountService` 接口,声明了一个转账的方法, `AccountServiceImpl` 是实现类,封装具体的业务,通常在这里进行事务的管理

3. web:控制器层

该层主要用于接收用户请求、协调业务逻辑、返回响应,属于 Controller 的一部分,是连接前端（View）和后端业务逻辑（Model）的桥梁:

通过 `AccountController` 获取前端提交的信息,然后把这些信息封装成对象,调用 service 层封装的业务方法,并将最终结果返回给 view

---

##### 2. 总体流程

```text
用户请求（转账）
     ↓
AccountController（封装参数，调用 service）
     ↓
AccountServiceImpl（执行业务，调用 dao，并进行事务管理）
     ↓
AccountDaoImpl（调用 mapper 文件执行 SQL）
     ↓
数据库操作（查询账户、更新余额）
     ↑
返回结果（封装 Account 对象或更新条数）
     ↑
处理异常（如余额不足、数据库异常）
     ↑
AccountController 返回响应（成功或失败）
```

---

##### 3. MVC 架构模式与三层架构模式的关系

MVC:


| 组件                     | 作用                                 | 举例                       |
| ------------------------ | ------------------------------------ | -------------------------- |
| **Model（模型）**        | 业务逻辑和数据模型                   | Java Bean、POJO、Service   |
| **View（视图）**         | 用户界面                             | HTML、JSP、Vue 等前端页面  |
| **Controller（控制器）** | 接收请求，调用业务逻辑，选择视图响应 | Servlet、Spring Controller |

三层架构:


| 层                                  | 作用               | 举例                  |
| ----------------------------------- | ------------------ | --------------------- |
| **表示层（Presentation Layer）**    | 处理用户请求与响应 | Controller、JSP、前端 |
| **业务逻辑层（Business Layer）**    | 处理具体业务逻辑   | Service               |
| **数据访问层（Data Access Layer）** | 与数据库进行交互   | DAO、MyBatis、JPA 等  |

相互对应关系:


| MVC 架构       | 三层架构                         | 实际类举例                                |
| -------------- | -------------------------------- | ----------------------------------------- |
| **View**       | 表示层（的一部分）               | JSP、HTML、Thymeleaf、Vue                 |
| **Controller** | 表示层（核心控制器）             | Spring Controller、Servlet                |
| **Model**      | 业务层 + 持久层（Service + DAO） | Service、ServiceImpl、DAO、Entity、Mapper |

---

#### 1.3 事务控制

原始代码中事务的控制处在 dao 层,此时没有业务逻辑的封装,难以判断当前程序是否出现异常,并且每执行一次 SQL 都会创建一个新的 `SqlSession` 对象,
这就导致即使判断除了异常问题,也无法通过一次 commit 管理两个 `SqlSession` 对象,所以需要在 service 层中进行事务的控制,让一个 `SqlSession` 对象执行多条 SQL

```java
SqlSession sqlSession = SqlSessionUtil.openSession();
Account act = (Account) sqlSession.selectOne("selectByActno", actno);
sqlSession.commit();
sqlSession.close();
return act;
```

---

##### 1. Tomcat 的工作机制：使用线程池处理请求

> Tomcat 是基于多线程的 Servlet 容器，它在启动时会创建一个线程池,每当有一个新的 HTTP 请求到来时，Tomcat 会从线程池中取出一个空闲线程来处理这个请求,
> 这个线程会从 Servlet 的入口开始执行，比如调用 doGet() / doPost() / Controller 方法,接下来会按调用链执行：Controller → Service → DAO → DB 查询...

但是整个过程中，都是由同一个线程执行的!

以代码为例:[Test1.java](./Demo5-Mybatis_web/src/main/java/com/cell/testThreadLocal/test1/Test1.java)

```text
Thread[#1,main,5,main]-main
Thread[#1,main,5,main]-UserService
Thread[#1,main,5,main]-UserDao
```

这样的设计可以保证每个请求独占一个线程，不会干扰其他请求(线程安全),并且可以保证线程的上下文共享,
比如 ThreadLocal、事务、日志 traceId、Session 都需要在一个线程中完整执行,
同时可以避免频繁创建销毁线程,提高效率

> 所以依赖这种技术可以实现事务的管理(JDBC 中 `Connection` 对象管理事务的提交, Mybatis 中 `SqlSession` 对象管理事务的提交)

所以可以使用 Map 集合进行统一管理, key 绑定当前请求的线程, value 绑定 `Connection` 或 `SqlSession`,当获取到绑定的线程时,
就将 `Connection` 或 `SqlSession` 取出,保证每一次操作时使用的是同一个对象,即保证每次的操作都是同一个事务在管理资源,避免多次传递 `Connection` 或 `SqlSession`

---

##### 2. ThreadLocal 管理事务

手动模拟一个简易的 ThreadLocal:[Test1.java](./Demo5-Mybatis_web/src/main/java/com/cell/testThreadLocal/test1/Test1.java)

添加 ThreadLocal 后的完整代码:[bank](./Demo5-Mybatis_web/src/main/java/com/cell/bank)

---
## 七. 使用 Javassist 生成 DaoImpl 类

手动封装一个简易的代理机制:[GenerateDaoProxy.java](./Demo5-Mybatis_web/src/main/java/com/cell/bank/utils/GenerateDaoProxy.java)

总结:这个例子很好的解释了为什么 sqlMapper.xml 文件中的 namespace 强制要求写接口的全限定名,否则会有警告提示,就是为了底层的反射机制可以找到这个接口,然后自动生成对应的实现类,
以及 sqlMapper.xml 中的 sqlId 为什么要与接口中的方法名同名,不过方法中的参数没有强制规定,因为反射时会动态地使用参数名

```text
 <select id="selectByActno" resultType="com.cell.bank.pojo.Account">
 Account selectByActno(String actno);
```

****
基于以上内容,现在可以以面向接口的方式再进行一次 CRUD:[CarMapperTest.java](./Demo5-Mybatis_web/src/main/java/com/cell/crudByInterface/test/CarMapperTest.java)

****
## 八. Mybatis 小技巧

### 1. #{} 和 ${}

#{} 占位符（预编译占位符）:

是用来传递参数值的(具体以传递的值的类型有关), MyBatis 会将 #{} 解析为 JDBC 的 `?` 占位符,并在 SQL 执行前通过 PreparedStatement 进行参数绑定

```sql
SELECT * FROM users WHERE id = #{id};
-- 等价于
SELECT * FROM users WHERE id = ?;
-- SELECT * FROM users WHERE id = 5;
```

如果传递的参数是字符串类型的话,就会自动拼接上 `''`

${} 占位符（字符串拼接占位符）:

MyBatis 会将 ${} 直接替换成传入的值，没有使用参数绑定，属于动态 SQL 拼接

```sql
SELECT * FROM ${tableName} WHERE id = ${id};
-- 如果传递 5 OR 1=1 
SELECT * FROM users WHERE id = 5 OR 1=1
-- 此时直接进行了拼接,就导致 SQL 注入问题
```

注意:

- ${} 不要用来传值，只用来拼结构

- 动态拼接字段时，如果不能避免 ${}，要严格校验传入参数是否合法

- #{} 可以安全传递各种类型：数字、字符串、日期、布尔等,但不是很适用于动态拼接

- 不要在 where 语句或值传递场景中误用 ${}，会带来 SQL 注入风险

****
### 2. 批量删除

可以利用 ${} 的动态拼接的特性来实现批量删除,例如传递一个字符串 "156,157,158"

```sql
delete from t_car where id in(${ids});
-- 等价于
delete from t_car where id in(156,157,158); 
```

****
### 3. 模糊查询

sql 语句的模糊查询是这样的: like '%XXX%',如果用 #{} 来动态查询的话是会报错的,因为 #{} 会被识别为 ? ,而 ? 在 '' 中会被识别为普通的字符串

1. 使用 '%${XXX}%'
2. 使用 concat 函数,实现字符串的拼接: concat('%', #{}, '%')
3. "%"#{}"%" -> "%"?"%",需要注意的是不能使用 '%'#{}'%',SQL 字符串本身需要用单引号 ' 包裹，而 MyBatis 的 #{} 占位符解析时会尝试保留单引号，导致语法错误
例如:

```sql
SELECT * FROM t_car WHERE brand LIKE '%'丰田'%'
```

****
### 4. typeAliases 标签起别名

起别名后就可以在 sqlMapper.xml 文件的 resultType 中使用别名,不用再写全限定类名了,但 namespace 不适用

[mybatis-config.xml](./Demo5-Mybatis_web/src/main/resources/mybatis-config.xml)

1. typeAlias

```xml
<typeAliases>
  <typeAlias type="com.cell.bank.pojo.Car" alias="Car"/>
</typeAliases>
```

typeAliases 标签中的 typeAlias 可以写多个,type 属性：指定给哪个类起别名; alias属性：别名.

2. package

如果一个包下的类太多，每个类都要起别名，会导致 typeAlias 标签配置较多，所以 mybatis 用提供 package 的配置方式，
只需要指定包名，该包下的所有类都自动起别名，别名就是简类名,并且别名不区分大小写

```xml
<typeAliases>
  <package name="com.cell.bank.pojo"/>
</typeAliases>
```

****
### 5. mappers 标签

在 MyBatis 中，<mapper> 标签用于定义与一个接口（Mapper 接口）绑定的 SQL 映射语句:[mybatis-config.xml](./Demo5-Mybatis_web/src/main/resources/mybatis-config.xml)

1. <mapper resource=""/>

从类路径（classpath）根目录开始查找,也就是从 resources/ 目录的根开始定位,当 sqlMapper.xml 文件放在 resources 中时可以使用

```css
src/
 └── main/
      └── resources/
           └── mappers/
                └── sqlMapper.xml
```

2. <mapper url=""/>

从本地文件开始查找:file:// 绝对路径

```xml
<mapper url="file:///D:/mybatis/mappers/sqlMapper.xml"/>
```

3. <mapper class="全限定类名"/>:用来告诉 MyBatis 通过接口类的全路径来加载 Mapper

使用这种方式的前提是 sqlMapper.xml 文件的位置不能随便放,必须和对应的 Mapper 接口"放在一起",也就是在 resources 中创建和 Mapper 接口一样的路径(用 '/' 代替 '.'),
Java 项目构建时,资源文件（resources 目录下的文件）会被按相同的包路径结构复制到输出目录（如 target/classes）,而 Java 类编译后的 .class 文件也会按照包路径放置在同一输出目录,
这样做方便运行时按包路径查找资源，MyBatis 在运行时通过类路径加载 XML 文件时，能直接定位到对应路径

4. <package name="com.cell.crudByInterface.mapper"/>

当一个包下有多个接口时,就需要对应多个 sqlMapper.xml 文件,就可以使用这种方式管理所有的 sqlMapper.xml 文件

****
### 6. 配置模板文件

打开 settings 中的 editor,找到 File and Code Templates:(File | Settings | Editor | File and Code Templates),添加相应的文件即可

****
### 7. 插入数据时获取自动生成的主键

在 sql 标签后面加上 useGeneratedKeys 和 keyProperty:

```xml
<insert id="insertCar" useGeneratedKeys="true" keyProperty="id">
    INSERT INTO t_car (name, price, brand)
    VALUES (#{name}, #{price}, #{brand})
</insert>
```

- `useGeneratedKeys="true"`:它会在执行完 insert 语句之后获取数据库自动生成的主键值
-  `keyProperty="id"`:把数据库生成的主键值，赋值回参数对象中的字段,即等号后面的字段

****
## 九. Mybatis 参数处理

### 1. 单个参数

1. 当传入的是基本类型或字符串时，MyBatis 会自动将参数的名字设置为 param 或 arg

```xml
<!--这里的 #{id} 表达式中的 id，MyBatis 实际会解析为参数名。
虽然传的是 int，MyBatis 默认会用 id、param 或 arg 来作为引用名-->
<select id="selectUserById" resultType="User" parameterType="int">
    SELECT * FROM user WHERE id = #{id}
</select>
```

2. 如果传入的是一个 JavaBean 对象，MyBatis 会根据属性名称来解析

```xml
<!--MyBatis 会通过反射获取 user 对象中的属性，如 id 和 name，来替换 #{id} 和 #{name}-->
<select id="selectStudent" resultType="Student" parameterType="Student">
    SELECT * FROM t_student
    WHERE id = #{id} AND name = #{name}
</select>
```

****
### 2. 多参数

1. 传递 Map 参数

传递 Map 参数时, sqlMapper 文件中的 sql 语句的 #{} 中应该填写和 Map 的 key 一样的内容,否则就无法获取到对应的 value,
因为 MyBatis 在处理 Map 类型参数时，会自动把 Map 作为参数对象本身，在解析 #{} 占位符时，直接通过 key 从这个 Map 中获取对应的值,例如:

```java
Map<String, Object> paramMap = new HashMap<>();
paramMap.put("name", "张三");
paramMap.put("age", 18);
// MyBatis 在执行 SQL 时会自动做类似的事情：
String name = paramMap.get("name");
Integer age = paramMap.get("age");
```

然后再在 sqlMapper 文件中获取值

```xml
<select id="selectByMap" resultType="Student">
  SELECT * FROM t_student WHERE name = #{name} AND age = #{age}
</select>
```

2. 但是如果不是传递 Map 参数,并且直接使用自己命名的参数作为接收值的话, Mybatis 就无法像单个参数一样自动识别出来,并且会抛出异常信息

```java
List<Student> students = mapper.selectByNameAndSex("张三", '女');
```

```xml
<select id="selectByNameAndSex" resultType="Student">
  select * from t_student where name = #{name} and sex = #{sex}
</select>
```

```text
Parameter 'name' not found. Available parameters are [arg1, arg0, param1, param2]
Parameter 'name' not found. Available parameters are [arg1, arg0, param1, param2]
```

但当我根据抛出的异常信息将两个参数名设置为 arg0 和 arg1 后, Mybatis 就能识别出来了

```xml
<select id="selectByNameAndSex" resultType="Student">
  select * from t_student where name = #{arg0} and sex = #{arg1}
</select>
```

****
### 3. @Param 注解

所以基于 Mybatis 可以识别 Map 集合的这种特性,它提供了 @Param 注解来达到类似的效果,也就是通过把 @Param(value = ) 中的 value 作为 key,
传递的参数作为 value 绑定,达到自定义传递参数名的效果,就不用再使用 arg0,arg1 了,提高了可读性

```java
// 使用注解后就可以在 sqlMapper 文件中使用自己定义的参数名了
List<Student> selectByNameAndAge(@Param(value="name") String name, @Param("age") int age);
```
****
## 十. 查询语句专题
[CarMapperTest.java](./Demo5-Mybatis_web/src/main/java/com/cell/select/test/CarMapperTest.java)
### 1. 返回 Map 

当返回的数据没有合适的实体类对应的话，可以采用Map集合接收。字段名做 key，字段值做 value.查询如果可以保证只有一条数据，则返回一个Map集合即可

****
### 2. 返回多个 Map

查询结果条数大于等于 1 条数据，则可以返回一个存储 Map 集合的 List 集合,例如 List<Map> 等同于 List<Car>,即一个 Map 对应一个 pojo

****
### 3. 返回大 Map

采用上一个 List 包含 Map 的方式有个弊端,就是想要获取 Map 中某个 key 的那个 Map 就很麻烦,
所以可以采用 Map 套 Map 的方式,用某个字段作为外层 Map 的 key (例如使用主键 id,通过 @MapKey("id") 来使用)

****
### 4. 结果映射

查询结果的列名和java对象的属性名对应不上怎么办？

- 第一种方式：as 给列起别名
- 第二种方式：使用 resultMap 进行结果映射:[CarMapper.xml](./Demo5-Mybatis_web/src/main/resources/com/cell/select/mapper/CarMapper.xml)
- 第三种方式：是否开启驼峰命名自动映射（配置settings）

****
## 十一. 动态 SQL 

[CarMapper.xml](./Demo5-Mybatis_web/src/main/resources/com/cell/dynamic_sql/mapper/CarMapper.xml)

### 1. if 标签

`<if>` 标签属于 MyBatis 的动态 SQL 标签，用于根据参数值是否满足条件来动态生成 SQL 的一部分

****

 ### 2. where 标签

`<where>` 标签在多个动态条件拼接的 SQL 中,自动处理 where 关键字的添加与多余逻辑运算符（如 AND、OR）的清理，简化 SQL 拼接工作

****
### 3. trim 标签

`<trim>` 标签对 SQL 子句的前缀、后缀、多余的分隔符等内容进行自动裁剪和添加

****
### 4. set 标签

`<set>` 标签是专门用于构建 UPDATE 语句中 SET 子句的动态 SQL 标签,它主要用于自动处理多条件更新时字段间的逗号 `,` 问题,某字段是否需要修改主要由 `<if>` 标签控制

****
### 5. choose 标签(when otherwise) 

`<choose>` 标签是用于实现类似 Java 中 if...else if...else 的逻辑判断结构的标签,它常用于多个条件之间具有互斥关系的场景中,但最终只会有一个分支被选择

****

### 6. foreach 标签

`<foreach>` 主要用于处理集合类型参数的动态 SQL 拼接，比如批量查询、批量插入、构建 in 语句等

| 属性           | 说明                               |
| ------------ | -------------------------------- |
| `collection` | 指定遍历的集合名，通常是 List、数组、Map 等       |
| `item`       | 每次遍历时的当前元素的变量名                   |
| `index`      | 当前迭代元素的索引（对于 List 是下标，Map 是 key） |
| `open`       | 循环体的前缀字符串（如 `(`）                 |
| `separator`  | 每个元素之间的分隔符（如 `,`）                |
| `close`      | 循环体的后缀字符串（如 `)`）                 |

****
### 7. sql 标签和 include 标签

用于提取和复用 SQL 片段,当某个 sql 语句经常被使用时,就可以用 `<sql>` 标签标记,通过 `<include>` 标签使用这些片段

****




















