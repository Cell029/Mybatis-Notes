package com.cell.dynamic_sql.test;

import com.cell.dynamic_sql.mapper.CarMapper;
import com.cell.dynamic_sql.pojo.Car;
import com.cell.dynamic_sql.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CarMapperTest {
    @Test
    public void testSelectByMultiCondition() {
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByMultiCondition("奔驰", 50.3, "燃油车");
        System.out.println(cars);
    }

    @Test
    public void testSelectByMultiConditionWithWhere() {
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByMultiConditionWithWhere("丰田", 20.0, "燃油车");
        System.out.println(cars);
    }

    @Test
    public void testSelectByMultiConditionWithTrim() {
        CarMapper mapper = SqlSessionUtil.openSession().getMapper(CarMapper.class);
        List<Car> cars = mapper.selectByMultiConditionWithTrim("宝马", null, "");
        System.out.println(cars);
    }

    @Test
    public void testUpdateWithSet() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Car car = new Car(7L, "7", "丰田霸道", 10.0, "", null);
        mapper.updateWithSet(car);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectWithChoose() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        // select * from t_car WHERE produce_time >= ''
        List<Car> cars = mapper.selectWithChoose("", null, "");
        cars.forEach(System.out::println);
    }

    @Test
    public void testDeleteBatchByForeach(){
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Long[] ids = {9L, 10L, 11L};
        mapper.deleteBatchByForeach(ids);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testInsertBatchByForeach() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Car car1 = new Car(null, "2001", "兰博基尼", 100.0, "1998-10-11", "燃油车");
        Car car2 = new Car(null, "2001", "兰博基尼", 100.0, "1998-10-11", "燃油车");
        Car car3 = new Car(null, "2001", "兰博基尼", 100.0, "1998-10-11", "燃油车");
        List<Car> cars = Arrays.asList(car1, car2, car3);
        mapper.insertBatchByForeach(cars);
        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void testSelectAllRetMap() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        Map<Long, Car> longCarMap = mapper.selectAllRetMap();
        sqlSession.commit();
        sqlSession.close();
        longCarMap.forEach((k, v) -> {
            System.out.println(k + "=" + v);
        });
    }

}
