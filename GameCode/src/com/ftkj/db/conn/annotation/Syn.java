package com.ftkj.db.conn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tim.huang
 * 2016年12月28日
 * 异步操作注解，一般配置实现了SynAO接口的AO使用,目前已废弃使用
 * 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface Syn {

}
