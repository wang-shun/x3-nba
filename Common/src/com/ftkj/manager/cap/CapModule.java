package com.ftkj.manager.cap;

import com.ftkj.enums.AbilityType;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.ECapModule;
import com.ftkj.manager.ablity.PlayerAbility;
import com.ftkj.manager.ablity.TeamAbility;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public abstract class CapModule {
    /**
     * 保留小数位数 100 = 2位
     */
    private static final float KEEP_RATE = 10000f;

    /**
     * 节点配置
     * 根节点的总攻防等于所有子节点的和
     */
    protected CapNode rootNode;
    protected int playerId;
    /**
     * 节点攻防
     */
    public Map<ECapModule, Map<EActionType, Float>> capMap;

    protected AbilityType abilityType;

    public CapModule(AbilityType abilityType) {
        this.abilityType = abilityType;
        initConfig();
        initNodeCap();
    }

    /**
     * 初始化配置
     * 子类各自实现
     */
    public abstract void initConfig();

    private void initNodeCap() {
        // 初始化节点
        this.capMap = Maps.newHashMap();
        capMap.put(rootNode.getType(), Maps.newHashMap());
        // 遍历节点配置，初始化map
        if (rootNode.getChildList() == null) {
            return;
        }
        for (CapNode node : rootNode.getChildList()) {
            capMap.put(node.getType(), Maps.newHashMap());
        }
    }

    /**
     * 从根节点开始，递归取所有子节点集合
     */
    public List<ECapModule> getAllCapType(CapNode node) {
        List<ECapModule> types = Lists.newArrayList();
        types.add(node.getType());
        for (CapNode n : node.getChildList()) {
            types.addAll(getAllCapType(n));
        }
        return types;
    }

    /**
     * 模块攻防初始化，请在实现类构造中调用
     */
    public void initCap() {
        // 从根节点取所有
        List<ECapModule> types = getAllCapType(this.rootNode);
        // 攻防计算公式
        for (ECapModule t : types) {
            initCapValue(t, analysis(t));
        }
    }

    private void initCapValue(ECapModule module, float[] values) {
        float ocap = Math.round(values[0] * KEEP_RATE) / KEEP_RATE;
        float dcap = Math.round(values[1] * KEEP_RATE) / KEEP_RATE;
        this.addModuleActionType(module, EActionType.ocap, ocap);
        this.addModuleActionType(module, EActionType.dcap, dcap);
        afterInitCap(module, ocap, dcap);
    }

    protected void afterInitCap(ECapModule module, float ocap, float dcap) {
    }

    /**
     * 攻防计算公式，子类各自实现
     */
    public abstract float[] analysis(ECapModule module);

    /**
     * 添加模块攻防值
     */
    public void addModuleActionType(ECapModule module, EActionType type, Float value) {
        if (!this.capMap.containsKey(module)) { return; }
        Map<EActionType, Float> map = this.capMap.get(module);
        if (!map.containsKey(type)) {
            map.put(type, value);
        } else {
            map.put(type, map.get(type) + value);
        }
    }

    /**
     * 取球员的攻防
     */
    public PlayerAbility getAbility() {
        PlayerAbility p = new PlayerAbility(this.abilityType, this.playerId);
        for (Map<EActionType, Float> map : this.capMap.values()) {
            for (EActionType type : map.keySet()) {
                p.addAttr(type, map.get(type));
            }
        }
        return p;
    }

    public TeamAbility getTeamAbility() {
        TeamAbility p = new TeamAbility(this.abilityType);
        for (Map<EActionType, Float> map : this.capMap.values()) {
            for (EActionType type : map.keySet()) {
                p.addAttr(type, map.get(type));
            }
        }
        return p;
    }

    public Map<ECapModule, Map<EActionType, Float>> getCapMap() {
        return capMap;
    }

    /**
     * 进攻计算
     */
    protected Float getJg(PlayerBean pb, Map<EActionType, Float> capMap, PlayerTalent pt) {
        float jg = 0;
        if (pb == null || capMap == null || pt == null) {
            return jg;
        }
        float tlmz = pb.getAbility(EActionType.fgm) * (1 + pt.getAbility(EActionType._2pm) / 10000);
        float fqmz = pb.getAbility(EActionType.ftm) * (1 + pt.getAbility(EActionType.ftm) / 10000);
        float df = pb.getAbility(EActionType.pts) * (1 + pt.getAbility(EActionType.pts) / 10000);
        float sfmx = pb.getAbility(EActionType._3pm) * (1 + pt.getAbility(EActionType._3pm) / 10000);
        float lb = pb.getAbility(EActionType.oreb) * (1 + pt.getAbility(EActionType.reb) / 10000);
        float zg = pb.getAbility(EActionType.ast) * (1 + pt.getAbility(EActionType.ast) / 10000);
        //
        jg += tlmz * 0.3f * capMap.getOrDefault(EActionType.fgm, 0f);
        jg += fqmz * 0.2f * capMap.getOrDefault(EActionType.ftm, 0f);
        jg += df * 1.5f * capMap.getOrDefault(EActionType.pts, 0f);
        jg += sfmx * 0.25f * capMap.getOrDefault(EActionType._3pm, 0f);
        jg += lb * 0.4f * capMap.getOrDefault(EActionType.reb, 0f);
        jg += zg * 0.6f * capMap.getOrDefault(EActionType.ast, 0f);
        jg -= pb.getAbility(EActionType.to) * 0.7f * capMap.getOrDefault(EActionType.to, 0f);
        jg += pb.getAbility(EActionType.min) * 0f * capMap.getOrDefault(EActionType.min, 0f);
        jg -= pb.getAbility(EActionType.pf) * 0.2f * capMap.getOrDefault(EActionType.pf, 0f);

        return jg;
    }

    /**
     * 防守计算
     */
    protected Float getFs(PlayerBean pb, Map<EActionType, Float> capMap, PlayerTalent pt) {
        float fs = 0;
        //
        float qd = pb.getAbility(EActionType.stl) * (1 + pt.getAbility(EActionType.stl) / 10000);
        float gm = pb.getAbility(EActionType.blk) * (1 + pt.getAbility(EActionType.blk) / 10000);
        float dreb = pb.getAbility(EActionType.dreb) * (1 + pt.getAbility(EActionType.reb) / 10000);
        //
        fs += qd * 0.8f * capMap.getOrDefault(EActionType.stl, 0f);
        fs += gm * 0.6f * capMap.getOrDefault(EActionType.blk, 0f);
        fs += dreb * 0.3f * capMap.getOrDefault(EActionType.reb, 0f);
        fs -= pb.getAbility(EActionType.to) * 0f * capMap.getOrDefault(EActionType.to, 0f);
        fs += pb.getAbility(EActionType.min) * 0.2f * capMap.getOrDefault(EActionType.min, 0f);
        fs -= pb.getAbility(EActionType.pf) * 0f * capMap.getOrDefault(EActionType.pf, 0f);

        return fs;
    }

    @Override
    public String toString() {
        return "CapModule [capMap=" + capMap + "]";
    }
}
