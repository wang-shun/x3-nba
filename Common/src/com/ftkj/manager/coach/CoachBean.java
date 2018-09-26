package com.ftkj.manager.coach;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.console.CoachConsole;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticId;
import com.ftkj.util.StringUtil;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年9月13日
 * 教练配置
 */
public class CoachBean extends ExcelBean implements Serializable {
    private static final long serialVersionUID = 1L;   
    /** 教练基础ID*/
    private int cId;
    /** 教练名字*/
    private String coachName;
    /** 教练战术*/
    private String tactics;
    /** */
    private String sids;
    /** 战术影响*/
    private String tacticsVal;
    /** 教练等级*/
    private int coachLevel;

    /** 影响的战术*/
    private List<CoachTactics> tacticsList;
    /** 教练佩戴的技能*/
    private Map<Integer, CoachSkillBean> coachSkillMap;

    public void init() {
        int[] tmp = StringUtil.toIntArray(tactics, StringUtil.DEFAULT_ST);
        int[] tmpSids = StringUtil.toIntArray(sids, StringUtil.DEFAULT_ST);
        String[] tmpVals = StringUtil.toStringArray(tacticsVal, StringUtil.DEFAULT_ST);
        CoachTactics ct = null;
        this.tacticsList = Lists.newArrayList();
        for (int i = 0; i < tmp.length; i++) {
            int[] pos = StringUtil.toIntArray(tmpVals[i], StringUtil.DEFAULT_EQ);
            ct = new CoachTactics(TacticId.convert(tmp[i]), pos[0], pos[1], pos[2], pos[3], pos[4]);
            this.tacticsList.add(ct);
        }

        this.coachSkillMap = Arrays.stream(tmpSids)
                .mapToObj(id -> CoachConsole.getCoachSkillBean(id))
                .collect(Collectors.toMap(key -> key.getSid(), val -> val));
    }

    public CoachTactics getTactics(TacticId tid) {
        return this.tacticsList.stream().filter(t -> t.getTactics() == tid).findFirst().orElse(null);
    }

    public float getTactics(TacticId id, EPlayerPosition pos, float defaultv) {
        CoachTactics ct = getTactics(id);
        if (ct == null) {
            return defaultv;
        }
        Float val = ct.getPosCapRate(pos);
        return val != null ? val : defaultv;
    }

    public void setTactics(String tactics) {
        this.tactics = tactics;
    }

    public String getSids() {
        return sids;
    }

    public void setSids(String sids) {
        this.sids = sids;
    }

    public void setCoachSkillMap(Map<Integer, CoachSkillBean> coachSkillMap) {
        this.coachSkillMap = coachSkillMap;
    }

	public CoachSkillBean getSkill(int sid) {
        return coachSkillMap.get(sid);
    }

    public List<CoachTactics> getTacticsList() {
        return tacticsList;
    }

    public Map<Integer, CoachSkillBean> getCoachSkillMap() {
        return coachSkillMap;
    }

    @Override
    public void initExec(RowData row) {

    }

	public int getCoachLevel() {
		return coachLevel;
	}

	public void setCoachLevel(int coachLevel) {
		this.coachLevel = coachLevel;
	}

	public String getCoachName() {
		return coachName;
	}

	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

}
