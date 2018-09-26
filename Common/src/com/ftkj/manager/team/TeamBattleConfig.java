package com.ftkj.manager.team;

import com.ftkj.enums.TacticId;
import com.ftkj.manager.coach.Coach;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.StringUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tim.huang 2017年3月2日
 * 比赛配置
 */
public class TeamBattleConfig implements Serializable {
    private static final long serialVersionUID = 2L;

    // 默认道具配置
    private List<PropSimple> props;
    // 默认进攻跟防守战术
    private TacticId offenseTactics;
    private TacticId defenseTactics;
    //默认携带的战术
    private List<TacticId> equTacticsList;
    //默认教练
    private Coach coach;

    public TeamBattleConfig(String ps, String ts, int jg, int fs, Coach coach) {
        this.props = PropSimple.getPropBeanByStringNotConfig(ps);
        this.equTacticsList = Arrays.stream(StringUtil.toIntArray(ts, StringUtil.DEFAULT_ST)).mapToObj(id -> TacticId.convert(id))
                .collect(Collectors.toList());
        if (!this.equTacticsList.stream().filter(t -> t.getId() == jg).findFirst().isPresent()) {
            this.equTacticsList.add(TacticId.convert(jg));
        }
        if (!this.equTacticsList.stream().filter(t -> t.getId() == fs).findFirst().isPresent()) {
            this.equTacticsList.add(TacticId.convert(fs));
        }
        this.offenseTactics = TacticId.convert(jg);
        this.defenseTactics = TacticId.convert(fs);
        this.coach = coach;
    }

    public Coach getCoach() {
        return coach;
    }

    public int getCid() {
        return this.coach == null ? 0 : this.coach.getCid();
    }

    public List<TacticId> getEquTacticsList() {
        return equTacticsList;
    }

    public TacticId getOffenseTactics() {
        return offenseTactics;
    }

    public TacticId getOffenseTactics(TacticId defaultTt) {
        return offenseTactics != null ? offenseTactics : defaultTt;
    }

    public TacticId getDefenseTactics() {
        return defenseTactics;
    }

    public TacticId getDefenseTactics(TacticId defaultTt) {
        return defenseTactics != null ? defenseTactics : defaultTt;
    }

    public List<PropSimple> getProps() {
        return props;
    }

}
