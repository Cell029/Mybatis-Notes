package com.cell.test;

import com.cell.utils.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

public class DeleteDemo {
    public static void main(String[] args) {
        // 获取SqlSession对象
        SqlSession sqlSession = SqlSessionUtil.openSession();
        // 执行SQL语句
        int count = sqlSession.delete("deleteByCarNum", "102");
        sqlSession.commit();
        sqlSession.close();
        System.out.println("删除了几条记录：" + count);
    }
}
