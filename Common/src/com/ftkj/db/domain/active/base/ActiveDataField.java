package com.ftkj.db.domain.active.base;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface ActiveDataField {

	public String fieldName();
	
	public int size() default 0;
	
}
