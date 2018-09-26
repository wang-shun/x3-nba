package com.ftkj.cfg.battle;

import com.ftkj.enums.EActionType;

/**
 * 子行为配置信息
 */
public interface SubActionBean {
    /** id */
    int getId();

    /** 子行为类型 */
    EActionType getAction();

    /**
     * 行为概率(1-100)(0或不填使用默认规则).
     * 目前可以控制如下行为:
     * 盖帽时 : 盖帽球员所在球队获得球权的概率
     * 罚球时 : 命中概率(不填或0时则使用球员罚球能力)
     * 篮板时 : 篮板球员所在球队获得球权的概率
     */
    int getChance();

    int getChance(int defaultValue);

    /**
     * 子行为附加参数 1
     * <p>
     * 使用球员或教练技能时的技能id, 更换战术时的战术进攻id
     * <ul>
     * <li><b>pts</b>: 得分</li>
     * </ul>
     */
    int getVi1();

    /**
     * 子行为附加参数 2
     * <p>
     * 更换战术时的战术防守id
     */
    int getVi2();

    /** 执行此行为的球队, 不为 null 时强制选择执行方, 为 null 时按业务逻辑选择 */
    HomeAway getHomeAway();

    /** 执行此行为的球队是否是主场球队 */
    boolean isHome();
}
