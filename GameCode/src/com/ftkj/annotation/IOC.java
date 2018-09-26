package com.ftkj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tim.huang
 * 2016年5月23日
 * 在Manager，AO，DAO中成员变量使用该注解。实现依赖注入
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IOC {

}
