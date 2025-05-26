package com.cell.testThreadLocal.test1;

import java.util.HashMap;
import java.util.Map;

public class MyThreadLocal<T> {
    private Map<Thread, T> map = new HashMap<Thread, T>();

    // 绑定数据
    public void set(T obj) {
        map.put(Thread.currentThread(), obj);
    }

    // 获取数据
    public T get() {
        return map.get(Thread.currentThread());
    }

    // 移除数据
    public void remove() {
        map.remove(Thread.currentThread());
    }
}
