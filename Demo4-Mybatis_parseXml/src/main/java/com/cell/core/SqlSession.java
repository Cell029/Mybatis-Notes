package com.cell.core;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SqlSession {
    private SqlSessionFactory sqlSessionFactory;

    public SqlSession(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    // insert
    public int insert(String sqlId, Object pojo) {
        int count = 0;
        try {
            Connection connection = sqlSessionFactory.getTransaction().getConnection();
            String mybatisSql = sqlSessionFactory.getMappedStatements().get(sqlId).getSql();
            // 把占位符变成 "?"
            String sql = mybatisSql.replaceAll("#\\{[a-zA-Z0-9_\\$]*}", "?");

            Map<Integer, String> map = new HashMap<>();
            int index = 1;
            while (mybatisSql.contains("#")) {
                int beginIndex = mybatisSql.indexOf("#") + 2;
                int endIndex = mybatisSql.indexOf("}");
                map.put(index++, mybatisSql.substring(beginIndex, endIndex).trim());
                mybatisSql = mybatisSql.substring(endIndex + 1);
            }

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 给 ? 传值
            map.forEach((k, v) -> {
                // 获取java实体类的get方法名
                String getMethodName = "get" + v.toUpperCase().charAt(0) + v.substring(1);
                Method getMethod = null;
                try {
                    getMethod = pojo.getClass().getDeclaredMethod(getMethodName);
                    preparedStatement.setString(k, getMethod.invoke(pojo).toString());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            // 执行 sql
            count = preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // selectOne
    public Object selectOne(String sqlId, Object param) {
        Object obj = null;
        try {
            Connection connection = sqlSessionFactory.getTransaction().getConnection();
            MappedStatement mappedStatement = sqlSessionFactory.getMappedStatements().get(sqlId);
            String mybatisSql = mappedStatement.getSql();
            // select * from t_user where id = #{id}
            String sql = mybatisSql.replaceAll("#\\{[a-zA-Z0-9_\\$]*}", "?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 给占位符传值
            preparedStatement.setString(1, param.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            // 从结果集中取出数据,封装成对象
            String resultType = mappedStatement.getResultType();
            if (resultSet.next()) {
                Class<?> resultTypeClass = Class.forName(resultType);
                obj = resultTypeClass.newInstance();
                // 给 User 类的 id,name,age 赋值
                ResultSetMetaData rsmd = resultSet.getMetaData();
                int columnCount = rsmd.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String propertyName = rsmd.getColumnName(i);
                    // 拼接方法名
                    String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                    // 获取 set 方法
                    Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName, String.class);
                    // 调用 set 方法给 obj 对象赋值
                    setMethod.invoke(obj, resultSet.getString(propertyName));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return obj;
    }

    // 提交事务,调用事务管理器的方法
    public void commit() {
        sqlSessionFactory.getTransaction().commit();
    }

    // 回滚事务
    public void rollback() {
        sqlSessionFactory.getTransaction().rollback();
    }

    // 关闭事务
    public void close() {
        sqlSessionFactory.getTransaction().close();
    }
}
