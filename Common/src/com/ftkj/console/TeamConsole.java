package com.ftkj.console;

import com.ftkj.cfg.TeamNumBean;
import com.ftkj.cfg.TeamNumBean.TeamNumType;
import com.ftkj.cfg.base.ExcelParseUtil;
import com.ftkj.cfg.base.ValidateBean;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * 球队配置
 */
public class TeamConsole extends AbstractConsole implements ValidateBean {
    /** 球队次数及购买次数消耗配置. map[id, Bean] */
    private static Map<TeamNumType, TeamNumBean> nums = Collections.emptyMap();

    public static void init() {
        Map<TeamNumType, TeamNumBean> nums = new EnumMap<>(TeamNumType.class);
        for (TeamNumBean.Builder builder : CM.teamNums) {
            TeamNumBean bb = builder.build();
            if(bb.getNumType() == null) continue;
            nums.put(bb.getNumType(), bb);
        }

        TeamConsole.nums = nums;
    }

    @Override
    public void validate() {
        for (TeamNumBean info : nums.values()) {
            validate(info);
        }
    }

    private void validate(TeamNumBean info) {
        int id = info.getNumType().getType();
        ExcelParseUtil.validate(info.getNumberAndCurrencies(), "球队次数. 购买次数消耗配置. 类型 %s ", id);
    }

    public static TeamNumBean getNums(TeamNumType type) {
        return nums.get(type);
    }
}
