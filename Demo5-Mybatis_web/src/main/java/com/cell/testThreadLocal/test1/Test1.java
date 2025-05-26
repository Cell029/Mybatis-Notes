package com.cell.testThreadLocal.test1;

public class Test1 {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        System.out.println(thread + "-main");

        // 调用 Service
        UserService userService = new UserService();
        userService.save();
    }
}
