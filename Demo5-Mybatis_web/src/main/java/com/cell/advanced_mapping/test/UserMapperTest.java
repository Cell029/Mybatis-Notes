package com.cell.advanced_mapping.test;

import com.cell.advanced_mapping.mapper.UserMapper;
import com.cell.advanced_mapping.pojo.User;
import com.cell.advanced_mapping.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class UserMapperTest {
    @Test
    public void testSelectByUserId() {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        User user = mapper.selectUserById(1);
        System.out.println(user);
    }
}
