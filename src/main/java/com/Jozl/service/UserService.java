package com.Jozl.service;

import com.spring.Autowired;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.Scope;

@Component("userService")
@Scope("prototype")
public class UserService implements BeanNameAware {
    @Autowired
    private SingleService singleService;

    private String beanName;

    @Override
    public void setBeanName(String name) {
        beanName = name;
    }

    public void test() {
        System.out.println(singleService);
        System.out.println(beanName);
    }
}
