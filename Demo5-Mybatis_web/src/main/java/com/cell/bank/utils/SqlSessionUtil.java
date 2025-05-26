package com.cell.bank.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;

public class SqlSessionUtil {
    /*
     * 类加载时初始化 sqlSessionFactory 对象
     */
    private static SqlSessionFactory sqlSessionFactory;
    private static ThreadLocal<SqlSession> local = new ThreadLocal<>();

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
        SqlSession sqlSession = local.get();
        if (sqlSession == null) {
            sqlSession = sqlSessionFactory.openSession();
            // 将 sqlSession 对象绑定到当前线程上
            local.set(sqlSession);
        }
        return sqlSession; // 手动提交
    }

    public static void close(SqlSession session) {
        if (session != null) {
            session.close();
            local.remove(); // 将 sqlSession 对象从当前线程的绑定关系中移除
        }
    }
}
