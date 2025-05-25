package com.cell.test;

import com.cell.pojo.Car;
import com.cell.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 功能：
 * 作者：Cell
 * 日期：2025/5/24 21:52
 */
public class UpdateDemo {
    public static void main(String[] args) {
        Car car = new Car();
        car.setId(12L);
        car.setCarNum("102");
        car.setBrand("比亚迪汉");
        car.setGuidePrice(30.23);
        car.setProduceTime("2018-09-10");
        car.setCarType("电车");
        // 获取SqlSession对象
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // 执行SQL语句
        int count = sqlSession.update("updateCarByPOJO", car);
        sqlSession.commit();
        sqlSession.close();
        System.out.println("更新了几条记录：" + count);
    }
}
