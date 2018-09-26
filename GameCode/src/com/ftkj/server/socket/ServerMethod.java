package com.ftkj.server.socket;

import java.lang.reflect.Method;

/**
 * 服务器反射用方法
 * @author tim.huang
 * 2015年11月27日
 */
public class ServerMethod {
	private int keyCode;
	private Method method;
	private Object instanceObject;
	private Class<?>[] attributeType;
	private String name;
	private int moduleCode;
	
	public static ServerMethod REJECT_METHOD = new ServerMethod(0);
	
			
	private ServerMethod(int keyCode){
		this.keyCode = keyCode;
	}
	public ServerMethod(int keyCode, Method method, Object instanceObject,String name) {
		super();
		this.keyCode = keyCode;
		this.method = method;
		this.instanceObject = instanceObject;
		this.name = name;
		attributeType = method.getParameterTypes();
	}
	public ServerMethod(int keyCode, Method method, Object instanceObject,String name,int moduleCode) {
		super();
		this.keyCode = keyCode;
		this.method = method;
		this.instanceObject = instanceObject;
		this.name = name;
		this.moduleCode = moduleCode;
		attributeType = method.getParameterTypes();
	}
	public int getModuleCode() {
		return moduleCode;
	}
	public void invoke(Object[] args)throws Throwable{
		this.method.invoke(this.instanceObject, args);
	}
	
	public Class<?>[] getAttributeType() {
		return attributeType;
	}
	
	public String getName() {
		return name;
	}
	public int getKeyCode() {
		return keyCode;
	}
	public Method getMethod() {
		return method;
	}
	public Object getInstanceObject() {
		return instanceObject;
	}	
}
