package com.ftkj.manager.system.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.RandomUtil;

/**
 * @author tim.huang
 * 2016年6月27日
 * 唯一掉落，多选多
 */
public class DropOnlyBean extends DropBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DropOnlyBean(int dropId) {
		super(dropId);
	}

	@Override
	public List<PropSimple> roll() {
		List<PropSimple> resultList = new ArrayList<PropSimple>();
		if(super.getDropList().size()<=0) {
			return resultList;
		}
		int ran = RandomUtil.randInt(super.getTotalProbability());
		int start = 0;
		DropProp dp = null;
		for(int i = 0 ; i < super.getDropList().size();i++){
			dp = super.getDropList().get(i);
			start += dp.getProbability();
			if(ran<start){
				resultList.add(new PropSimple(dp.getProp().getPropId(),RandomUtil.randInt(dp.getMinNum(),dp.getMaxNum()),dp.getProp().getHour()));
				return resultList;
			}
		}
		//避免上面概率出现问题
		dp = super.getDropList().get(0);
		if(dp != null){
			resultList.add(new PropSimple(dp.getProp().getPropId(),RandomUtil.randInt(dp.getMinNum(),dp.getMaxNum()),dp.getProp().getHour()));
		}
		return resultList;
	}

	@Override
	public List<PropSimple> roll(int args) {
		List<PropSimple> resultList = new ArrayList<PropSimple>();
		if(args>=super.getDropList().size()) {
			return resultList;
		}
		Set<Integer> tempResult = new HashSet<Integer>();
		PropSimple tempProp = null;
		for(int i =0; i < args ; ){
			tempProp = roll().get(0);
			if(tempResult.contains(tempProp.getPropId())) {
				continue;
			}
			tempResult.add(tempProp.getPropId());
			resultList.add(tempProp);
			i++;
		}
		return resultList;
	}

	@Override
	public List<PropSimple> roll(String args) {
		return null;
	}

	@Override
	public List<PropSimple> roll(List<PropSimple> filterIds) {
		return null;
	}

}
