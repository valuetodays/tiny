package com.billy.jee.tinymybatis;

import java.lang.annotation.*;

/**
 * by vt.zd
 * on 2017-03-20 15:12
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Param {
    String value() default "";
}
