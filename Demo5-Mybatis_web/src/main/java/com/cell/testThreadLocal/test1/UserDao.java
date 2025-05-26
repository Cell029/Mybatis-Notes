package com.cell.testThreadLocal.test1;

public class UserDao {
    public void insert() {
        Thread thread = Thread.currentThread();
        System.out.println(thread + "-UserDao");

        Connection connection = DBUtil.getConnection();
        System.out.println(connection);

        System.out.println("User Dao insert");
    }
}
