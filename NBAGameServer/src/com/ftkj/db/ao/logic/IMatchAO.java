package com.ftkj.db.ao.logic;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ftkj.db.domain.match.MatchBestPO;
import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.db.domain.match.MatchPO;
import com.ftkj.db.domain.match.MatchSignPO;

public interface IMatchAO {
	
	public List<MatchPO> getMatchPOList();
	
	public MatchPO getMatchPO(int seqId, int matchId);
	
	public List<MatchSignPO> getMatchSignPOList(int seqId, int matchId);
	
	/**
	 * 查询某届比赛排名前几
	 * @param seqId
	 * @param matchId
	 * @param size 前几名
	 * @return
	 */
	public List<MatchSignPO> getMatchSignPOListRank(int seqId, int matchId, int size);
	
	public List<MatchPKPO> getMatchPKPOList(int seqId, int matchId);
	
	/**
	 * 批量查询历史最佳
	 * @param matchId
	 * @param teamList
	 * @return
	 */
	public List<MatchBestPO> getMatchBestPOList(int matchId, Collection<Long> teamList);
	
	/**
	 * 查询球队的历史最佳
	 * @param teamId
	 * @return
	 */
	public List<MatchBestPO> getTeamMatchBestList(long teamId);

	public Map<String, String> getMatchSeq();

}
