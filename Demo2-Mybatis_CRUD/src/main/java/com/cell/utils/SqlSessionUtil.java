package com.cell.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

/**
 * 功能：
 * 作者：Cell
 * 日期：2025/5/24 20:16
 */
public class SqlSessionUtil {
    /*
     * 类加载时初始化 sqlSessionFactory 对象
     */
    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsStream("mybatis-config.xml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private SqlSessionUtil(){}

    /*
     * 每调用一次 openSession() 可获取一个新的会话
     */
    public static SqlSession openSession() {
        return sqlSessionFactory.openSession(); // 手动提交
    }

    public static void closeSession(SqlSession session) {
        if (session != null) {
            session.close();
        }
    }
}
