package com.cell.core;

import java.util.Map;

/**
 * 通过 SqlSessionFactory 对象获取 SqlSession 对象(开启会话,可获取多个)
 */
public class SqlSessionFactory {
    /*
    事务管理器
     */
    private Transaction transaction;

    /*
    存放 sql 语句的 Map 集合(key 为 sqlId,value 为 MappedStatement 对象)
     */
    private Map<String, MappedStatement> mappedStatements;

    public SqlSessionFactory(Transaction transaction, Map<String, MappedStatement> mappedStatements) {
        this.transaction = transaction;
        this.mappedStatements = mappedStatements;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transactionManager) {
        this.transaction = transactionManager;
    }

    public Map<String, MappedStatement> getMappedStatements() {
        return mappedStatements;
    }

    public void setMappedStatements(Map<String, MappedStatement> mappedStatements) {
        this.mappedStatements = mappedStatements;
    }

    // 获取 SqlSession 对象
    public SqlSession openSession() {
        // 开启会话的前提是开启连接
        transaction.openConnection();
        // 创建 SqlSession 对象
        SqlSession sqlSession = new SqlSession(this);
        return sqlSession;
    }
}
