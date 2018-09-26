package com.ftkj.console;

import static com.ftkj.cfg.MMatchDivisionBean.STAR_IDX_MAX;
import static com.ftkj.cfg.MMatchDivisionBean.STAR_IDX_MIN;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.cfg.ActionCondition;
import com.ftkj.cfg.MMatchConditionBean;
import com.ftkj.cfg.MMatchDivisionBean;
import com.ftkj.cfg.MMatchLevBean;
import com.ftkj.cfg.MMatchLevBean.SpecialHandleMatch;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.match.MainMatchLevel;
import com.ftkj.manager.match.TeamMainMatch;
import com.google.common.collect.ImmutableMap;

/**
 * 主线赛程.
 */
public class MainMatchConsole extends AbstractConsole implements ValidateBean {
    private static final Logger log = LoggerFactory.getLogger(MainMatchConsole.class);
    /** 赛区. map[赛区配置id, Bean] */
    private static Map<Integer, MMatchDivisionBean> divs = Collections.emptyMap();
    /** 关卡. map[关卡配置id, Bean] */
    private static Map<Integer, MMatchLevBean> levs = Collections.emptyMap();
    /** 胜利条件. map[条件配置id, Bean] */
    private static Map<Integer, MMatchConditionBean> winConditions = Collections.emptyMap();
    /** 赛区和关卡. map[赛区id, set[关卡id]] */
    private static Map<Integer, Set<MMatchLevBean>> levsOfDiv = Collections.emptyMap();
    

    public static void init() {
        Map<Integer, MMatchDivisionBean> divs = new LinkedHashMap<>();
        Map<Integer, MMatchLevBean> levs = new LinkedHashMap<>();
        Map<Integer, Set<MMatchLevBean>> levsOfDiv = new LinkedHashMap<>();
        ImmutableMap.Builder<Integer, MMatchConditionBean> wcs = ImmutableMap.builder();

        for (MMatchDivisionBean divb : CM.mMatchDivs) {
            divs.put(divb.getId(), divb);
        }
        for (MMatchLevBean levb : CM.mMatchLevs) {
            levs.put(levb.getId(), levb);
            levsOfDiv.computeIfAbsent(levb.getDivId(), levId -> new LinkedHashSet<>()).add(levb);
        }
        for (MMatchConditionBean wc : CM.mMatchWcs) {
            wcs.put(wc.getId(), wc);
        }

        //计算后续赛区和关卡
        for (MMatchDivisionBean divb : divs.values()) {
            if (divb.hasEnablePre()) {
                for (Integer preId : divb.getEnablePreIds()) {
                    divs.get(preId).getNextDivs().add(divb.getId());

                }
            }
        }
        for (MMatchLevBean levb : levs.values()) {
            if (levb.getEnablePreId() > 0) {
                levs.get(levb.getEnablePreId()).getNextLevs().add(levb.getId());
            }
            if (levb.getAssocChampionLev() > 0) {
                levs.get(levb.getAssocChampionLev()).addAssociateRegularLev(levb.getId());
            }
        }

        MainMatchConsole.divs = ImmutableMap.copyOf(divs);
        MainMatchConsole.levs = ImmutableMap.copyOf(levs);
        MainMatchConsole.levsOfDiv = ImmutableMap.copyOf(levsOfDiv);
        MainMatchConsole.winConditions = wcs.build();
        log.debug("divs {} levs {} wcs {}", divs.size(), levs.size(), winConditions.size());
    }

    @Override
    public void validate() {
        int defaultOpenLev = ConfigConsole.global().mMatchDefaultOpenLev;
        final int trainingNpcSize = getTargetNum();
        MMatchLevBean initLev = levs.get(defaultOpenLev);
        if (defaultOpenLev <= 0 || initLev == null) {
            throw exception("主线赛程. 默认开启的关卡配置id %s 没有配置", defaultOpenLev);
        }
        if (initLev.getEnablePreId() > 0) {
            throw exception("主线赛程. 默认开启的关卡配置id %s 不需要配置前置关卡", defaultOpenLev);
        }

        for (MMatchDivisionBean div : divs.values()) {
            validate(div);
        }
        for (MMatchLevBean lev : levs.values()) {
            validate(trainingNpcSize, lev);
        }
        for (MMatchConditionBean wc : winConditions.values()) {
            validate(wc);
        }
    }

