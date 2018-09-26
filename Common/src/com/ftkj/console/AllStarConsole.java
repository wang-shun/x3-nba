package com.ftkj.console;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ftkj.cfg.AllStarBean;
import com.ftkj.cfg.AllStarBean.AllStarAwardBuilder;
import com.ftkj.cfg.AllStarBean.AllStarBuilder;
import com.ftkj.cfg.AllStarBean.AllStarNpcBuilder;
import com.ftkj.cfg.AllStarBean.AwardBean;
import com.ftkj.cfg.AllStarBean.NpcBean;
import com.ftkj.cfg.AllStarBean.PersonalAwardBuilder;
import com.ftkj.cfg.AllStarBean.ScorePersonalAward;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.IntervalInt;
import com.google.common.collect.ImmutableMap;

/**
 * 挑战全明星
 */
public class AllStarConsole extends AbstractConsole implements ValidateBean {
    private static Map<Integer, AllStarBean> allStars = Collections.emptyMap();
    private static List<ScorePersonalAward> scorePersonalAwards  = new ArrayList<>();

    public static void init() {
        Map<Integer, AllStarBuilder> asbs = new HashMap<>();
        Map<Integer, List<NpcBean>> npcs = new HashMap<>();
        Map<Integer, List<AwardBean>> awards = new HashMap<>();
        for (AllStarBuilder builder : CM.allStars) {
            asbs.put(builder.getId(), builder);
        }
        for (AllStarNpcBuilder builder : CM.allStarNpcs) {
            npcs.computeIfAbsent(builder.getId(), id -> new ArrayList<>()).add(builder.build());
        }
        for (AllStarAwardBuilder builder : CM.allStarAwards) {
            awards.computeIfAbsent(builder.getId(), id -> new ArrayList<>()).add(builder.build());
        }

        ImmutableMap.Builder<Integer, AllStarBean> allstars = ImmutableMap.builder();
        asbs.forEach((k, v) -> allstars.put(k, v.build(npcs.get(k), awards.get(0))));//TODO  awards.get(k)
        AllStarConsole.allStars = allstars.build();
        for(PersonalAwardBuilder builder : CM.allStarPersonalAward) {
          List<PropSimple> rewards = PropSimple
              .getPropBeanByStringNotConfig(builder.getAward());
          ScorePersonalAward scorePersonalAward = new ScorePersonalAward(builder.getScore(),rewards);
          scorePersonalAwards.add(scorePersonalAward);
        }
        scorePersonalAwards.sort((s1, s2) -> s1.getScore() - s2.getScore());
    }
    
    @Override
    public void validate() {
        for (AllStarBean bean : allStars.values()) {
            validate(bean);
        }
    }

    private void validate(AllStarBean bean) {
        int id = bean.getId();
//        if (bean.getAwardTime().isBefore(bean.getEndTime())) {
//            throw exception("挑战全明星 %s. 每日奖励发放时间 %s 必须>= 结束时间 %s", id, bean.getAwardTime(),
//                    bean.getEndTime());
//        }
        for (int i = AllStarBean.Npc_Lev_Start; i < bean.getNpcs().size(); i++) {
            if (bean.getNpc(i) == null) {
                throw exception("挑战全明星 %s. 难度 %s 的npc没有配置", id, i);
            }
        }
        int ranklower = 1;
        for (AwardBean ab : bean.getAwards()) {
            IntervalInt r = ab.getRank();
            if (r.getLower() != ranklower) {
                throw exception("挑战全明星 %s. 排名 %s - %s 和上一个排名奖励之间有空隙", id, r.getLower(), r.getUpper());
            }
            ranklower = r.getUpper() + 1;
            PropConsole.validate(ab.getProps(), "挑战全明星 %s. 排名 %s - %s 奖励, ", id, r.getLower(), r.getUpper());
            DropConsole.validate(ab.getDrop(), "挑战全明星 %s. 排名 %s - %s 奖励, ", id, r.getLower(), r.getUpper());
        }
    }

    public static AllStarBean getBean(int allStarRid) {
        return allStars.get(allStarRid);
    }

    public static Map<Integer, AllStarBean> getAllStars() {
        return allStars;
    }
    
    public static List<PropSimple> getScorePersonalAward(int score){
      for(ScorePersonalAward s : scorePersonalAwards) {
        if(score == s.getScore()) {
          return s.getRewards();
        }
      }
      return null;
    }
    
    public static List<ScorePersonalAward> getScorePersonalAward(){
      return scorePersonalAwards;
    }
}
