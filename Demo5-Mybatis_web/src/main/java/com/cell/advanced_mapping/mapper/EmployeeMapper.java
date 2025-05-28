package com.cell.advanced_mapping.mapper;

import com.cell.advanced_mapping.pojo.Employee;

import java.util.List;

public interface EmployeeMapper {
    List<Employee> selectEmployeeWithProjects(Integer id);
}
