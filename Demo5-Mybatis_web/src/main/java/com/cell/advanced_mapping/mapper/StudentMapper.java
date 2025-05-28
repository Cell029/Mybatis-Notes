package com.cell.advanced_mapping.mapper;

import com.cell.advanced_mapping.pojo.Student;

import java.util.List;

public interface StudentMapper {
    Student selectBySid(int id);

    List<Student> selectByCid(Integer cid);
}
