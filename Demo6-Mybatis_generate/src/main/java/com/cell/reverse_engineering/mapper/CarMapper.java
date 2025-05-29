package com.cell.reverse_engineering.mapper;

import com.cell.reverse_engineering.pojo.Car;
import com.cell.reverse_engineering.pojo.CarExample;
import java.util.List;

import org.apache.ibatis.annotations.*;

public interface CarMapper {
    long countByExample(CarExample example);

    int deleteByExample(CarExample example);

    int deleteByPrimaryKey(Long id);

    int insert(Car row);

    int insertSelective(Car row);

    List<Car> selectByExample(CarExample example);

    Car selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("row") Car row, @Param("example") CarExample example);

    int updateByExample(@Param("row") Car row, @Param("example") CarExample example);

    int updateByPrimaryKeySelective(Car row);

    int updateByPrimaryKey(Car row);

    List<Car> selectByPage(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    List<Car> selectByPageHelper();

    @Insert(value="insert into t_car values(null,#{carNum},#{brand},#{guidePrice},#{produceTime},#{carType})")
    int insertWithAnnotation(Car car);

    @Delete("delete from t_car where id = #{id}")
    int deleteByIdWithAnnotation(Long id);

    @Update("update t_car set car_num=#{carNum},brand=#{brand},guide_price=#{guidePrice},produce_time=#{produceTime},car_type=#{carType} where id=#{id}")
    int updateWithAnnotation(Car car);

    @Select("select * from t_car where id = #{id}")
    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "car_num", property = "carNum"),
            @Result(column = "brand", property = "brand"),
            @Result(column = "guide_price", property = "guidePrice"),
            @Result(column = "produce_time", property = "produceTime"),
            @Result(column = "car_type", property = "carType")
    })
    Car selectByIdWithAnnotation(Long id);
}