package com.ftkj.manager.active.base;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ftkj.db.domain.active.base.ActiveBase;

@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ActiveAnno {

	public enum ERedType{活动,福利,默认};
	public EAtv atv();
	public ERedType redType();
	
	/**
	 * 球队数据类
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class clazz() default ActiveBase.class;
	
	/**
	 * 公共数据类
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class shareClass() default ActiveBase.class;
	
	/**
	 * 球队每天（redis）数据类
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class redisClass() default Object.class;
}
