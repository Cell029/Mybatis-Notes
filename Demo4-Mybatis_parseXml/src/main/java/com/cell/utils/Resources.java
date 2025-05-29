package com.cell.utils;

import java.io.InputStream;

/*
* 完成"类路径"中资源的加载
*/
public class Resources {
    public static InputStream getResourcesAsStream(String config){
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(config);
    }
}
