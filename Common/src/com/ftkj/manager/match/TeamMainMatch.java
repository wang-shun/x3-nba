package com.ftkj.manager.match;

import com.ftkj.util.concurrent.X3Collectors;
import com.ftkj.util.concurrent.X3Collectors.MaxIdMapTuple;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 球队主线赛程信息
 */
public class TeamMainMatch implements Serializable {
    private static final long serialVersionUID = -6946806051661152828L;
    private long teamId;
    /** 球队主线赛程信息 */
    private MainMatch mainMatch;
    /** 关卡. map[关卡配置id, MainMatchDivision] */
    private Map<Integer, MainMatchDivision> divs;
    /** 节点. map[节点配置id, MainMatchLevel] */
    private Map<Integer, MainMatchLevel> levels;

    /** 每日限制掉落道具关联的日期 */
    private LocalDate limitPropsDay;
    /** 每日限制掉落道具, 已经掉落的数量. map[道具组id, 已经掉落的数量] map[propTid, num] */
    private Map<Integer, Integer> limitProps;
    /** 每日限制掉落道具 redis key */
    private String limitPropsRedisKey;

    //玩家自增长id，每个玩家自己维护该字段
    private AtomicInteger divId = new AtomicInteger();
    private AtomicInteger levelId = new AtomicInteger();

    public TeamMainMatch(long teamId, MainMatch mainMatch, Map<Integer, MainMatchDivision> divs, Map<Integer, MainMatchLevel> levels) {
        this.teamId = teamId;
        this.mainMatch = mainMatch;
        this.divs = divs;
        this.levels = levels;
    }

    public TeamMainMatch(long teamId, MainMatch mainMatch, List<MainMatchDivision> levs, List<MainMatchLevel> levels) {
        this.teamId = teamId;
        this.mainMatch = mainMatch;

        MaxIdMapTuple<Integer, MainMatchDivision> divmmt = X3Collectors.collector(levs, MainMatchDivision::getId, MainMatchDivision::getResourceId);
        this.divs = divmmt.getMap();
        this.divId.set(divmmt.getMaxIdInt());

        MaxIdMapTuple<Integer, MainMatchLevel> levmmt = X3Collectors.collector(levels, MainMatchLevel::getId, MainMatchLevel::getResourceId);
        this.levels = levmmt.getMap();
        this.levelId.set(levmmt.getMaxIdInt());
    }

    public MainMatchLevel newMainMatchLevel() {
        return new MainMatchLevel(teamId, levelId.incrementAndGet());
    }

    public MainMatchDivision newMainMatchDiv() {
        return new MainMatchDivision(teamId, divId.incrementAndGet());
    }

    public MainMatchLevel addLevel(MainMatchLevel newLevel) {
        return levels.put(newLevel.getResourceId(), newLevel);
    }

    public MainMatchDivision addDiv(MainMatchDivision newDiv) {
        return divs.put(newDiv.getResourceId(), newDiv);
    }

    public long getTeamId() {
        return teamId;
    }

    public MainMatch getMainMatch() {
        return mainMatch;
    }

    public MainMatch setMainMatch(MainMatch mainMatch) {
        this.mainMatch = mainMatch;
        return this.mainMatch;
    }

    public boolean hasDiv(Set<Integer> divRids) {
        return divs != null && divs.keySet().containsAll(divRids);
    }

    public Map<Integer, MainMatchDivision> getDivs() {
        return divs;
    }

    public MainMatchLevel getLevel(int levrid) {
        return levels.get(levrid);
    }

    public Map<Integer, MainMatchLevel> getLevels() {
        return levels;
    }

    public String getLimitPropsRedisKey() {
        return limitPropsRedisKey;
    }

    public void setLimitPropsRedisKey(String limitPropsRedisKey) {
        this.limitPropsRedisKey = limitPropsRedisKey;
    }

    public LocalDate getLimitPropsDay() {
        return limitPropsDay;
    }

    public void setLimitPropsDay(LocalDate limitPropsDay) {
        this.limitPropsDay = limitPropsDay;
    }

    public Map<Integer, Integer> getLimitProps() {
        return limitProps;
    }

    public void setLimitProps(Map<Integer, Integer> limitProps) {
        this.limitProps = limitProps;
    }
}
