package com.cell.param.mapper;

import com.cell.param.pojo.Car;

import java.util.List;

public interface CarMapper {
    int insert(Car car);

    int delete(Long id);

    int update(Car car);

    Car selectById(Long id);

    List<Car> selectAll();
}
