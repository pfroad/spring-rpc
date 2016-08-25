package com.s515.rpc.service;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;

/**
 * Created by Administrator on 8/25/2016.
 */
public class ClassPathServiceScanner extends ClassPathBeanDefinitionScanner {
    public ClassPathServiceScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    public ClassPathServiceScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
        super(registry, useDefaultFilters);
    }

    public ClassPathServiceScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters, Environment environment) {
        super(registry, useDefaultFilters, environment);
    }
}
