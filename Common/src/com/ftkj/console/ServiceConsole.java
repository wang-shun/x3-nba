package com.ftkj.console;

import com.ftkj.enums.EChat;

/**
 * @author tim.huang
 * 2017年3月9日
 * 监听订阅控制台
 */
public class ServiceConsole {
	
	
	private static final String Battle = "Battle_";
	private static final String DraftMain = "DraftMain";
	private static final String DraftStage = "DraftStage_";
	private static final String DraftRoom = "DraftRoom_";
	public static final String MatchList = "MatchList_ALL";
	private static final String Match = "Match_";
	private static final String MatchDetail = "MatchList_Detail_";
	private static final String Chat = "Chat_";
	private static final String LeagueArena = "LeagueArena_";
	
	private static final String CustomRoom = "CustomRoom_";
	
	public static final String getLeagueAreaAllKey() {
		return LeagueArena + "ALL";
	}
	public static final String getLeagueAreaOneKey(int leagueId) {
		return LeagueArena + leagueId;
	}
	
	public static final String getChatKey(EChat type){
		return Chat+type.getType()+"_";
	}
	
	public static final String getCustomRoomKey(int roomId){
		return CustomRoom + roomId;
	}
	
	public static final String getBattleKey(long battleId){
		return Battle+battleId;
	}
	
	public static final String getDraftMainKey(){
		return DraftMain;
	}
	
	public static final String getDraftStageKey(int roomId){
		return DraftStage+roomId;
	}
	
	public static final String getDraftRoomKey(int roomId){
		return DraftRoom+roomId;
	}
	
	public static String getMatchDetailKey(int matchId, int seqId) {
		return MatchDetail + matchId + "_" + seqId;
	}
	
	public static String getMatchKey(int matchId, int seqId) {
		return Match + matchId + "_" + seqId;
	}
	
}
