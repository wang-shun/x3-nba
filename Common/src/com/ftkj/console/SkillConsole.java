package com.ftkj.console;

import com.ftkj.cfg.SkillLevelBean;
import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.manager.skill.buff.ClearSkillBuffer;
import com.ftkj.manager.skill.buff.MoraleSkillBuffer;
import com.ftkj.manager.skill.buff.PlayerSkillBuffer;
import com.ftkj.manager.skill.buff.PowerSkillBuffer;
import com.ftkj.manager.skill.buff.SkillBuffer;
import com.ftkj.manager.skill.buff.SkillBufferVO;
import com.ftkj.manager.skill.buff.StopSkillBuffer;
import com.ftkj.manager.skill.buff.TacticsSkillBuffer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillConsole extends AbstractConsole {

    private static Map<Integer, SkillBean> skillBeanMap;
    private static Map<Integer, SkillBuffer> skillBufferMap;
    private static Map<EPlayerPosition, List<SkillPositionBean>> skillPositionMap;
    private static Map<Integer, List<SkillPositionBean>> skillPlayerPositionMap;
    private static Map<Integer, List<SkillLevelBean>> skillLevelMap;

    public static void init() {
        skillBeanMap = CM.skillBeanList.stream()
                .collect(toImmutableMap(b -> b.getSkillId(), b -> b));
        skillBufferMap = CM.skillBuffers.stream()
                .map(buff -> instanceSkillBuffer(buff))
                .filter(buff -> buff != null)
                .collect(toImmutableMap(b -> b.getBid(), b -> b));
        skillPositionMap = CM.skillPositionList.stream()
                .peek(skill -> skill.initSkill())
                .collect(Collectors.groupingBy(SkillPositionBean::getSkillPosition));
        skillPlayerPositionMap = CM.skillPlayerPositionList.stream()
                .peek(skill -> skill.initSkill())
                .collect(Collectors.groupingBy(SkillPositionBean::getPlayerId));
        skillLevelMap = CM.skillLevelList.stream()
                .collect(Collectors.groupingBy(SkillLevelBean::getStep));
    }

    public static SkillPositionBean getSkillPositionBean(EPlayerPosition position, int step, int playerId) {
        SkillPositionBean result = null;
        List<SkillPositionBean> spList = null;
        if (step <= 4) {
            spList = skillPositionMap.get(position);
        } else {
            spList = skillPlayerPositionMap.get(playerId);
        }
        if (spList != null) {
            result = spList.stream().filter(sp -> sp.getStep() == step).findFirst().orElse(null);
        }
        return result;
    }

    public static boolean checkPlayerSkillPositionBean(int playerId) {
        return skillPlayerPositionMap.containsKey(playerId);
    }

    public static SkillLevelBean getSkillLevelBean(int step, int level) {
        List<SkillLevelBean> slList = skillLevelMap.get(step);
        SkillLevelBean result = null;
        if (slList != null) {
            result = slList.stream().filter(sp -> sp.getLevel() == level).findFirst().orElse(null);
        }
        return result;
    }

    private static SkillBuffer instanceSkillBuffer(SkillBufferVO vo) {
        SkillBuffer buffer = null;
        if (vo.getType() == 1) {
            buffer = new PowerSkillBuffer(vo);
        } else if (vo.getType() == 2) {
            buffer = new MoraleSkillBuffer(vo);
        } else if (vo.getType() == 3) {
            buffer = new TacticsSkillBuffer(vo);
        } else if (vo.getType() == 4) {
            buffer = new PlayerSkillBuffer(vo);
        } else if (vo.getType() == 5) {
            buffer = new StopSkillBuffer(vo);
        } else if (vo.getType() == 6) {
            buffer = new ClearSkillBuffer(vo);
        }
        return buffer;
    }

    public static SkillBuffer getSkillBuffer(int bid) {
        return skillBufferMap.get(bid);

    }

    public static SkillBean getSkillBean(int skillId) {
        return skillBeanMap.get(skillId);
    }

    public static SkillBean getRanSkillBean(boolean attack) {
        List<SkillBean> list = skillBeanMap.values().stream().filter(skill -> skill.attack() == attack).collect(Collectors.toList());
        Collections.shuffle(list);
        return list.get(0);
    }

    /**
     * 取装备技能的等级
     *
     * @param skill
     * @param player
     * @return
     */
    @SuppressWarnings("unused")
    public static int[] getPlayerSkillLv(PlayerSkill skill, Player player) {
        //		int attackStep=0,defendStep=0;
        int attackLevel = 0, defendLevel = 0;
        boolean attackType = false, defendType = false;
        //		SkillBean attackSkillBean = null;
        //        SkillBean defendSkillBean =null;
        if (skill != null) {
            List<SkillPositionBean> stepList = skill.getSkillTree().stream()
                    .map(sk -> SkillConsole.getSkillPositionBean(player.getPlayerPosition(), sk.getStep(), player.getPlayerRid()))
                    .filter(sk -> sk != null)
                    .collect(Collectors.toList());
            SkillPositionBean attackBean = stepList.stream().filter(sk -> sk.hasSkill(skill.getAttack())).findFirst().orElse(null);
            SkillPositionBean defendBean = stepList.stream().filter(sk -> sk.hasSkill(skill.getDefend())).findFirst().orElse(null);

            if (attackBean != null) {
                //				attackStep = attackBean.getStep();
                if (attackBean.getS1() == skill.getAttack()) {
                    attackLevel = skill.getSkillTree(attackBean.getStep()).getS1();
                    //					attackSkillBean = attackBean.getSkill1();
                } else if (attackBean.getS2() == skill.getAttack()) {
                    attackLevel = skill.getSkillTree(attackBean.getStep()).getS2();
                    //					attackSkillBean = attackBean.getSkill2();
                } else if (attackBean.getS3() == skill.getAttack()) {
                    attackLevel = skill.getSkillTree(attackBean.getStep()).getS3();
                    //					attackType = true;
                    //					attackSkillBean = attackBean.getSkill3();
                } else if (attackBean.getS4() == skill.getAttack()) {
                    attackLevel = skill.getSkillTree(attackBean.getStep()).getS4();
                    //					attackSkillBean = attackBean.getSkill4();
                    //					attackType = true;
                }
            }

            if (defendBean != null) {
                //				defendStep = defendBean.getStep();
                if (attackBean.getS1() == skill.getDefend()) {
                    defendLevel = skill.getSkillTree(defendBean.getStep()).getS1();

                    //					defendSkillBean = defendBean.getSkill1();
                } else if (attackBean.getS2() == skill.getDefend()) {
                    defendLevel = skill.getSkillTree(defendBean.getStep()).getS2();
                    //					defendSkillBean = defendBean.getSkill2();
                } else if (attackBean.getS3() == skill.getDefend()) {
                    //					defendSkillBean = defendBean.getSkill3();
                    defendLevel = skill.getSkillTree(defendBean.getStep()).getS3();
                    //					defendType = true;
                } else if (attackBean.getS4() == skill.getDefend()) {
                    defendLevel = skill.getSkillTree(defendBean.getStep()).getS4();
                    //					defendSkillBean = defendBean.getSkill4();
                    //					defendType = true;
                }
            }
        }
        return new int[]{attackLevel, defendLevel};
    }

}
