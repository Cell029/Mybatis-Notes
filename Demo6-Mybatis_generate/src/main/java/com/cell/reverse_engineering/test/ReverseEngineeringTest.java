package com.cell.reverse_engineering.test;

import com.cell.reverse_engineering.mapper.CarMapper;
import com.cell.reverse_engineering.pojo.Car;
import com.cell.reverse_engineering.pojo.CarExample;
import com.cell.reverse_engineering.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

public class ReverseEngineeringTest {
    @Test
    public void test1() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);

        // 1. 查询一个
        Car car = mapper.selectByPrimaryKey(14L);
        System.out.println(car);

        // 2. 查询所有(selectByExample 根据条件查询,如果条件是 null,则表示没有条件,也就是查询所有)
        List<Car> cars = mapper.selectByExample(null);
        cars.forEach(System.out::println);

        // 3. 按条件查询
        // QBC 风格: Query By Criteria,一种查询方式,比较面向对象,一般看不到 sql 语句
        // 通过 XxxExample 对象来封装查询条件
        CarExample carExample = new CarExample();
        // 调用 carExample.createCriteria() 方法来创建查询条件
        carExample.createCriteria()
                .andBrandLike("博") // 模糊查询带有 "博" 的车
                .andGuidePriceGreaterThan(new BigDecimal("60.0")); // 查询价格大于 60.0

        // 添加 or
        carExample.or().andCarTypeEqualTo("燃油车"); // 查询或者汽车类型为燃油车的
        // 执行查询
        List<Car> cars1 = mapper.selectByExample(carExample);
        cars1.forEach(System.out::println);


    }
}
