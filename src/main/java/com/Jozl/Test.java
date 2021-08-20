package com.Jozl;

import com.Jozl.service.UserService;
import com.spring.JozlApplicationContext;

public class Test {
    public static void main(String[] args) {
        JozlApplicationContext applicationContext = new JozlApplicationContext(AppConfig.class);

        UserService userService1 = (UserService) applicationContext.getBean("userService");
        userService1.test();
    }
}
