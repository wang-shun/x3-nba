package com.ftkj.manager.system.bean;

import java.util.ArrayList;
import java.util.List;

import com.ftkj.enums.EPropType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.RandomUtil;

/**
 * @author tim.huang
 * 2016年2月25日
 * 掉落所有
 */
public class DropAllBean extends DropBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DropAllBean(int dropId) {
		super(dropId);
	}

	@Override
	public List<PropSimple> roll() {
		List<PropSimple> resultList = new ArrayList<PropSimple>();
		PropSimple ps = null;
		for(DropProp dp : super.getDropList()){
			ps = new PropSimple(dp.getProp().getPropId(),RandomUtil.randInt(dp.getMinNum(),dp.getMaxNum()),dp.getProp().getHour());
			if(dp.getProp().getConfig().indexOf("repeated:false") != -1) {
				resultList.addAll(openBoxFilter(dp.getProp(), ps, resultList));
			}else {
				resultList.addAll(openBox(dp.getProp(),ps));
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
		return null;
	}

	@Override
	public List<PropSimple> roll(List<PropSimple> filterIds) {
		return null;
	}
	
}
