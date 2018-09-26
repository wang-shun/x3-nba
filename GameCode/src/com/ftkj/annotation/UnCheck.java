package com.ftkj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tim.huang
 * 2016年2月23日
 * 在Manager层使用，与客户端的接口不检查登录状态
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnCheck {

}
