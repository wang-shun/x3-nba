package com.ftkj.db.conn.ao;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.ftkj.server.socket.LogicMethod;

/**
 * AO层动态代理
 * @author tim.huang
 * 2015年12月4日
 */
public class AOInvocationHandler implements InvocationHandler {

	private BaseAO ao;
	
	public AOInvocationHandler (BaseAO ao){
		this.ao = ao;
	}
	
	/**
	 * @param proxy 指代我们所代理的那个真实对象
	 * @param method 指代的是我们所要调用真实对象的某个方法的Method对象
	 * @param args 指代的是调用真实对象某个方法时接受的参数
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		    AOSynManager.put(new LogicMethod(method, ao, args));

		return null;
	}

}