    private void validate(MMatchConditionBean wcb) {
        int id = wcb.getId();
        switch (wcb.getType()) {
            //（球队型）
            case Team_Win_Point: //球队赢 vi1 分
                if (wcb.getVi1() <= 0) {
                    throw exception("主线赛程. 胜利条件 %s 是 球队赢 vi1 分, vi1 <= 0", id);
                }
                break;
            case Team_Overtime_Win: //比赛进入加时并获胜
                break;
            case Team_Last_Shot: //绝杀比赛获胜
                break;
            case Team_Point_Win: //球队获胜并达到 vi1 分
                if (wcb.getVi1() <= 0) {
                    throw exception("主线赛程. 胜利条件 %s 是 球队获胜并达到 vi1 分, vi1 <= 0", id);
                }
                break;
            //            case Team_Comeback: //落后 vi1 分反超获胜
            //                if (wcb.getVi1() <= 0) {
            //                    throw exception("主线赛程. 胜利条件 %s 是 落后 vi1 分反超获胜, vi1 <= 0", id);
            //                }
            //                break;
            //（球员型）
            case Multi_Player_Any_Num_Action_Type: //vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个.
                String msg0 = "vi1 名球员每人满足 N 个 tuple 数据对(类型和值)中任意 vi2 个";
                if (wcb.getVi1() <= 0 || wcb.getVi2() <= 0) {
                    throw exception("主线赛程. 胜利条件 %s 是 %s. vi1 %s 或者 vi2 %s <=0", id, msg0, wcb.getVi1(), wcb.getVi2());
                }
                validateCondition(id, wcb, msg0);
                break;
            case Multi_Player_Multi_Action_Type: //vi1 名球员每人满足 N 个 tuple 数据对(类型和值)
                String msg1 = "vi1 名球员每人满足 N 个 tuple 数据对(类型和值)";
                if (wcb.getVi1() <= 0) {
                    throw exception("主线赛程. 胜利条件 %s 是 %s. vi1 %s <=0", id, msg1, wcb.getVi2());
                }
                validateCondition(id, wcb, msg1);
                break;
            case Multi_Player_Any_Num_Action_Type_Of_Any_Quarter: //vi1 名球员每人在任意一节中满足 N 个 tuple 数据对(类型和值), n个数据必须都在同一节
                String msg2 = "vi1 名球员每人在任意一节中满足 N 个 tuple 数据对(类型和值)";
                if (wcb.getVi1() <= 0) {
                    throw exception("主线赛程. 胜利条件 %s 是 %s. vi1 %s <=0", id, msg2, wcb.getVi2());
                }
                validateCondition(id, wcb, msg2);
                break;
            case All_Player_Any_Num_Action_Type: //所有上场球员每人满足 N 个 tuple 数据对(类型和值)
                validateCondition(id, wcb, "所有上场球员每人满足 N 个 tuple 数据对(类型和值)");
                break;
        }
    }

    private void validateCondition(int id, MMatchConditionBean wcb, String msg) {
        for (ActionCondition cond : wcb.getConditions().values()) {
            if (cond.getValue() <= 0) {
                throw exception("主线赛程. 胜利条件 %s 是 %s. 数据类型 %s 要求的值 %s <= 0", id, msg, cond.getAct(), cond.getValue());
            }
        }
    }

    private void validate(int trainingNpcSize, MMatchLevBean levb) {
        int levid = levb.getId();
        MMatchDivisionBean divb = getDivBean(levb.getDivId());
        if (divb == null) {
            throw exception("主线赛程. 关卡 %s 所属的赛区 %s 没有配置", levid, levb.getDivId());
        }
        if (levb.getEnablePreId() > 0 && getLevBean(levb.getEnablePreId()) == null) {
            throw exception("主线赛程. 关卡 %s. 开启本关卡需要的上一个关卡 %s 没有配置", levid, levb.getEnablePreId());
        }
        levb.getStars().forEach((starNum, star) -> {
            PropConsole.validate(star.getProps(), "主线赛程. 关卡 %s. %s 星, ", levid, starNum);
            DropConsole.validate(star.getDrops(), "主线赛程. 关卡 %s. %s 星, ", levid, starNum);
            if (!levb.isChampionship() && starNum > 1) {
                for (Integer wc : star.getWinConditionId()) {
                    if (winConditions.get(wc) == null) {
                        throw exception("主线赛程. 关卡 %s. %s 星, 获取此星级需要的胜利条件 %s 没有配置", levid, starNum, wc);
                    }
                }
            }
        });
        NPCConsole.validate(levb.getNpcId(), "主线赛程. 关卡 %s.", levid);
        levb.getChampionNpc().forEach(npc ->
                NPCConsole.validate(npc, "主线赛程. 关卡 %s. 锦标赛", levid));
        if (levb.isChampionship()) {
            if (levb.getChampionNpc().size() != trainingNpcSize) {
                throw exception("主线赛程. 关卡 %s. 是锦标赛, npc数量 %s 小于 %s", levid, levb.getChampionNpc().size(), trainingNpcSize);
            }
        }
        if (levb.isRegular() && levb.getAssocChampionLev() > 0) {
            if (getLevBean(levb.getAssocChampionLev()) == null) {
                throw exception("主线赛程. 关卡 %s. 是常规赛, 关联的锦标赛关卡 %s 没有配置", levid, levb.getAssocChampionLev());
            }
        }
        if (levb.getEnablePreId() == levid) {
            throw exception("主线赛程. 关卡 %s. 开启本关卡需要的上一个关卡id是自己", levid);
        }
        for (SpecialHandleMatch shm : levb.getSpecialHandle().values()) {
            if (BattleCustomConsole.getBean(shm.getBattleCustomId()) == null) {
                throw exception("主线赛程. 关卡 %s 自定义比赛第 %s 场id %s 没有配置", levid, shm.getNum(), shm.getBattleCustomId());
            }
        }
    }

