package com.ftkj.manager.system.bean;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropPlayerGradeBean;
import com.ftkj.util.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author tim.huang
 * 2016年2月25日
 * 普通掉落,多选一
 */
public class DropSimpleBean extends DropBean {
    private static final long serialVersionUID = 1L;

    public DropSimpleBean(int dropId) {
        super(dropId);
    }

    @Override
    public List<PropSimple> roll() {
        List<PropSimple> resultList = new ArrayList<>();
        if (super.getDropList().size() <= 0) {
            return resultList;
        }
        int ran = RandomUtil.randInt(super.getTotalProbability());
        int start = 0;
        for (int i = 0; i < super.getDropList().size(); i++) {
            DropProp dp = super.getDropList().get(i);
            start += dp.getProbability();
            if (ran < start) {
                if (dp.getProp().getPropId() != 0) {
//                	PropPlayerGradeBean pb = (PropPlayerGradeBean) dp.getProp();
//                	System.err.println("命中:" + dp.getProp().getPropId() + "  ran=" + ran + "  pb=" + pb);
                    resultList.addAll(openBox(dp.getProp(), new PropSimple(dp.getProp().getPropId(), RandomUtil.randInt(dp.getMinNum(), dp.getMaxNum()), dp.getProp().getHour())));
                }
                return resultList;
            }
        }
        DropProp dp = super.getDropList().get(0);
        if (dp != null) {
            if (dp.getProp().getPropId() != 0) {
                resultList.add(new PropSimple(dp.getProp().getPropId(), RandomUtil.randInt(dp.getMinNum(), dp.getMaxNum()), dp.getProp().getHour()));
            }
        }
        return resultList;
    }

    @Override
    public List<PropSimple> roll(int args) {
        return null;
    }

    @Override
    public List<PropSimple> roll(String args) {
        return roll();
    }

	@Override
	public List<PropSimple> roll(List<PropSimple> filterIds) {
		List<PropSimple> resultList = new ArrayList<>();
        if (super.getDropList().size() <= 0) {
            return resultList;
        }
        int ran = RandomUtil.randInt(super.getTotalProbability());
        int start = 0;
        for (int i = 0; i < super.getDropList().size(); i++) {
            DropProp dp = super.getDropList().get(i);
            start += dp.getProbability();
            if (ran < start) {
                if (dp.getProp().getPropId() != 0) {
                	PropSimple ps = new PropSimple(dp.getProp().getPropId(), RandomUtil.randInt(dp.getMinNum(), dp.getMaxNum()), dp.getProp().getHour());
                    resultList.addAll(openBoxFilter(dp.getProp(), ps, filterIds));
                }
                return resultList;
            }
        }
        DropProp dp = super.getDropList().get(0);
        if (dp != null) {
            if (dp.getProp().getPropId() != 0) {
                resultList.add(new PropSimple(dp.getProp().getPropId(), RandomUtil.randInt(dp.getMinNum(), dp.getMaxNum()), dp.getProp().getHour()));
            }
        }
        return resultList;
	}

}
