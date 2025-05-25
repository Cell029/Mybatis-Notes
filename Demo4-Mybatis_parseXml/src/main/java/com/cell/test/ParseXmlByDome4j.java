package com.cell.test;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.HashMap;
import java.util.Map;

public class ParseXmlByDome4j {
    public static void main(String[] args) throws Exception {
        // 创建 SAXReader 对象
        SAXReader saxReader = new SAXReader();
        // 读 xml 文件,返回 document 对象,document 对象是文档对象,代表了整个 xml 文件
        Document document = saxReader.read(Thread.currentThread().getContextClassLoader().getResourceAsStream("mybatis-config.xml"));

        // 获取 <environments> 标签的 default 属性的值
        // 从根目录下开始找 configuration 标签,然后找 configuration 的子标签 environments
        Element environmentsElt = (Element)document.selectSingleNode("/configuration/environments");
        // 获取属性的值
        String defaultId = environmentsElt.attributeValue("default");
        System.out.println("默认环境的 id:" + defaultId);

        // 根据 default 的值获取对应的 environment 标签
        Element environmentElt = (Element)document.selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");

        // 获取 environment 标签下的 transaction 标签(Element 的 element() 用来获取孩子节点)
        Element transactionManager = environmentElt.element("transactionManager");
        // 获取 transaction 标签的值,也就是事务管理器的类型
        String transactionManagerType = transactionManager.attributeValue("type");
        System.out.println("事务管理器的类型:" + transactionManagerType);

        // 获取 datasource 的节点
        Element dataSource = environmentElt.element("dataSource");
        // 获取 datasource 的值,也就是使用了哪种连接池
        String dataSourceType = dataSource.attributeValue("type");
        System.out.println("数据源类型:" + dataSourceType);

        // 将数据源信息封装到Map集合
        // 获取 datasource 下的所有子节点(property)
        Map<String,String> dataSourceMap = new HashMap<>();
        dataSource.elements().forEach(propertyElt -> {
            dataSourceMap.put(propertyElt.attributeValue("name"), propertyElt.attributeValue("value"));
        });

        System.out.println("property 信息:");
        dataSourceMap.forEach((k, v) -> System.out.println(k + ":" + v));

        // 获取 sqlmapper.xml 文件的路径
        Element mappersElt = (Element) document.selectSingleNode("//mappers");
        mappersElt.elements().forEach(mapper -> {
            System.out.println("sqlMapper.xml 路径:" + mapper.attributeValue("resource"));
        });
    }
}
