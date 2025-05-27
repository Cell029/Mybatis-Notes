package com.cell.crudByInterface.test;

import com.cell.crudByInterface.mapper.CarMapper;
import com.cell.crudByInterface.pojo.Car;
import com.cell.crudByInterface.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;


public class CarMapperTest {
    @Test
    public void testInsert() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // 面向接口获取接口的代理对象
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = new Car(null, "8654", "凯美瑞", 3.0, "2000-10-10", "新能源");
        int count = carMapper.insert(car);
        sqlSession.commit();
        sqlSession.close();
        System.out.println(count);
    }

    @Test
    public void testDelete() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        int count = carMapper.delete(154L);
        sqlSession.commit();
        sqlSession.close();
        System.out.println(count);
    }

    @Test
    public void testUpdate() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = new Car(11L,"123", "宝马", 49.9, "2023-10-11", "新能源");
        carMapper.update(car);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectById() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        Car car = carMapper.selectById(1L);
        System.out.println(car);
    }

    @Test
    public void testSelectAll() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper carMapper = sqlSession.getMapper(CarMapper.class);
        List<Car> carList = carMapper.selectAll();
        carList.forEach(System.out::println);
    }
}
