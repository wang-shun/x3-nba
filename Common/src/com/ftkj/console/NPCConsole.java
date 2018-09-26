package com.ftkj.console;

import com.ftkj.cfg.NpcMirrorBean;
import com.ftkj.cfg.base.ValidateBean;
import com.ftkj.db.domain.bean.NPCBeanVO;
import com.ftkj.manager.npc.NPCBean;
import com.ftkj.util.RandomUtil;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年3月27日
 * npc 初始化
 */
public class NPCConsole extends AbstractConsole implements ValidateBean {
    private static Map<Long, NPCBean> npcs;

    public static void init() {
        Map<Long, NPCBean> temp = Maps.newHashMap();
        Map<Long, NPCBeanVO> vos = toMap(CM.NPC, b -> b.getNpcId(), b -> b);
        CM.NPC.forEach(npc -> temp.put(npc.getNpcId(), new NPCBean(npc)));

        for (NpcMirrorBean.Builder b : CM.NPC_Mirrors) {
            NpcMirrorBean mb = b.build();
            NPCBeanVO vo = vos.get(mb.getMirrorNpcId());
            if (temp.containsKey(mb.getNpcId())) {
                throw exception("npc配置. 镜像npc %s 和基础npc id冲突", mb.getNpcId());
            }
            temp.put(mb.getNpcId(), new NPCBean(vo, mb.getNpcId(), mb.getName()));
        }

        NPCConsole.npcs = temp;
    }

    public static NPCBean getNPC(long npcId) {
        return npcs.get(npcId);
    }

    public static Map<Long, NPCBean> getNpcs() {
        return npcs;
    }

    /**
     * 按顺序取NPC
     *
     * @param size      大小
     * @param filterIds 过滤指定
     * @param minId     起始ID
     * @param maxId     结束ID
     * @return
     */
    public static List<Long> getSeqNpcId(int size, List<Long> filterIds, int minId, int maxId) {
        return npcs.values().stream()
                .filter(p -> !filterIds.contains(p.getNpcId()))
                .filter(p -> p.getNpcId() >= minId && p.getNpcId() <= maxId)
                .limit(size)
                .mapToLong(npc -> npc.getNpcId())
                .boxed()
                .collect(Collectors.toList());
    }

    /**
     * 取随机NPC
     *
     * @param size      大小
     * @param filterIds 过滤指定
     * @param minId     起始ID
     * @param maxId     结束ID
     * @return
     */
    public static List<NPCBean> getRoundNpcId(int size, List<Long> filterIds, int minId, int maxId) {
        List<NPCBean> selectList = npcs.values().stream().filter(p -> !filterIds.contains(p.getNpcId()))
                .filter(p -> p.getNpcId() >= minId && p.getNpcId() <= maxId).collect(Collectors.toList());
        return RandomUtil.randList(selectList, size, false);
    }

    /**
     * 判断是否是NPC
     *
     * @param npcId
     * @return
     */
    public static boolean isNPC(long npcId) {
        return npcs.containsKey(npcId);
    }

    @Override
    public void validate() {
        for (NPCBean b : npcs.values()) {
            validate(b);
        }
    }

    private void validate(NPCBean b) {
        long id = b.getNpcId();
        if (b.getAiGroupId() > 0 && AIConsole.getBean(b.getAiGroupId()) == null) {
            throw exception("npc配置. npc %s 的AI规则分组 %s 没有配置", id, b.getAiGroupId());
        }
    }

    public static void validate(Collection<Long> npcIds, String msg, Object... msgArgs) {
        for (Long npcId : npcIds) {
            validate(npcId, msg, msgArgs);
        }
    }

    public static void validate(long npcId, String msg, Object... msgArgs) {
        if (getNPC(npcId) == null) {
            String preMsg = String.format(msg, msgArgs);
            throw exception(String.format(preMsg + "npc %s 没有配置", npcId));
        }
    }
}
