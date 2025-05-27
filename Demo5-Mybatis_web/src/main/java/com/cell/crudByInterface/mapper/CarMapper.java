package com.cell.crudByInterface.mapper;

import com.cell.crudByInterface.pojo.Car;

import java.util.List;

public interface CarMapper {
    int insert(Car car);

    int delete(Long id);

    int update(Car car);

    Car selectById(Long id);

    List<Car> selectAll();
}
