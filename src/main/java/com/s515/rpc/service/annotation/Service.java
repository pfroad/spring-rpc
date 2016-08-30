package com.s515.rpc.service.annotation;

import java.lang.annotation.*;

/**
 * Created by CYMAC on 8/29/16.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface Service {
    String value() default "";
}
