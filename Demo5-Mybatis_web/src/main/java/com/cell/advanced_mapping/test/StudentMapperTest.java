package com.cell.advanced_mapping.test;

import com.cell.advanced_mapping.mapper.StudentMapper;
import com.cell.advanced_mapping.pojo.Student;
import com.cell.advanced_mapping.utils.SqlSessionUtil;
import org.junit.Test;

public class StudentMapperTest {
    @Test
    public void testSelectBySid(){
        StudentMapper mapper = SqlSessionUtil.openSession().getMapper(StudentMapper.class);
        Student student = mapper.selectBySid(1);
        System.out.println(student);
    }
}
