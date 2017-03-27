package com.gerald.test;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class App {
    public static void main(String[] args) {
        try(ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(App.class)) {
            TestBean t = context.getBean(TestBean.class);
            t.show();
        }
    }
}
