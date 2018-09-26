package com.ftkj.manager.prop;

import com.ftkj.console.PropConsole;
import com.ftkj.db.domain.PropPO;
import com.ftkj.manager.prop.bean.PropBean;

import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author tim.huang
 * 2017年2月16日
 * 玩家道具
 */
public class TeamProp {
    private static final Logger log = LoggerFactory.getLogger(TeamProp.class);
    // tid : prop
    private Map<Integer, Prop> propMap;
    private AtomicInteger id;

    public static TeamProp Empty = new TeamProp();

    private TeamProp() {
    }

    public TeamProp(List<PropPO> propList) {
        this.propMap = Maps.newHashMap();
        propList.stream().map(prop -> {
            PropBean pb = PropConsole.getProp(prop.getPropId());
            if (pb == null) {
                return -1;
            }
            return pb.getTid();
        }).distinct().filter(tid -> tid != -1)
            .forEach(tid -> propMap.put(tid, new Prop(tid)));
        int initId = propList.stream().mapToInt(prop -> prop.getId()).max().orElse(0);
        this.id = new AtomicInteger(initId);
        DateTime now = DateTime.now();
        propList.stream()
            .forEach(prop -> {
                PropBean pb = PropConsole.getProp(prop.getPropId());
                if (pb == null) {
                    log.error("玩家包含不存在的道具{},{}", prop.getTeamId(), prop.getPropId());
                    return;
                }
                propMap.computeIfAbsent(pb.getTid(), (key) -> new Prop(key)).initProp(now, prop);
            });
    }

    /**
     * 检查道具数量是否足够
     * 注意，del方法已经有做验证，所以无需重复验证
     * 除非有单独数量验证需求
     *
     * @param tid
     * @param num
     * @return
     */
    public boolean checkPropNum(int tid, int num) {
        Prop prop = getProp(tid);
        return checkPropNum(prop, num);
    }

    /**
     * 检查道具数量是否足够
     *
     * @param PropSimple
     * @return false 不够
     */
    public boolean checkPropNum(PropSimple ps) {
        return checkPropNum(ps.getPropId(), ps.getNum());
    }

    /**
     * 检查道具数量是否足够
     *
     * @param PropSimple
     * @return false不够
     */
    public boolean checkPropListNum(List<PropSimple> ps) {
        return ps.stream().allMatch(p -> checkPropNum(p));
    }

    /**
     * 检查道具是否能使用
     *
     * @param tid
     * @return
     */
    public boolean checkCanUse(int tid) {
        PropBean pb = PropConsole.getProp(tid);
        if (pb == null || pb.getUse() == 0) {
            return false;
        }
        return true;
    }

    //	/**
    //	 * 道具增加去PropManager中调用
    //	 * @param teamId
    //	 * @param ps
    //	 */
    //	public PropPO addProp(long teamId,PropSimple ps){
    //		PropBean pb = PropConsole.getProp(ps.getPropId());
    //		Prop prop = getProp(pb.getTid());
    //		if(prop.checkHas(ps.getPropId())) {
    //			return prop.updateProp(ps);
    //		}else {
    //			return prop.addProp(teamId, this.id.incrementAndGet(), ps);
    //		}
    //	}

    /**
     * 道具增加去PropManager中调用
     *
     * @param teamId
     * @param ps
     * @param endTime
     */
    public PropPO addProp(long teamId, PropSimple ps, DateTime endTime) {
        PropBean pb = PropConsole.getProp(ps.getPropId());
        Prop prop = getProp(pb.getTid());
        if (prop.checkHas(ps.getPropId())) {
            return prop.updateProp(ps);
        } else {
            return prop.addProp(teamId, this.id.incrementAndGet(), ps, endTime);
        }
    }

    /**
     * 道具移除去PropManager中调用
     *
     * @param tid
     * @param num
     * @return
     */
    public List<PropPO> delProp(int tid, int num) {
        Prop prop = getProp(tid);
        if (!checkPropNum(prop, num)) {
            return null;
        }
        //
        return prop.delProp(num);
    }

    /**
     * 道具移除去PropManager中调用
     *
     * @param PropSimple
     * @return
     */
    public List<PropPO> delProp(PropSimple ps) {
        PropBean pb = PropConsole.getProp(ps.getPropId());
        return delProp(pb.getTid(), ps.getNum());
    }

    /**
     * 道具移除去PropManager中调用
     *
     * @param tid
     * @param num
     * @return
     */
    public Prop delPropAndGet(int tid, int num) {
        Prop prop = getProp(tid);
        if (!checkPropNum(prop, num)) {
            return null;
        }
        //
        prop.delProp(num);
        return prop;
    }

    private boolean checkPropNum(Prop prop, int num) {
        int total = prop.getNum();
        if (total < num) {
            return false;//道具数量不足
        }
        return true;
    }

    public Prop getProp(int tid) {
        Prop prop = this.propMap.get(tid);
        if (prop == null) {
            prop = new Prop(tid);
            this.propMap.put(tid, prop);
        }
        return prop;
    }

    public Prop getPropByPropId(int pid) {
        PropBean bean = PropConsole.getProp(pid);
        int tid = bean.getTid();
        Prop prop = this.propMap.get(tid);
        if (prop == null) {
            prop = new Prop(tid);
            this.propMap.put(tid, prop);
        }
        return prop;
    }

    public Collection<Prop> getPropList() {
        return propMap.values();
    }

    /** 获取道具数量 */
    public int getPropNum(int tid) {
        Prop prop = this.getProp(tid);
        return prop.getNum();
    }

}
