package com.cell.test;

import com.cell.pojo.Car;
import com.cell.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class InsertDemo {
    public static void main(String[] args) {
       /* // 准备数据
        Map<String, Object> map = new HashMap<>();
        map.put("carNum", "103");
        map.put("brand", "奔驰E300L");
        map.put("guidePrice", 50.3);
        map.put("produceTime", "2020-10-01");
        map.put("carType", "燃油车");
        // 获取SqlSession对象
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // 执行SQL语句（使用map集合给sql语句传递数据）
        int count = sqlSession.insert("insertCar", map);
        sqlSession.commit();
        sqlSession.close();
        System.out.println("插入了几条记录：" + count);*/

        // 创建POJO，封装数据
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
    }
}
