package com.cell.test;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class ParseSqlMapperXML {
    public static void main(String[] args) throws DocumentException {
        // 读取xml，获取document对象
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("sqlmapper.xml"));

        // 获取namespace
        Element mapperElt = (Element) document.selectSingleNode("/mapper");
        String namespace = mapperElt.attributeValue("namespace");
        System.out.println(namespace);

        // 获取 mapper 下的所有子节点
        mapperElt.elements().forEach(statementElt -> {
            // 标签名
            String name = statementElt.getName();
            System.out.println("name:" + name);
            // 如果是 select 标签，就要获取它的 resultType
            if ("select".equals(name)) {
                String resultType = statementElt.attributeValue("resultType");
                System.out.println("resultType:" + resultType);
            }
            // 获取 sql id
            String id = statementElt.attributeValue("id");
            System.out.println("sqlId:" + id);
            // sql 语句
            String sql = statementElt.getTextTrim();
            System.out.println("sql:" + sql);
        });
    }
}
