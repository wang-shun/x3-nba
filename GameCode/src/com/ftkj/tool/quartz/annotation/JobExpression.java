package com.ftkj.tool.quartz.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * /** 每天0点 
	"0 0 0 * * ?"</br>
	/** 每天3点 
	"0 0 3 * * ?"</br>
	/** 每1秒钟 
	"0/1 * * * * ?"</br>
	/** 每5秒钟 
	"0/5 * * * * ?"</br>
	/** 每10秒钟 
	"0/10 * * * * ?"</br>
	/** 每1分钟 
	"0 0/1 * * * ?"</br>
	/** 每3分钟 
	"0 0/3 * * * ?"</br>
	/** 每5分钟 
	"0 0/5 * * * ?"</br>
	/** 每10分钟 
	"0 0/10 * * * ?"</br>
	/** 每1小时 
	"0 0 0/1 * * ?"</br>
	/** 每3小时 
	"0 0 0/3 * * ?"</br>
	/** 每月最后一天23点59分
	"0 59 23 L * ?"</br>
 * @author tim.huang
 * 2015年12月14日
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JobExpression {

	public static final String SYSTEM = "system";
	public static final String GAME = "game";
	
	public String expression();
	
	public String name();
	
	/**
	 * @see JobExpression
	 * SYSTEM 系统任务调度组
	 * GAME 游戏内任务调度组
	 * @return
	 */
	public String group();
	
	
}
