package com.cell.param.mapper;

import com.cell.param.pojo.Student;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface StudentMapper {
    List<Student> selectByNameAndSex(String name, Character sex);

    List<Student> selectByNameAndAge(@Param(value="name") String name, @Param("age") int age);
}
