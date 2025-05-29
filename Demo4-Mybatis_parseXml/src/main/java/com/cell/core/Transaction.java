package com.cell.core;

import java.sql.Connection;

public interface Transaction {
    void commit();

    void rollback();

    void close();

    // 开启数据库连接
    void openConnection();

    // 获取数据库连接对象
    Connection getConnection();
}
