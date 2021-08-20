package com.Jozl;

import com.spring.JozlApplicationContext;

public class Test {
    public static void main(String[] args) {
        JozlApplicationContext applicationContext = new JozlApplicationContext(AppConfig.class);

        Object userService1 = applicationContext.getBean("userService");
        Object userService2 = applicationContext.getBean("userService");
        System.out.println(userService1);
        System.out.println(userService2);
        Object singleService1 = applicationContext.getBean("singleService");
        Object singleService2 = applicationContext.getBean("singleService");
        System.out.println(singleService1);
        System.out.println(singleService2);
    }
}
