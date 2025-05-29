package com.cell.core;

/**
 * sqlMapper.xml 中的 sql 语句映射实体类,将 sql 标签中的所有信息封装到这个对象中
 */
public class MappedStatement {
    // private String sqlId;


    // 封装的结果集类型,有些语句不需要返回结果集类型,所以存在为 null 的情况
    private String resultType;

    // sql 语句
    private String sql;

    // private String parameterType;

    // private String sqlType;

    @Override
    public String toString() {
        return "MappedStatement{" +
                "resultType='" + resultType + '\'' +
                ", sql='" + sql + '\'' +
                '}';
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    public MappedStatement(String sql, String resultType) {
        this.sql = sql;
        this.resultType = resultType;
    }
}
