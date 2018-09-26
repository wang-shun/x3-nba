package com.ftkj.manager.match;

import com.ftkj.db.domain.match.MatchSignPO;
import org.joda.time.DateTime;

import java.io.Serializable;

/**
 * 多人赛报名
 *
 * @author Jay
 * @Description:
 * @time:2017年5月19日 上午10:17:36
 */
public class MatchSign implements Serializable {
    private static final long serialVersionUID = 1L;

    private MatchSignPO matchSignPO;

    /**
     * 是否已淘汰出局（当前用在超快赛）
     */
    private boolean isOut;
    
    public MatchSign(MatchSignPO matchSignPO) {
        super();
        this.matchSignPO = matchSignPO;
    }

    public int getSeqId() {
        return this.matchSignPO.getSeqId();
    }

    public int getMatchId() {
        return this.matchSignPO.getMatchId();
    }

    public int getTeamCap() {
        return this.matchSignPO.getTeamCap();
    }

    public long getTeamId() {
        return this.matchSignPO.getTeamId();
    }

    public int getStatus() {
        return this.matchSignPO.getStatus();
    }

    public void setStatus(int status) {
        this.matchSignPO.setStatus(status);
    }

    public DateTime getCreateTime() {
        return this.matchSignPO.getCreateTime();
    }

    public int getRank() {
        return this.matchSignPO.getRank();
    }

    public void setRank(int rank) {
        this.matchSignPO.setRank(rank);
    }

    public void setTeamCap(int teamCap) {
        this.matchSignPO.setTeamCap(teamCap);
    }

    public void save() {
        this.matchSignPO.save();

    }

    public void del() {
        this.matchSignPO.del();
    }

    
    public boolean isOut() {
      return isOut;
    }

    public void setOut(boolean isOut) {
      this.isOut = isOut;
    }

    @Override
    public String toString() {
        return matchSignPO.toString();
    }

}
