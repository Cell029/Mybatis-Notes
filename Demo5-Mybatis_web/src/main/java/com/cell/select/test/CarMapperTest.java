package com.cell.select.test;

import com.cell.select.mapper.CarMapper;
import com.cell.select.pojo.Car;
import com.cell.select.utils.SqlSessionUtil;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class CarMapperTest {
    @Test
    public void testSelectByIdRetMap(){
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        Map<String,Object> car = mapper.selectByIdRetMap(13L);
        System.out.println(car);
    }

    @Test
    public void testSelectAllRetListMap(){
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        List<Map<String,Object>> cars = mapper.selectAllRetListMap();
        System.out.println(cars);
    }

    @Test
    public void testSelectAllRetMap(){
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        Map<Long,Map<String,Object>> cars = mapper.selectAllRetMap();
        System.out.println(cars);
    }

    @Test
    public void testSelectAllByResultMap(){
        CarMapper carMapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        List<Car> cars = carMapper.selectAllByResultMap();
        System.out.println(cars);
    }

    @Test
    public void testSelectTotal(){
        CarMapper carMapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        Long total = carMapper.selectTotal();
        System.out.println(total);
    }
}
