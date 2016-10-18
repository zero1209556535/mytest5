package com.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by lenovo on 2016/10/18.
 */
@Service
public class SpringAnnotationMain {
    @Resource
    SpringAnnotation springAnnotation;

   public void spring(){
        springAnnotation.springAN();
    }

    public static void main(String[] args) {
        ApplicationContext context=new ClassPathXmlApplicationContext("applicationContext.xml");
        SpringAnnotationMain main= (SpringAnnotationMain) context.getBean("springAnnotation");
        main.spring();

    }
}
