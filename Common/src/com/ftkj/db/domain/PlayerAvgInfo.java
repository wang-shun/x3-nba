package com.ftkj.db.domain;

import java.util.List;
import java.util.Map;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EActionType;
import com.ftkj.util.StringUtil;
import com.ftkj.util.lambda.LBFloat;
import com.google.common.collect.Lists;

public class PlayerAvgInfo extends AsynchronousBatchDB {
    private static final long serialVersionUID = 1L;
    private long teamId;
    /** 球员id*/
    private int playerId;
    /** 得分*/
    private int pts;
    /** 得分*/
    private int reb;
    /** 助攻次数*/
    private int ast;
    /** 抢断次数*/
    private int stl;
    /** 盖帽次数 */
    private int blk;
    /** 失误次数*/
    private int to;
    /** 犯规次数*/
    private int pf;
    /** 总命中次数*/
    private int fgm;
    /** 总出手次数*/
    private int fga;
    /** 罚球次数*/
    private int fta;
    /** 罚球命中数*/
    private int ftm;
    /** 三分投篮*/
    private int pa3;
    /** 三分命中数*/
    private int pm3;
    /** */
    private int total;

    public PlayerAvgInfo() {

    }

    public PlayerAvgInfo(long teamId, int playerId) {
        this.teamId = teamId;
        this.playerId = playerId;
        this.total = 0;
    }

    public void update(Map<EActionType, LBFloat> sourceMap) {
        LBFloat v = sourceMap.get(EActionType.min);
        if (v == null || v.intValue() == 0) {
            return;
        }
        this.total++;
        sourceMap.forEach((key, val) -> update(key, val.getVal()));
        this.save();
    }

    private void update(EActionType type, float val) {
        if (type == EActionType.fgm) {
            this.fgm += val;
        } else if (type == EActionType.ftm) {
            this.ftm += val;
        } else if (type == EActionType.pts) {
            this.pts += val;
        } else if (type == EActionType._3pa) {
            this.pa3 += val;
        } else if (type == EActionType.reb) {
            this.reb += val;
        } else if (type == EActionType.ast) {
            this.ast += val;
        } else if (type == EActionType.stl) {
            this.stl += val;
        } else if (type == EActionType.blk) {
            this.blk += val;
        } else if (type == EActionType.to) {
            this.to += val;
        } else if (type == EActionType.fga) {
            this.fga += val;
        } else if (type == EActionType.pf) {
            this.pf += val;
        } else if (type == EActionType.fta) {
            this.fta += val;
        } else if (type == EActionType._3pm) {
            this.pm3 += val;
        }

    }

    public float getAbility(EActionType type) {
        if (type == EActionType.fgm) {
            return this.fgm;
        } else if (type == EActionType.fga) {
            return this.fga;
        } else if (type == EActionType.fgp) {
            return this.fgm * 1F / Math.max(1, fga);
        } else if (type == EActionType.ftm) {
            return this.ftm;
        } else if (type == EActionType.fta) {
            return this.fta;
        } else if (type == EActionType.ftp) {
            return this.ftm * 1F / Math.max(1, fta);
        } else if (type == EActionType.pts) {
            return this.pts;
        } else if (type == EActionType._3pa) {
            return this.pa3;
        } else if (type == EActionType._3pm) {
            return this.pm3;
        } else if (type == EActionType._3pp) {
            return this.pm3 * 1F / Math.max(1, pa3);
        } else if (type == EActionType.reb) {
            return this.reb;
        } else if (type == EActionType.ast) {
            return this.ast;
        } else if (type == EActionType.stl) {
            return this.stl;
        } else if (type == EActionType.blk) {
            return this.blk;
        } else if (type == EActionType.to) {
            return this.to;
        } else if (type == EActionType.pf) {
            return this.pf;
        }
        return 0;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getFgm() {
        return fgm;
    }

    public void setFgm(int fgm) {
        this.fgm = fgm;
    }

    public int getFtm() {
        return ftm;
    }

    public void setFtm(int ftm) {
        this.ftm = ftm;
    }

    public int getPts() {
        return pts;
    }

    public void setPts(int pts) {
        this.pts = pts;
    }

    public int getAst() {
        return ast;
    }

    public void setAst(int ast) {
        this.ast = ast;
    }

    public int getStl() {
        return stl;
    }

    public void setStl(int stl) {
        this.stl = stl;
    }

    public int getBlk() {
        return blk;
    }

    public void setBlk(int blk) {
        this.blk = blk;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public int getReb() {
        return reb;
    }

    public void setReb(int reb) {
        this.reb = reb;
    }

    public int getFga() {
        return fga;
    }

    public void setFga(int fga) {
        this.fga = fga;
    }

    public int getFta() {
        return fta;
    }

    public void setFta(int fta) {
        this.fta = fta;
    }

    public int getPa3() {
        return pa3;
    }

    public void setPa3(int pa3) {
        this.pa3 = pa3;
    }

    public int getPm3() {
        return pm3;
    }

    public void setPm3(int pm3) {
        this.pm3 = pm3;
    }

    public int getPf() {
        return pf;
    }

    public void setPf(int pf) {
        this.pf = pf;
    }

    @Override
    public String getSource() {
        return StringUtil.formatSQL(teamId, playerId, pts, reb, ast, stl, blk, to, pf, fgm, fga, fta, ftm, pa3, pm3, total);
    }

    @Override
    public String getRowNames() {
        return "team_id, player_id, pts, reb, ast, stl, blk, `to`, pf, fgm, fga, fta, ftm, pa3, pm3, total";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(teamId, playerId, pts, reb, ast, stl, blk, to, pf, fgm, fga, fta, ftm, pa3, pm3, total);
    }

    @Override
    public String getTableName() {
        return "t_u_player_source";
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public void del() {
        save();
    }
}
