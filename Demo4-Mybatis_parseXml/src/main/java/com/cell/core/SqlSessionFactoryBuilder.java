package com.cell.core;

import com.cell.utils.Resources;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通过 SqlSessionFactoryBuilder 的 build 方法解析 mybatis-config.xml 文件,然后创建 SqlSessionFactory 对象
 */
public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {

    }

    public SqlSessionFactory build(InputStream inputStream) { // inputStream 指向资源文件的输入流
        SqlSessionFactory sqlSessionFactory = null;
        try {
            // 解析 mybatis-config.xml 文件获取对应标签的信息
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            Element environments = (Element) document.selectSingleNode("/configuration/environments");
            String defaultId = environments.attributeValue("default");
            Element environment = (Element) document.selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");
            Element transactionElt = environment.element("transactionManager");
            Element dataSourceElt = environment.element("dataSource");
            List<String> sqlMapperXMLPathList = new ArrayList<String>();
            List<Node> nodes = document.selectNodes("//mapper"); // 获取整个配置文件的所有 mapper 标签
            nodes.forEach(node -> {
                Element mapper = (Element) node;
                String resource = mapper.attributeValue("resource");
                sqlMapperXMLPathList.add(resource);
            });
            // 获取数据源对象
            DataSource dataSource = getDataSource(dataSourceElt);

            // 获取事务管理器
            Transaction transaction = getTransaction(transactionElt, dataSource);
            // 获取 MappedStatement 对象
            Map<String, MappedStatement> mappedStatementMap = getMappedStatements(sqlMapperXMLPathList);
            // 解析完成后构建 SqlSessionFactory 对象
            sqlSessionFactory = new SqlSessionFactory(transaction, mappedStatementMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sqlSessionFactory;
    }


    private Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperXMLPathList) {
        Map<String, MappedStatement> mappedStatements = new HashMap<>();
        sqlMapperXMLPathList.forEach(sqlMapperXMLPath -> {
            try {
                // String resource = mapperElt.attributeValue("resource");
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(Resources.getResourcesAsStream(sqlMapperXMLPath));
                Element mapper = (Element) document.selectSingleNode("/mapper");
                String namespace = mapper.attributeValue("namespace");

                mapper.elements().forEach(element -> {
                    String id = element.attributeValue("id");
                    String sqlId = namespace + "." + id;
                    // 获取 sql 语句
                    String sql = element.getTextTrim();
                    // String parameterType = sqlMapper.attributeValue("parameterType");
                    String resultType = element.attributeValue("resultType");
                    // String sqlType = element.getName().toLowerCase();
                    // 封装 MappedStatement 对象
                    MappedStatement mappedStatement = new MappedStatement(sql, resultType);
                    mappedStatements.put(sqlId, mappedStatement);
                });
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });
        return mappedStatements;
    }

    /*
    获取事务管理器
     */
    private Transaction getTransaction(Element transactionElt, DataSource dataSource) {
        Transaction transaction = null;
        String type = transactionElt.attributeValue("type").trim().toUpperCase();
        if (Const.JDBC_TRANSACTION.equals(type)) {
            transaction = new JdbcTransaction(dataSource, false);
        }
        return transaction;
    }

    /*
    获取数据源对象
     */
    private DataSource getDataSource(Element dataSourceElt) {
        Map<String, String> dataSourceMap = new HashMap<String, String>();
        // 获取 dataSource 下的所有 property
        List<Element> propertyElts = dataSourceElt.elements("property");
        propertyElts.forEach(propertyElt -> {
            String name = propertyElt.attributeValue("name");
            String value = propertyElt.attributeValue("value");
            dataSourceMap.put(name, value);
        });
        DataSource dataSource = null;
        String type = dataSourceElt.attributeValue("type").trim().toUpperCase();
        if (Const.UN_POOLED_DATASOURCE.equals(type)) {
            dataSource = new UnPooledDataSource(dataSourceMap.get("driver"), dataSourceMap.get("url"), dataSourceMap.get("username"), dataSourceMap.get("password"));
        }
        return dataSource;
    }
}
