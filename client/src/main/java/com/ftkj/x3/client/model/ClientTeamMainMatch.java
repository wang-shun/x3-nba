package com.ftkj.x3.client.model;

import com.ftkj.manager.match.MainMatch;
import com.ftkj.manager.match.MainMatchDivision;
import com.ftkj.manager.match.MainMatchLevel;
import com.ftkj.manager.match.TeamMainMatch;

import java.util.List;
import java.util.Map;

public class ClientTeamMainMatch extends TeamMainMatch {
    private static final long serialVersionUID = -3929230220887048082L;
    /** 已经购买挑战次数 */
    private int buyMatchNum;

    public ClientTeamMainMatch(long teamId, MainMatch mainMatch, Map<Integer, MainMatchDivision> divs, Map<Integer, MainMatchLevel> levels) {
        super(teamId, mainMatch, divs, levels);
    }

    public ClientTeamMainMatch(long teamId, MainMatch mainMatch, List<MainMatchDivision> levs, List<MainMatchLevel> levels) {
        super(teamId, mainMatch, levs, levels);
    }

    public int getBuyMatchNum() {
        return buyMatchNum;
    }

    public void setBuyMatchNum(int buyMatchNum) {
        this.buyMatchNum = buyMatchNum;
    }
}
