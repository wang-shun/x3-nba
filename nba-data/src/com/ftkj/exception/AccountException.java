package com.ftkj.exception;

public class AccountException extends BizException{

	private static final long serialVersionUID = -8223290957875415006L;
	
	public AccountException(Errors error, String msg) {
		super(error, msg);		
	}	
	public static final Errors CODE_ACCOUNT_ALREADY_EXISTS	= new Errors(100001,"帐号已存在");	
	public static final Errors CODE_ACCOUNT_NOT_FOUND		= new Errors(100002,"帐号不存在");
	public static final Errors CODE_WRONG_PASSWORD			= new Errors(100003,"密码错误");	
	public static final Errors CODE_ACCOUNT_NEVER_ACTIVATE	= new Errors(100004,"帐号没有激活");

}

