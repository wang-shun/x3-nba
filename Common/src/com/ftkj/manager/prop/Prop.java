package com.ftkj.manager.prop;

import com.ftkj.db.domain.PropPO;
import com.ftkj.util.lambda.LBInt;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Prop {

    private int tid;
    private Map<Integer, PropPO> props;

    public Prop(int tid) {
        this.tid = tid;
        this.props = Maps.newHashMap();
    }

    public int getNum() {
        return props.values().stream().mapToInt(prop -> prop.getNum()).sum();
    }

    /**
     * 取这个道具的ID，如果没有，则新建道具
     *
     * @param pid
     * @return
     */
    public boolean checkHas(int pid) {
        if (this.props.containsKey(pid)) {
            return true;
        }
        return false;
    }

    public List<PropPO> delProp(int num) {
        List<Integer> removeList = Lists.newArrayList();
        List<PropPO> resultPO = Lists.newArrayList();
        LBInt i = new LBInt(num);
        //
        props.values().stream().sorted((a, b) -> a.getEndTime().isAfter(b.getEndTime()) ? -1 : 1)
            .forEach(prop -> {
                if (i.getVal() > 0) {
                    int cnum = prop.getNum() > i.getVal() ? i.getVal() : prop.getNum();
                    prop.setNum(prop.getNum() - cnum);
                    if (prop.getNum() <= 0) {
                        removeList.add(prop.getId());
                        prop.del();
                    }
                    i.sum(-cnum);
                    prop.save();
                    resultPO.add(prop);
                }
            });
        //
        removeList.forEach(id -> props.remove(id));
        return resultPO;
    }

    PropPO addProp(PropPO po) {
        this.props.put(po.getPropId(), po);
        po.save();
        return po;
    }

    void initProp(DateTime now, PropPO po) {
        if (po.getEndTime().isBeforeNow()) {
            return;
        }
        addProp(po);
    }

    public int getTid() {
        return tid;
    }

//    PropPO addProp(long teamId, int id, PropSimple ps) {
//        return addProp(teamId, id, ps, ps.getHour() != 0 ? DateTime.now().plusHours(ps.getHour()) : GameConsole.Max_Date);
//    }

    PropPO addProp(long teamId, int id, PropSimple ps, DateTime endTime) {
        PropPO po = new PropPO();
        po.setConfig(ps.getConfig());
        po.setCreateTime(DateTime.now());
        po.setEndTime(endTime);
        po.setId(id);
        po.setNum(ps.getNum());
        po.setPropId(ps.getPropId());
        po.setTeamId(teamId);
        return addProp(po);
    }

    /**
     * 更新道具
     *
     * @param ps
     * @return
     */
    PropPO updateProp(PropSimple ps) {
        PropPO po = this.props.get(ps.getPropId());
        po.setNum(po.getNum() + ps.getNum());
        po.save();
        return po;
    }

    public Collection<PropPO> getPropList() {
        return props.values();
    }

}