    private void validate(MMatchDivisionBean divb) {
        int divid = divb.getId();
        divb.getStarAwards().forEach((starId, sa) -> {
            if (starId < STAR_IDX_MIN || starId > STAR_IDX_MAX) {
                throw exception("主线赛程. 赛区 %s. 星级礼包id只能为 (%s, %s)", divid, STAR_IDX_MIN, STAR_IDX_MAX);
            }
            PropConsole.validate(sa.getProps(), "主线赛程. 赛区 %s. 星级礼包 %s 星, ", divid, sa.getNum());
            DropConsole.validate(sa.getDrop(), "主线赛程. 赛区 %s. 星级礼包 %s 星, ", divid, sa.getNum());
        });
        if (divb.hasEnablePre()) {
            for (Integer preId : divb.getEnablePreIds()) {
                if (getDivBean(preId) == null) {
                    throw exception("主线赛程. 赛区 %s. 开启本赛区需要的前置赛区 %s 没有配置", divid, preId);
                }
            }
        }
    }

    /** 计算本赛区非锦标赛关卡星数 */
    public static int sumDivStarNum(Map<Integer, MainMatchLevel> levs, int divId) {
        int starNum = 0;
        for (MMatchLevBean levb : getLevsOfDiv(divId)) {
            MainMatchLevel lev = levs.get(levb.getId());
            if (lev == null) {
                continue;
            }

            starNum += lev.getStar();
        }
        return starNum;
    }

    /** 汇总赛区信息 */
    private static DivStatistics summarizingDiv(TeamMainMatch tmm, Collection<Integer> divRids) {
        DivStatistics ds = new DivStatistics();
        ds.allPreDivStrategyLevEnabled = true;
        for (Integer divRid : divRids) {
            for (MMatchLevBean levb : getLevsOfDiv(divRid)) {
                MainMatchLevel lev = tmm.getLevels().get(levb.getId());
                if (lev == null) {
                    ds.allPreDivStrategyLevEnabled = false;
                    break;//short circuit
                }
                ds.regularStarSum += lev.getStar();
            }
        }
        return ds;
    }

    public static void setLevs(Map<Integer, MMatchLevBean> levs) {
        MainMatchConsole.levs = levs;
    }

    /** 关卡汇总 */
    private static final class DivStatistics {
        /** 所有前置赛区的关卡是否已经根据条件开启 */
        private boolean allPreDivStrategyLevEnabled;
        /** 已经开启关卡的总星数 */
        private int regularStarSum;
    }

    /** 校验赛区前置开启条件 */
    public static ErrorCode checkPreDiv(TeamMainMatch tmm, MMatchDivisionBean divb) {
        if (divb.hasEnablePre() && !hasDiv(tmm, divb.getId())) {//本赛区有开启条件
            log.debug("mainmatch checkPreDiv, check pre div. tid {} div {} prediv {}", tmm.getTeamId(), divb.getId(), divb.getEnablePreIds());
            boolean hasAllPreDiv = tmm.hasDiv(divb.getEnablePreIds());
            if (!hasAllPreDiv) {
                return ErrorCode.MMatch_Div_Pre_Null;
            }
            if (divb.getPreDivStrategy() > 0) {
                DivStatistics ds = summarizingDiv(tmm, divb.getEnablePreIds());
                log.debug("mainmatch checkPreDiv, check pre div. tid {} ds enabled {} star sum {} cfg {}", tmm.getTeamId(),
                        ds.allPreDivStrategyLevEnabled, ds.regularStarSum, divb);
                if (!ds.allPreDivStrategyLevEnabled) {
                    return ErrorCode.MMatch_Div_Pre_Regular;
                }
                if (divb.getEnablePreStar() > 0 && ds.regularStarSum < divb.getEnablePreStar()) {
                    return ErrorCode.MMatch_Div_Pre_Star;
                }
            }
        }
        return ErrorCode.Success;
    }

    /** 当前赛区是否已经开启关卡 */
    private static boolean hasDiv(TeamMainMatch tmm, int divId) {
        for (MMatchLevBean levb : MainMatchConsole.getLevsOfDiv(divId)) {
            if (tmm.getLevels().get(levb.getId()) != null) {
                return true;
            }
        }
        return false;
    }

    public static int getTargetNum() {
        return 1 << ConfigConsole.global().mMatchMaxStar;
    }

    public static MMatchDivisionBean getDivBean(int divId) {
        return divs.get(divId);
    }

    public static Map<Integer, Set<MMatchLevBean>> getLevsOfDiv() {
        return levsOfDiv;
    }

    public static Set<MMatchLevBean> getLevsOfDiv(int divId) {
        return levsOfDiv.get(divId);
    }

    public static MMatchLevBean getLevBean(int levId) {
        return levs.get(levId);
    }

    public static Map<Integer, MMatchConditionBean> getWinConditions() {
        return winConditions;
    }

    public static MMatchConditionBean getWinCondition(int winConditionId) {
        return winConditions.get(winConditionId);
    }
}
