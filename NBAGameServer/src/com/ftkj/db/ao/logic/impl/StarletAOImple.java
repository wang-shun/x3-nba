package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.IStarletAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.StarletDAO;
import com.ftkj.manager.starlet.StarletDualMeet;
import com.ftkj.manager.starlet.StarletPlayer;
import com.ftkj.manager.starlet.StarletRank;
import com.ftkj.manager.starlet.StarletTeamRedix;

public class StarletAOImple extends BaseAO implements IStarletAO {

	@IOC
	private StarletDAO dao;

    @Override
    public List<StarletTeamRedix> getStarletTeamRedix(long tid) {
        return dao.getStarletTeamRedixList(tid);
    }

    @Override
    public List<StarletDualMeet> getStarletDualMeetByTeamId(long tid) {
        return dao.getStarletDualMeetList(tid);
    }

    @Override
    public List<StarletRank> getAllStarletRank() {
        return dao.getStarletRankList();
    }

    @Override
    public List<StarletPlayer> getStarletPlayer(long tid) {
        return dao.getStarletPlayerList(tid);
    }

    @Override
    public void deleteStarletPlayer(long tid, Integer prid) {
        dao.deleteStarletPlayer(tid, prid);
    }

    @Override
    public void deleteStarletDualMeet(long tid, Integer prid) {
        dao.deleteStarletDualMeet(tid, prid);
    }
}
