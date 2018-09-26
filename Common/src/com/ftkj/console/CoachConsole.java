package com.ftkj.console;

import com.ftkj.manager.coach.CoachBean;
import com.ftkj.manager.coach.CoachSkillBean;
import com.ftkj.manager.skill.buff.SkillBuffer;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年9月18日
 * 教练控制台
 */
public class CoachConsole extends AbstractConsole {
    private static Map<Integer, CoachBean> coachMap;
    private static Map<Integer, CoachSkillBean> coachSkillMap;

    public static void init() {
        //初始化教练技能
        coachSkillMap = CM.coachSkills.stream()
                .peek(skill -> skill.setBuff(getSkillBuffer(skill.getBids())))
                .collect(toImmutableMap(b -> b.getSid(), b -> b));
        coachMap = CM.coachBeanList.stream()
                .peek(coach -> coach.init())
                .collect(toImmutableMap(b -> b.getcId(), b -> b));
    }

    private static List<SkillBuffer> getSkillBuffer(String bids) {
        int[] ids = StringUtil.toIntArray(bids, StringUtil.DEFAULT_ST);
        return Arrays.stream(ids)
                .mapToObj(id -> SkillConsole.getSkillBuffer(id))
                .collect(Collectors.toList());
    }

    public static List<CoachBean> getCoachList() {
        return Lists.newArrayList(coachMap.values());
    }

    public static CoachSkillBean getCoachSkillBean(int sid) {
        return coachSkillMap.get(sid);
    }

    public static CoachBean getCoachBean(int cid) {
        return coachMap.get(cid);
    }

}
