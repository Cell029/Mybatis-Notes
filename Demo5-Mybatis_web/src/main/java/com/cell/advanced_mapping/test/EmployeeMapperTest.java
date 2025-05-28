package com.cell.advanced_mapping.test;

import com.cell.advanced_mapping.mapper.EmployeeMapper;
import com.cell.advanced_mapping.pojo.Employee;
import com.cell.bank.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class EmployeeMapperTest {
    @Test
    public void testSelectEmployeeWithProjects() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        EmployeeMapper mapper = sqlSession.getMapper(EmployeeMapper.class);
        List<Employee> employees = mapper.selectEmployeeWithProjects(1);
        System.out.println(employees);
    }
}
