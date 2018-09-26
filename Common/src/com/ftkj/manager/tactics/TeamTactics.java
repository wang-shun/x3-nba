package com.ftkj.manager.tactics;

import com.ftkj.db.domain.TacticsPO;
import com.ftkj.enums.TacticId;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年3月2日
 * 球队战术
 */
public class TeamTactics {

    private long teamId;
    private Map<TacticId, Tactics> tacticsMap;

    /**
     * 新建球队调用，学习默认战术
     *
     * @param teamId
     * @param list
     * @param level
     * @return
     */
    public static TeamTactics createTactics(long teamId, List<TacticId> list, int level) {
        List<Tactics> tacticsList = list.stream()
                .map(tc -> new TacticsPO(teamId, tc.getId(), level))
                .map(po -> new Tactics(po))
                .collect(Collectors.toList());
        TeamTactics tt = new TeamTactics(teamId, tacticsList);
        return tt;
    }

    public static TeamTactics createTeamTactics(long teamId, List<TacticId> list) {
        return createTactics(teamId, list, 1);
    }

    /**
     * 创建战术并保存
     *
     * @param teamId
     * @param tacticsList
     */
    public TeamTactics(long teamId, List<Tactics> tacticsList) {
        this.tacticsMap = Maps.newHashMap();
        this.teamId = teamId;
        tacticsList.forEach(tactics -> {
            this.tacticsMap.put(tactics.getTactics(), tactics);
        });
    }

    public Map<TacticId, Tactics> getTacticsMap() {
        return tacticsMap;
    }

    public Tactics getTactics(TacticId tid) {
        return this.tacticsMap.get(tid);
    }

    /**
     * 学习新战术
     */
    public void studyTactics(TacticId studyTactics) {
        Tactics t = new Tactics(new TacticsPO(teamId, studyTactics.getId(), 1));
        t.save();
        this.tacticsMap.put(studyTactics, t);
    }

    /**
     * 取默认战术
     *
     * @return
     */
    public Map<TacticId, Tactics> getDefault() {
        return null;
    }

    /**
     * 战术升级
     *
     * @param id
     */
    public int upLv(TacticId id) {
        Tactics tac = getTactics(id);
        tac.setLevel(tac.getLevel() + 1);
        tac.save();
        return tac.getLevel();
    }

    /**
     * 添加突破时间
     *
     * @param eTactics
     * @param day      return 时间毫秒数
     */
    public long addBuff(TacticId id, int day) {
        Tactics tac = getTactics(id);
        DateTime now = null;
        if (tac.getBuffTime() != null && tac.getBuffTime().isAfterNow()) {
            now = tac.getBuffTime();
        } else {
            now = DateTime.now();
        }
        tac.setBuffTime(now.plusDays(day));
        tac.save();
        return tac.getBuffTime().getMillis();
    }

}
