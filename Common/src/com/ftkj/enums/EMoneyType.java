package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Jay
 * @Description:货币枚举
 * @time:2017年3月16日 下午5:49:51
 */
public enum EMoneyType {
    // 这里的name对应道具配置config里的配置
    /** 球卷 */
    Money("money"),
    /** 绑定球券 */
    Bind_Money("bdmoney"),
    /** 经费 */
    Gold("gold"),
    /** 经验 */
    Exp("exp"),
    /** 建设费 */
    Build_Money("jsf"),;

    private String name;

    private EMoneyType(String name) {
        this.name = name;
    }

    public static final Map<String, EMoneyType> nameCaches = new HashMap<>();

    public String getConfigName() {
        return name;
    }

    static {
        for (EMoneyType t : values()) {
            String configName = t.getConfigName().toLowerCase();
            if (nameCaches.containsKey(configName)) {
                throw new Error("duplicate action cfg name :" + configName);
            }
            nameCaches.put(configName, t);
        }
    }

    public static EMoneyType convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    public static EMoneyType getMoneyByName(String name) {
        for (EMoneyType t : EMoneyType.values()) {
            if (t.name.equals(name.toLowerCase())) {
                return t;
            }
        }
        return null;
    }

}
