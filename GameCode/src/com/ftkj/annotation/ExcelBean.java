package com.ftkj.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelBean {

	public String name();

	@SuppressWarnings("rawtypes")
	public Class clazz();
	
	public int order() default 0;
	
	public String sheet() default "";
	
	public String key() default "";
	
	public String value() default "";
	
	

}
