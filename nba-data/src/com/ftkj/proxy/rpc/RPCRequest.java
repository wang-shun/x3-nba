package com.ftkj.proxy.rpc;

import java.io.Serializable;
import java.util.Arrays;

public class RPCRequest implements Serializable {

	private static final long serialVersionUID = 9082848780680086400L;
	private long startTime;
	private long id;
	private String targetClass;
	private String methodName;
	private String[]parameterTypes;
	private Object[]parameters;
	private boolean isAsync;
	
	public RPCRequest() {
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}	
	public boolean isAsync() {
		return isAsync;
	}
	public void setAsync(boolean isAsync) {
		this.isAsync = isAsync;
	}
	public String getTargetClass() {
		return targetClass;
	}
	public void setTargetClass(String clazz) {
		this.targetClass = clazz;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String[] getParameterTypes() {
		return parameterTypes;
	}
	public void setParameterTypes(String[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}
	public Object[] getParameters() {
		return parameters;
	}
	public void setParameters(Object[] parameters) {
		this.parameters = parameters;
	}	
	
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	@Override
	public String toString() {
		return "RPCRequest [id=" + id + ", clazz=" + targetClass + ", methodName=" + methodName 
		//+ ", parameterTypes="+ Arrays.toString(parameterTypes) 
		+ ", parameters="+ Arrays.toString(parameters) + "]-"+(System.currentTimeMillis()-startTime);
	}
	
}