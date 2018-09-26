package com.ftkj.enums.battle;

/**
 * 暂停游戏后恢复策略.
 */
public enum ResumeType {
    /** 由客户端行为触发自定恢复 */
    client_act,
    /** 手工恢复 */
    manual
    //
    ;

    public static ResumeType convertByCfgName(String cfgName) {
        if (cfgName == null || cfgName.isEmpty()) {
            return manual;
        }
        return valueOf(cfgName);
    }
}
