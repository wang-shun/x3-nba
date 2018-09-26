package com.ftkj.cfg;

import com.ftkj.cfg.base.AbstractExcelBean;
import com.ftkj.util.excel.RowData;

/** 镜像npc配置 */
public class NpcMirrorBean {
    /** 玩家ID必须小于100000 */
    private final long npcId;
    /** 要镜像的npcId */
    private final long mirrorNpcId;
    /** 玩家名称(覆盖 GameNPC 表中的名称) */
    private final String name;

    private NpcMirrorBean(long npcId, long mirrorNpcId, String name) {
        this.npcId = npcId;
        this.mirrorNpcId = mirrorNpcId;
        this.name = name;
    }

    public long getNpcId() {
        return npcId;
    }

    public long getMirrorNpcId() {
        return mirrorNpcId;
    }

    public String getName() {
        return name;
    }

    public static final class Builder extends AbstractExcelBean {
        /** 玩家ID必须小于100000 */
        private long npcId;
        /** 要镜像的npcId */
        private long mirrorNpcId;
        /** 玩家名称(覆盖 GameNPC 表中的名称) */
        private String npcName;

        @Override
        public void initExec(RowData row) {

        }

        public NpcMirrorBean build() {
            if (npcId == mirrorNpcId) {
                throw new RuntimeException("npc配置. 不能镜像自己 " + npcId);
            }
            return new NpcMirrorBean(npcId, mirrorNpcId, npcName);
        }
    }
}
