package com.iscas.lndicatormonitor.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解，用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {
    /**
     * 操作名称
     */
    String value() default "";
    
    /**
     * 操作类型
     */
    String type() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default false;
    
    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
}