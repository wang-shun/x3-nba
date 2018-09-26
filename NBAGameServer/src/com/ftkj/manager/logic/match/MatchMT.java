package com.ftkj.manager.logic.match;

import java.util.Collection;
import java.util.List;

import com.ftkj.enums.EMatchStatus;
import com.ftkj.enums.battle.EBattleStage;
import com.ftkj.manager.battle.BattleHandle;
import com.ftkj.manager.battle.BattleAPI;
import com.ftkj.manager.logic.LeagueManager;
import com.ftkj.manager.logic.TeamManager;
import com.ftkj.manager.match.MatchBest;
import com.ftkj.manager.match.MatchPK;
import com.ftkj.manager.match.MatchSign;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.MatchPB;
import com.ftkj.proto.MatchPB.MatchRankListData;
import com.ftkj.proto.MatchPB.MatchReportData;
import com.ftkj.proto.MatchPB.MatchStartTipData;
import com.ftkj.server.instance.InstanceFactory;
import com.google.common.collect.Lists;

public class MatchMT {

	/**
	 * 历史最佳协议转换
	 * @param teamId
	 * @return
	 */
	public static MatchPB.MatchBestListData getMatchBestListData(long teamId, Collection<MatchBest> bestList) {
		List<MatchPB.MatchBestData> dataList = Lists.newArrayList();
		for(MatchBest best : bestList) {
			dataList.add(MatchPB.MatchBestData.newBuilder()
					.setMatchId(best.getMatchId())
					.setRank(best.getRank())
					.build());
		}
		return MatchPB.MatchBestListData.newBuilder()
				.setTeamId(teamId)
				.addAllBestList(dataList)
				.build();
	}
	
	/**
	 * 比赛列表
	 * @param teamId
	 * @return
	 */
	public static MatchPB.MatchListData getMatchListData(List<KnockoutMatch> list, long teamId,int dailySignupNum) {
		List<MatchPB.MatchData> matchListData = Lists.newArrayList();
		for(KnockoutMatch match : list) {
			matchListData.add(getMatchData(match, teamId,dailySignupNum));
		}
		return MatchPB.MatchListData.newBuilder()
				.addAllMatchList(matchListData)
				.build();
	}
	
	public static MatchPB.MatchData getMatchData(KnockoutMatch match, long teamId,int dailySignupNum) {
		return MatchPB.MatchData.newBuilder()
			.setSeq(match.getSeqId())
			.setId(match.getMatchId())
			.setStatus(match.getStatus())
			.setIsSign(match.isSign(teamId))
			.setSignNum(match.getSignNum())
			.setMatchTime(match.getMatchTime().getMillis())
			.setDailySignupNum(dailySignupNum)
			.build();
	}
	
	/**
	 * 比赛界面推包数据
	 * @param match
	 * @return
	 */
	public static MatchPB.MatchTopicData getMatchTopicData(KnockoutMatch match,int dailySignupNum) {
		return MatchPB.MatchTopicData.newBuilder()
			.setSeq(match.getSeqId())
			.setId(match.getMatchId())
			.setStatus(match.getStatus())
			.setSignNum(match.getSignNum())
			.setDailySignupNum(dailySignupNum)
			.build();
	}
	
	/**
	 * 比赛详细对战列表界面
	 * @param match
	 * @return
	 */
	public static MatchPB.MatchDetailData getMatchDetailData(KnockoutMatch match, LeagueManager leagueManager) {
		return MatchPB.MatchDetailData.newBuilder()
				.setRound(match.getRound())
				.addAllPkList(getMatchPKListData(match, leagueManager))
				.build();
		
	}
	
