package com.ftkj.console;

import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.ftkj.manager.system.NBABattleGuessBean;

public class GameConsole {
	public static final int Game_Load_Code_Success = 0;
	public static final int Game_Load_Code_Create = 1;
	public static final int Game_Load_Code_Error = 2;

	public static final DateTime Max_Date = DateTime.parse("2030-01-01 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
	public static final DateTime Min_Date = DateTime.parse("1971-01-01 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
	public static final DateTime Min_Timestamp = DateTime.parse("1990-01-01 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
	
	/**
	 * 游戏APP的ID
	 */
	public static final int Game_App_Id = 309;
//	public static final String Game_SDK_Server_URL = "http://portal.ftxgame.com:8080/sdk/verifyTokenPlf/";
	public static final String Game_SDK_Server_URL = "http://portal.ftxgame.com/sdkServer/verifyToken";
	public static final String Game_Web_Create_Rechage_URL = "http://web.bjjh.aboilgame.com:8080/xgame/recharge/create?";

	public static final int Team_Name_Min_Len = 1;
	/** 球队名. 数据库列最大长度 */
	public static final int Team_Name_Max_Len = 16;

	private static Map<Integer,NBABattleGuessBean> nbaBattleGuessMap;
	
	public static void init(){
		// FIXME 竞猜功能调整。
		nbaBattleGuessMap =  CM.guessBeanList.stream().collect(Collectors.toMap(NBABattleGuessBean::getGameId, val->val));
	}
	
	public static NBABattleGuessBean getGuessBean(int gameId){
		return nbaBattleGuessMap.get(gameId);
	}
	
	
	
	
	
	
	
	
}
