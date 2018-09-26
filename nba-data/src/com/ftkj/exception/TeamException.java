package com.ftkj.exception;

public class TeamException extends BizException{

	public TeamException(Errors error, String msg) {
		super(error, msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2415975396030881353L;

	public static final Errors CODE_TEAM_ALREADY_EXISTS	= new Errors(110001,"球队已存在");
	public static final Errors CODE_TEAM_NOT_FOUND		= new Errors(110002,"球队不存在");
	
	public static final Errors CODE_PLAYER_NOT_FOUND		= new Errors(110003,"球员不存在");
	
}
