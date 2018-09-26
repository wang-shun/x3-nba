package com.ftkj.enums;

import com.ftkj.event.EEventType;

import java.util.Arrays;

public enum EPayType {

    充值(0, EEventType.充值, true),
    GM充值(1, EEventType.GM充值, false),
    // 福利类型，不算VIP成长
    每日充值特惠(7, EEventType.购买每日充值特惠, false),
    成长基金(10, EEventType.购买成长基金, false),
    装备周卡(11, EEventType.购买装备强化周卡, false),
    装备月卡(12, EEventType.购买装备经验月卡, false),
    头像周卡(13, EEventType.购买头像碎片周卡, false),
    球星卡月卡(14, EEventType.购买球星卡经验月卡, false),
    荣耀点月卡(15, EEventType.购买荣耀点月卡, false),
    经验手册月卡(16, EEventType.购买经验手册月卡, false),
    训练点月卡(17, EEventType.购买训练点月卡, false),;

    private int type;
    /**
     * 是否VIP成长计算
     */
    private boolean isVip;

    /**
     * 事件类型
     */
    private EEventType event;

    /**
     * 是否算充值金额
     */
    private boolean recharge;

    /**
     * @param type
     * @param isVip 是否算为VIP成长
     */
    private EPayType(int type, EEventType event, boolean isVip) {
        this.type = type;
        this.event = event;
        this.isVip = isVip;
    }

    public boolean isVip() {
        return this.isVip;
    }

    public EEventType getEvent() {
        return event;
    }

    /**
     * 取充值类型
     */
    public static EPayType convertType(int type) {
        return Arrays.stream(EPayType.values())
                .filter(t -> t.type == type)
                .findFirst()
                .orElse(EPayType.充值);
    }

    public int getType() {
        return type;
    }
}
