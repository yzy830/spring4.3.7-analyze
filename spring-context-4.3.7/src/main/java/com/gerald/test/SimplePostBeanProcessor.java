package com.gerald.test;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class SimplePostBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName)
            throws BeansException {
        System.out.println("before, " + beanName);
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        for(StackTraceElement e : elems) {
            System.out.println(e.toString());
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        System.out.println("after, " + beanName);
        return bean;
    }
    
}
