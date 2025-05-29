package com.cell.reverse_engineering.test;

import com.cell.reverse_engineering.mapper.CarMapper;
import com.cell.reverse_engineering.pojo.Car;
import com.cell.reverse_engineering.utils.SqlSessionUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class PageTest {
    @Test
    public void testSelectByPage() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);
        // 获取每页的显示记录条数
        int pageSize = 3;
        // 页码
        int pageNum = 2;
        // 每页首条数据的下标
        int startIndex = (pageNum - 1) * pageSize;
        List<Car> cars = mapper.selectByPage(startIndex, pageSize);
        cars.forEach(System.out::println);
    }

    @Test
    public void testSelectByPageHelper() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        CarMapper mapper = sqlSession.getMapper(CarMapper.class);

        // 开启分页
        PageHelper.startPage(2, 2);
        // 执行查询语句
        List<Car> cars = mapper.selectByPageHelper();
        // 获取分页信息对象
        PageInfo<Car> pageInfo = new PageInfo<>(cars, 5);
        List<Car> list = pageInfo.getList();
        list.forEach(System.out::println);
        System.out.println(pageInfo);
    }
}
