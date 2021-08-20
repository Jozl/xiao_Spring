package com.spring;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class JozlApplicationContext {
    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    //单例池
    private ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>();

    public JozlApplicationContext(Class configClass) {
        this.configClass = configClass;

        scan(configClass);
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                Object bean = createBean(beanDefinition);
                singletonObjects.put(beanName, bean);
            }
        }
    }

    private Object createBean(BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getDeclaredConstructor().newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void scan(Class configClass) {
        //解析配置类 拿扫描路径
        ComponentScan componentScanAnnotation = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
        String path = componentScanAnnotation.value();

        //扫描
        //TODO 类加载器 Bootstrap Ext App
        ClassLoader classLoader = JozlApplicationContext.class.getClassLoader();
        URL resource = classLoader.getResource(path.replace(".", "\\"));
        File file = new File(URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8));
        if (file.isDirectory()) {
            for (File fileClass : Objects.requireNonNull(file.listFiles())) {
                String className = fileClass.getAbsolutePath();
                className = className.substring(className.indexOf("com"), className.indexOf(".class")).replace("\\", ".");

                try {
                    Class<?> clazz = classLoader.loadClass(className);
                    if (clazz.isAnnotationPresent(Component.class)) {
                        //解析Bean是singleton还是prototype
                        //BeanDefinition
                        Component componentAnnotation = clazz.getDeclaredAnnotation(Component.class);
                        String beanName = componentAnnotation.value();

                        BeanDefinition beanDefinition = new BeanDefinition();
                        beanDefinition.setClazz(clazz);
                        if (clazz.isAnnotationPresent(Scope.class)) {
                            Scope scopeAnnotation = clazz.getDeclaredAnnotation(Scope.class);
                            beanDefinition.setScope(scopeAnnotation.value());
                        } else {
                            beanDefinition.setScope("singleton");
                        }

                        beanDefinitionMap.put(beanName, beanDefinition);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Object getBean(String beanName) {
        if (beanDefinitionMap.containsKey(beanName)) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if (beanDefinition.getScope().equals("singleton")) {
                return singletonObjects.get(beanName);
            } else {
                //创建Bean对象
                return createBean(beanDefinition);
            }
        }
        return null;
    }
}
