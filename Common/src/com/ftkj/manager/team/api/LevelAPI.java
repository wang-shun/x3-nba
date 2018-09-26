package com.ftkj.manager.team.api;

import com.ftkj.cfg.TeamExpBean;
import com.ftkj.console.GradeConsole;
import com.ftkj.manager.team.TeamGrade;

public class LevelAPI {

    /**
     * 检查球队是否升级，升级失败返回空；
     *
     * @param tm  当前等级
     * @param exp 当前经验
     * @return 最新等级，剩余经验
     */
    public static TeamGrade checkTeamGrade(int currLv, int exp) {
        TeamExpBean bean = GradeConsole.getTeamExpBean(currLv);
        if (bean == null) {
            return null;
        }
        TeamExpBean newLv = GradeConsole.getTeamExpBeanByTotal(exp);
        if (newLv != null && newLv.getLv() > currLv) {
            return new TeamGrade(newLv.getLv(), exp - newLv.getTotal(), newLv.getPrice(), newLv.getAwardList());
        }
        return null;
    }

}
