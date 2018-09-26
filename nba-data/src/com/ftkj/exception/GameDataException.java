package com.ftkj.exception;


public class GameDataException extends BizException{
	
	public GameDataException(Errors error, String msg) {
		super(error, msg);		
	}
	public GameDataException(Errors error,Throwable e){
		super(error,e);		
	}	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3876458986704242691L;
	
	public static final Errors CODE_GET_DATA_ERROR = new Errors(400000,"获取数据失败");
	public static final Errors CODE_CAN_NOT_FOUND_PLAYER = new Errors(400001,"球员不存在");
	public static final Errors CODE_CAN_NOT_FOUND_TEAM = new Errors(400002,"找不到球队");
	public static final Errors CODE_CAN_NOT_FOUND_SEASON = new Errors(400003,"赛季不存在");
	
	
}
