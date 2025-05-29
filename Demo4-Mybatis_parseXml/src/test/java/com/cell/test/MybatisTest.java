package com.cell.test;


import com.cell.core.SqlSession;
import com.cell.core.SqlSessionFactory;
import com.cell.core.SqlSessionFactoryBuilder;
import com.cell.pojo.User;
import com.cell.utils.Resources;
import org.junit.Test;

public class MybatisTest {
    @Test
    public void testSqlSessionFactory() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourcesAsStream("mybatis-config.xml"));
        System.out.println(sqlSessionFactory);
    }

    @Test
    public void testSqlSessionInsert() {
        User user = new User("2", "lisi", "23");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourcesAsStream("mybatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int count = sqlSession.insert("User.insertUser", user);
        System.out.println("插入了几条记录：" + count);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSqlSessionSelectOne() {
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBuilder.build(Resources.getResourcesAsStream("mybatis-config.xml"));
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Object object = sqlSession.selectOne("User.selectUserById", "1");
        System.out.println(object);
    }

}
