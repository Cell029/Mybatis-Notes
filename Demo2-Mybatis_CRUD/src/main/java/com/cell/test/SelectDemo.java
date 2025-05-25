package com.cell.test;

import com.cell.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.List;

/**
 * 功能：
 * 作者：Cell
 * 日期：2025/5/24 22:09
 */
public class SelectDemo {
    public static void main(String[] args) {
        /*SqlSession sqlSession = SqlSessionUtil.openSession();
        // 执行SQL语句
        Object car = sqlSession.selectOne("selectCarById", 1);
        System.out.println(car);*/

        SqlSession sqlSession = SqlSessionUtil.openSession();
        List<Object> cars = sqlSession.selectList("selectCarAll");
        cars.forEach(car -> System.out.println(car));
    }
}
