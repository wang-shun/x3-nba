package com.ftkj.db.ao.logic;

import java.util.List;

import com.ftkj.manager.starlet.StarletDualMeet;
import com.ftkj.manager.starlet.StarletPlayer;
import com.ftkj.manager.starlet.StarletRank;
import com.ftkj.manager.starlet.StarletTeamRedix;

public interface IStarletAO {

    public List<StarletTeamRedix> getStarletTeamRedix(long tid);

    public List<StarletDualMeet> getStarletDualMeetByTeamId(long tid);

    public List<StarletRank> getAllStarletRank();

    public List<StarletPlayer> getStarletPlayer(long tid);

    public void deleteStarletPlayer(long tid, Integer prid);
    
    public void deleteStarletDualMeet(long tid, Integer prid);
}
