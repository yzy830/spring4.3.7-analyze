package com.gerald.test;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;

public class TestBean implements FactoryBean<Test> {
    @Autowired
    private ApplicationContext context;
    
    private String name;
    
    public void show() {
        System.out.println("do someting");
    }
    
    @Required
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Test getObject() throws Exception {
        return new Test();
    }

    @Override
    public Class<?> getObjectType() {
        return Test.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
