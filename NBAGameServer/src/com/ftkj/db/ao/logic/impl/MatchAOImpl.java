package com.ftkj.db.ao.logic.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IMatchAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.MatchDAO;
import com.ftkj.db.domain.match.MatchBestPO;
import com.ftkj.db.domain.match.MatchPKPO;
import com.ftkj.db.domain.match.MatchPO;
import com.ftkj.db.domain.match.MatchSignPO;

public class MatchAOImpl extends BaseAO implements IMatchAO {

	@IOC
	private MatchDAO dao;

	@Override
	public List<MatchPO> getMatchPOList() {
		return dao.getMatchPOList();
	}
	
	@Override
	public MatchPO getMatchPO(int seqId, int matchId) {
		return dao.getMatchPO(seqId, matchId);
	}
	
	@Override
	public List<MatchSignPO> getMatchSignPOList(int seqId, int matchId) {
		return dao.getMatchSignPOList(seqId, matchId);
	}
	
	@Override
	public List<MatchSignPO> getMatchSignPOListRank(int seqId, int matchId, int size) {
		return dao.getMatchSignPOListRank(seqId, matchId, size);
	}

	@Override
	public List<MatchPKPO> getMatchPKPOList(int seqId, int matchId) {
		return dao.getMatchPKPOList(seqId, matchId);
	}

	@Override
	public List<MatchBestPO> getMatchBestPOList(int matchId, Collection<Long> teamList) {
		return dao.getMatchBestPOList(matchId, teamList);
	}

	@Override
	public List<MatchBestPO> getTeamMatchBestList(long teamId) {
		return dao.getTeamMatchBestList(teamId);
	}

	@Override
	public Map<String, String> getMatchSeq() {
		return dao.getMatchSeq();
	}
}
