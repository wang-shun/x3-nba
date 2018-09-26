package com.ftkj.script;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * JsonUtil导出是的限制条件
 */
@Target(ElementType.FIELD)  
@Retention(RetentionPolicy.RUNTIME)  
@Documented
@Inherited  
public @interface JSONField{
	int min() default Integer.MIN_VALUE;
	int max() default Integer.MAX_VALUE;
	int size() default 0;
	boolean notNull() default false;
}
