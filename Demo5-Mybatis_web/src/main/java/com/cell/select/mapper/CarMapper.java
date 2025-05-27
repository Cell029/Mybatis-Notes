package com.cell.select.mapper;

import com.cell.select.pojo.Car;
import org.apache.ibatis.annotations.MapKey;

import java.util.List;
import java.util.Map;

public interface CarMapper {
    Map<String, Object> selectByIdRetMap(Long id);

    List<Map<String,Object>> selectAllRetListMap();

    @MapKey("id")
    Map<Long,Map<String,Object>> selectAllRetMap();

    List<Car> selectAllByResultMap();

    Long selectTotal();
}
