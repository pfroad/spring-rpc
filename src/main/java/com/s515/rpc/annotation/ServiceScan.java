package com.s515.rpc.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by Administrator on 8/25/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ServiceScannerRegistrar.class)
public @interface ServiceScan {
    String[] basePackages() default {};

    Class<? extends Annotation> annotationClass() default Annotation.class;
}
