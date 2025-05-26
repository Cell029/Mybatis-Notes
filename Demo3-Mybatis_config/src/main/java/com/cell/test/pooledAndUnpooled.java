package com.cell.test;

import com.cell.pojo.Car;
import com.cell.utils.SqlSessionUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class pooledAndUnpooled {
    public static void main(String[] args) throws IOException {
        Car car = new Car();
        car.setCarNum("133");
        car.setBrand("丰田霸道");
        car.setGuidePrice(50.3);
        car.setProduceTime("2020-01-10");
        car.setCarType("燃油车");
        SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory =
                sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml")); // 使用 POOLED
        // Created connection 20853837.
        // Returned connection 20853837 to pool. 两次使用的连接对象都是同一个

        /*SqlSessionFactory sqlSessionFactory =
                sqlSessionFactoryBuilder.build(Resources.getResourceAsStream("mybatis-config.xml"),"unpooled"); // 使用 POOLED
        // [com.mysql.cj.jdbc.ConnectionImpl@4686afc2]
        // [com.mysql.cj.jdbc.ConnectionImpl@790da477] 两次使用的连接对象都不一样
                */
        SqlSession sqlSession = sqlSessionFactory.openSession();
        int count = sqlSession.insert("insertCar", car);
        sqlSession.commit();
        sqlSession.close();
        System.out.println("插入了几条记录：" + count);

        SqlSession sqlSession1 = sqlSessionFactory.openSession();
        int count1 = sqlSession1.insert("insertCar", car);
        sqlSession1.commit();
        sqlSession1.close();
        System.out.println("插入了几条记录：" + count1);

    }
}
