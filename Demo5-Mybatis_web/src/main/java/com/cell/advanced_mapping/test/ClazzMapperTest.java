package com.cell.advanced_mapping.test;

import com.cell.advanced_mapping.mapper.ClazzMapper;
import com.cell.advanced_mapping.pojo.Clazz;
import com.cell.advanced_mapping.utils.SqlSessionUtil;
import org.junit.Test;

public class ClazzMapperTest {
    @Test
    public void testSelectClazzAndStusByCid() {
        ClazzMapper mapper = SqlSessionUtil.openSession().getMapper(ClazzMapper.class);
        Clazz clazz = mapper.selectClazzAndStusByCid(1001);
        System.out.println(clazz);
    }
}
