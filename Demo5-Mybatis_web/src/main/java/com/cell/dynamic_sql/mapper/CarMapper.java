package com.cell.dynamic_sql.mapper;

import com.cell.dynamic_sql.pojo.Car;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface CarMapper {
    List<Car> selectByMultiCondition(@Param("brand") String brand, @Param("guidePrice") Double guidePrice, @Param("carType") String carType);

    List<Car> selectByMultiConditionWithWhere(@Param("brand") String brand, @Param("guidePrice") Double guidePrice, @Param("carType") String carType);

    List<Car> selectByMultiConditionWithTrim(@Param("brand") String brand, @Param("guidePrice") Double guidePrice, @Param("carType") String carType);

    int updateWithSet(Car car);

    List<Car> selectWithChoose(@Param("brand") String brand, @Param("guidePrice") Double guidePrice, @Param("produceTime") String produceTime);

    int deleteBatchByForeach(@Param("ids") Long[] ids);

    int insertBatchByForeach(@Param("cars") List<Car> cars);

    @MapKey("id")
    Map<Long, Car> selectAllRetMap();
}

