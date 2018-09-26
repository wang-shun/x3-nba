package com.ftkj.manager.system.bean;

import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.domain.bean.DropBeanVO;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.EPropType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropAutoBoxBean;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropPlayerBean;
import com.ftkj.manager.prop.bean.PropPlayerGradeBean;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2016年2月24日
 * 物品掉落
 */
public abstract class DropBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<DropProp> dropList;
    private int totalProbability;
    private int dropId;

    private static final int SIMPLE = 1;
    private static final int ALL = 2;
    private static final int ONLY = 3;
    private static final int ADD = 4;

    public DropBean(int dropId) {
        super();
        this.dropId = dropId;
        this.totalProbability = 0;
        this.dropList = new ArrayList<>();
    }

    protected List<DropProp> getDropList() {
        return dropList;
    }

    protected int getTotalProbability() {
        return totalProbability;
    }

    public static DropBean newDrop(int dropId, int type) {
        if (type == SIMPLE) {
            return new DropSimpleBean(dropId);
        } else if (type == ALL) {
            return new DropAllBean(dropId);
        } else if (type == ONLY) {
            return new DropOnlyBean(dropId);
        } else if (type == ADD) {
            return new DropAddBean(dropId);
        } else { throw new IllegalAccessError(); }
    }

    /**
     * 随机获得物品
     *
     * @return
     */
    public abstract List<PropSimple> roll();

    public abstract List<PropSimple> roll(int args);

    public abstract List<PropSimple> roll(String args);
    
    public abstract List<PropSimple> roll(List<PropSimple> filterIds);

    public DropBean appendDropProp(DropBeanVO drop, PropBean prop) {
        DropProp dp = new DropProp(prop, drop.getProbaility(), drop.getMin(), drop.getMax(), drop.getRemark());
        this.dropList.add(dp);
        this.totalProbability += drop.getProbaility();
        return this;
    }

    public DropBean appendProp(String props) {
        this.totalProbability = Arrays.stream(props.split(","))
            .map(ps -> new DropProp(PropConsole.getProp(Integer.parseInt(ps.split(StringUtil.DEFAULT_FH)[0])), 100,
                Integer.parseInt(ps.split(StringUtil.DEFAULT_FH)[1]), Integer.parseInt(ps.split(StringUtil.DEFAULT_FH)[1]), ""))
            .peek(dp -> this.dropList.add(dp))
            .map(dp -> dp.getProbability())
            .reduce(0, (c, m) -> c + m);
        return this;
    }

    /**
     * 打开礼包。判断是否自动打开礼包，并且递归打开礼包中的多概率事件
     *
     * @param prop
     * @return
     */
    public static List<PropSimple> openBox(PropBean prop, PropSimple pp) {
        return openBoxFilter(prop, pp, null);
    }
    
    /**
     * 球员过滤一个掉落里面的多个嵌套重复问题.
     */
    public static List<PropSimple> openBoxFilter(PropBean prop, PropSimple pp, List<PropSimple> filterList) {
    	List<PropSimple> result = new ArrayList<PropSimple>();
        if (prop.getType() == EPropType.Package_Random) {
            PropAutoBoxBean pb = (PropAutoBoxBean) prop;
            for (PropSimple ps : pb.getDrop().roll(filterList)) {
                result.addAll(openBoxFilter(PropConsole.getProp(ps.getPropId()), ps, filterList));
            }
        } else if (prop.getType() == EPropType.PlayerGrade) {//随机级别球员
            PropPlayerGradeBean pb = (PropPlayerGradeBean) prop;
            // repeated:false多开球员去重机制
            List<Integer> filterIds = filterList == null ? Lists.newArrayList() 
            		: filterList.stream().map(p->(PropPlayerBean) (PropConsole.getProp(p.getPropId()))).mapToInt(p-> p.getHeroId())
            		.boxed().collect(Collectors.toList());
            List<Integer> playerIds = PlayerConsole.getRanPlayerIds(pb.getMinGrade(), pb.getMaxGrade(), 
            		EPlayerPosition.NULL, pb.getDraft(), pp.getNum(), filterIds);
            result.addAll(playerIds.stream().map(playerId-> new PropSimple(PropPlayerBean.convertHeroId(playerId),
            		PlayerConsole.getPlayerBean(playerId).getPrice())).collect(Collectors.toList()));
        } else {
            if (pp.getPropId() != 0) {
                result.add(pp);
            }
        }
        return result;
    }

    /**
     * 打开礼包。判断是否自动打开礼包，并且递归打开礼包中的多概率事件
     *
     * @param prop
     * @return
     */
    public static List<PropSimple> openBox(List<PropSimple> list) {
        List<PropSimple> result = list.stream()
            .map(ps -> openBox(PropConsole.getProp(ps.getPropId()), ps))
            .reduce(new ArrayList<PropSimple>(), (x, v) -> {x.addAll(v); return x;});
        return result;
    }

    public int getDropId() {
        return dropId;
    }

    static class DropProp implements Serializable {
        private static final long serialVersionUID = 1L;
        private PropBean prop;
        private int probability;
        private int minNum;
        private int maxNum;
        private String remark;

        public DropProp(PropBean prop, int probability, int minNum, int maxNum, String remark) {
            this.prop = prop;
            this.probability = probability;
            this.minNum = minNum;
            this.maxNum = maxNum;
            this.remark = remark;
        }

        public int getMinNum() {
            return minNum;
        }

        public int getMaxNum() {
            return maxNum;
        }

        public PropBean getProp() {
            return prop;
        }

        public int getProbability() {
            return probability;
        }

        public String getRemark() {
            return remark;
        }
    }
}
