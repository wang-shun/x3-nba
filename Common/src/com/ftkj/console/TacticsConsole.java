package com.ftkj.console;

import com.ftkj.cfg.TacticsCapBean;
import com.ftkj.cfg.TacticsPowerBean;
import com.ftkj.cfg.TacticsStudyBean;
import com.ftkj.cfg.TacticsUpBean;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.TacticId;
import com.ftkj.enums.TacticType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.tactics.Tactics;
import com.ftkj.manager.tactics.TacticsBean;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang 2017年3月3日 战术控制
 */
public class TacticsConsole {
    private static final int[] GRADE = {1, 20, 40, 60, 80, 100, 110};

    private static Map<TacticId, TacticsBean> tactics;
    private static List<TacticsBean> offenses;
    private static List<TacticsBean> defenses;
    /** 突破天数允许参数 */
    public static final int[] Buff_Day = {1, 3, 7};
    /** 战术最大等级 */
    public static final int Max_Lv = 10;
    //	/** 战术加成配置 */
    //	public static List<TacticsCapBean> tacticsCapList;
    /** 学习配置 */
    private static List<TacticsStudyBean> tacticsStudy;
    /** 升级配置 */
    private static List<TacticsUpBean> tacticsUps;
    /** 默认学习战术 */
    private static List<TacticId> defaultStudy;
    private static String defTacticsStr;
    private static Map<TacticId, TacticsPowerBean> tacticsPowers;
    private static Map<TacticId, TacticId> restrain = new EnumMap<>(TacticId.class);
    private static Map<TacticId, TacticId> restrainReverse = new EnumMap<>(TacticId.class);

    public static void init() {
        tacticsStudy = CM.tacticsStudyBeanList;
        tacticsUps = CM.tacticsUpBeanList;
        tacticsPowers = CM.tacticsPowerBeanList.stream().collect(Collectors.toMap(b -> b.getTid(), b -> b));
        // 转换处理
        Map<TacticId, Map<Integer, TacticsCapBean>> caps = Maps.newHashMap();
        for (TacticsCapBean capBean : CM.tacticsCaps) {
            TacticId id = TacticId.convert(capBean.getId());
            caps.computeIfAbsent(id, k -> new HashMap<>())
                    .put(capBean.getLv(), capBean);
        }

        tactics = Maps.newHashMap();
        buildBean(caps, TacticId.强攻内线, TacticId.外线防守);
        buildBean(caps, TacticId.外线投篮, TacticId.内线防守);
        buildBean(caps, TacticId.挡拆进攻, TacticId.联合防守);
        buildBean(caps, TacticId.老鹰进攻, TacticId.混合防守);
        buildBean(caps, TacticId.跑轰战术, TacticId.全场紧逼);
        buildBean(caps, TacticId.内线防守, TacticId.强攻内线);
        buildBean(caps, TacticId.外线防守, TacticId.外线投篮);
        buildBean(caps, TacticId.混合防守, TacticId.挡拆进攻);
        buildBean(caps, TacticId.联合防守, TacticId.老鹰进攻);
        buildBean(caps, TacticId.全场紧逼, TacticId.跑轰战术);

        initList();
        // 默认初始战术
        defaultStudy = Lists.newArrayList();
        defTacticsStr = ConfigConsole.getVal(EConfigKey.tactics_default);
        if (defTacticsStr == null || defTacticsStr.equals("")) { defTacticsStr = "11,21"; }
        for (String id : defTacticsStr.split(",")) {
            defaultStudy.add(TacticId.convert(Integer.parseInt(id)));
        }
    }

    private static void initList() {
        offenses = tactics.values().stream().filter(tc -> tc.getType() == TacticType.Offense).collect(Collectors.toList());
        defenses = tactics.values().stream().filter(tc -> tc.getType() == TacticType.Defense).collect(Collectors.toList());
    }

    private static void buildBean(Map<TacticId, Map<Integer, TacticsCapBean>> tacticsCapBeanMap, TacticId id, TacticId restrain) {
        tactics.put(id, new TacticsBean(id, restrain, tacticsCapBeanMap.get(id), tacticsPowers.get(id)));
        TacticsConsole.restrain.put(id, restrain);
        TacticsConsole.restrainReverse.put(restrain, id);
    }

    public static TacticsBean getBean(TacticId tid) {
        return tactics.get(tid);
    }

    public static TacticsBean getRandom(TacticType type) {
        if (type == TacticType.Offense) {
            return offenses.get(RandomUtil.randInt(offenses.size()));
        } else {
            return defenses.get(RandomUtil.randInt(offenses.size()));
        }
    }

    public static int getGradeInt(int grade) {
        return GRADE[grade];
    }

    /** 获取克制谁 */
    public static TacticId getRestrain(TacticId id) {
        if (id == null) {
            return null;
        }
        return restrain.get(id);
    }

    /** 获取被谁克制 */
    public static TacticId getRestrainReverse(TacticId id) {
        if (id == null) {
            return null;
        }
        return restrainReverse.get(id);
    }

    /**
     * 取学习新战术所需要的等级
     *
     * @param tactics 约定，小于20是进攻，大于20是防守
     * @param list
     * @return
     */
    public static int getStudyLv(TacticId tactics, Collection<Tactics> list) {
        int num = (int) list.stream().filter(ta -> ta.getTactics().getType() == tactics.getType()).count();
        return tacticsStudy.get(num).getNeedLv();
    }

    /**
     * 取学习新战术所需要的费用
     *
     * @param tactics 约定，小于20是进攻，大于20是防守
     * @param list
     * @return
     */
    public static int getStudyMoney(TacticId tactics, Collection<Tactics> list) {
        int num = (int) (int) list.stream().filter(ta -> ta.getTactics().getType() == tactics.getType()).count();
        return tacticsStudy.get(num).getNeedGlod();
    }

    /**
     * 系统默认学习战术，创建球队用
     *
     * @return
     */
    public static List<TacticId> getDefaultStudy() {
        return defaultStudy;
    }

    public static String getDefaultStudyStr() {
        return defTacticsStr;
    }

    /**
     * 升级需要道具,数量
     *
     * @param lv    当前等级
     * @param index 下标
     * @return
     */
    public static PropSimple getUpLvPropNum(int lv, int index) {
        // 注意,10,11时是突破道具
        return tacticsUps.get(lv - 1).getNeedPropList().get(index);
    }

    /**
     * 取突破所需要的道具
     *
     * @param day
     * @return
     */
    public static PropSimple getBuffPropNum(int day) {
        int index = 0;
        for (int i = 0; i < Buff_Day.length; i++) {
            if (day == Buff_Day[i]) {
                index = i;
                break;
            }

        }
        return tacticsUps.get(10).getNeedPropList().get(index);
    }

}