	public static List<MatchPB.MatchPKData> getMatchPKListData(KnockoutMatch match, LeagueManager leagueManager) {
		List<MatchPB.MatchPKData> pkListData = Lists.newArrayList();
		if(match.getStatus() != EMatchStatus.比赛中.status) {
			return pkListData;
		}
		List<MatchPK> pkList = match.getRoundPKList();
		if(pkList != null && pkList.size() > 0) {
			for(MatchPK pkInfo : pkList) {
				TeamManager teamManager = InstanceFactory.get().getInstance(TeamManager.class);
				Team homeTeam = teamManager.getTeam(pkInfo.getHomeId());
				Team awayTeam = teamManager.getTeam(pkInfo.getAwayId());
				if(homeTeam == null || awayTeam == null) {
					// 容错处理
					return pkListData;
				}
				int homeScore = pkInfo.getHomeScore();
				int awayScore = pkInfo.getAwayScore();
				// 比赛没有结束，比分从比赛数据中取
				BattleHandle battle = BattleAPI.getInstance().getBattleHandle(pkInfo.getBattleId());
				if(match.getStatus() != 1) {
					if(battle != null) {
						homeScore = battle.getBattleSource().getHome().getScore();
						awayScore = battle.getBattleSource().getAway().getScore();
					} 
				}
//				Before(1),//赛前
//				TipTeam(2),//玩家准备完毕，通知玩家进入比赛阶段
//				PK(3),//比赛阶段
//				End(4),//结束
//				Close(5)//关闭
				EBattleStage battleStatus = pkInfo.getStatus()==0?EBattleStage.Before:EBattleStage.End;
				if(battle != null) {
					battleStatus = battle.getBattleSource().getStage();
				}
				//
				pkListData.add(MatchPB.MatchPKData.newBuilder()
						.setBattleId(pkInfo.getBattleId())
						.setHomeId(homeTeam.getTeamId())
						.setAwatId(awayTeam.getTeamId())
						.setHomeName(homeTeam.getName())
						.setAwayName(awayTeam.getName())
						.setHomeLogo(homeTeam.getLogo())
						.setAwayLogo(awayTeam.getLogo())
						.setHomeLevel(homeTeam.getLevel())
						.setAwayLevel(awayTeam.getLevel())
						.setHomeLeague(leagueManager.getLeagueName(homeTeam.getTeamId()))
						.setAwayLeague(leagueManager.getLeagueName(awayTeam.getTeamId()))
						.setStatus(battleStatus.type)
						.setHomeScore(homeScore)
						.setAwayScore(awayScore)
						.build());
			}
		}
		return pkListData;
	}
	
	/**
	 * 上届排名
	 * @param list
	 * @return
	 */
	public static MatchRankListData getMatchLastRank(List<MatchSign> list, TeamManager teamManager) {
		List<MatchPB.MatchRankData> dataList = Lists.newArrayList();
		for(MatchSign sign : list) {
			dataList.add(MatchPB.MatchRankData.newBuilder()
					.setTeamName(teamManager.getTeamNameById(sign.getTeamId()))
					.setRank(sign.getRank())
					.build());
		}
		return MatchPB.MatchRankListData.newBuilder().addAllRankList(dataList).build();
	}

	/**
	 * 比赛结算数据
	 * @param matchSign
	 * @return 
	 * @return
	 */
	public static MatchReportData getMatchReportData(MatchSign matchSign) {
		// 被挤出去的排名做+1处理，前台显示要求
		int rank = matchSign.getStatus() == 0 ? matchSign.getRank() + 1 : matchSign.getRank();
		return MatchPB.MatchReportData.newBuilder()
				.setId(matchSign.getMatchId())
				.setSeq(matchSign.getSeqId())
				.setRank(rank)
				.build();
	}
	
	/**
	 * 赛前通知
	 * @param matchSign
	 * @param min
	 * @return
	 */
	public static MatchStartTipData getMatchStartTipData(KnockoutMatch match, int min) {
		return MatchPB.MatchStartTipData.newBuilder()
				.setId(match.getMatchId())
				.setSeq(match.getSeqId())
				.setMin(min)
				.build();
	}
}
