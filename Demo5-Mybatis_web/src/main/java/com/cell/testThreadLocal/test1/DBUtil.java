package com.cell.testThreadLocal.test1;

public class DBUtil {
    private static MyThreadLocal<Connection> myThreadLocal = new MyThreadLocal();

    public static Connection getConnection() {
        Connection conn = myThreadLocal.get();
        // 第一次调用肯定获取到的 Connection 对象是空的
        if (conn == null) {
            conn = new Connection();
            // 将创建的 Connection 对象放进 Map 集合中
            myThreadLocal.set(conn);
        }
        return conn;
    }
}
