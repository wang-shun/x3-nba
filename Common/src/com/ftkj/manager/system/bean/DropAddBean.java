package com.ftkj.manager.system.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;

/**
 * @author tim.huang
 * 2016年8月19日
 *
 */
public class DropAddBean extends DropBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DropAddBean(int dropId) {
		super(dropId);
	}

	@Override
	public List<PropSimple> roll() {
		return null;
	}

	@Override
	public List<PropSimple> roll(int args) {
		return null;
	}

	@Override
	public List<PropSimple> roll(String args) {
		List<AddProbability> addList = Arrays.stream(args.split(StringUtil.DEFAULT_ST))
										.map(config->new AddProbability(config)).collect(Collectors.toList());
		int newPro = super.getTotalProbability();
		for(DropProp dp : super.getDropList()){
			AddProbability ap = addList.stream().filter(a->a.getPropId() == dp.getProp().getPropId()).findFirst().orElse(null);
			if(ap == null) {
				continue;
			}
			int nd = Math.round((dp.getProbability()+ap.getAdd())*ap.getPer()/100f + ap.getAdd());
			newPro = newPro + nd;
			ap.setTotal(nd+dp.getProbability());
		}
		List<PropSimple> resultList = new ArrayList<PropSimple>();
		if(super.getDropList().size()<=0) {
			return resultList;
		}
		int ran = RandomUtil.randInt(newPro);
		int start = 0;
		for(int i = 0 ; i < super.getDropList().size();i++){
			DropProp dp = super.getDropList().get(i);
			AddProbability ap = addList.stream().filter(a->a.getPropId() == dp.getProp().getPropId()).findFirst().orElse(null);
			int po = dp.getProbability();
			if(ap != null) {
				po = ap.getTotal();
			}
			start += po;
			if(ran<start){
				if(dp.getProp().getPropId()!=0) {
					resultList.add(new PropSimple(dp.getProp().getPropId(),RandomUtil.randInt(dp.getMinNum(),dp.getMaxNum()),dp.getProp().getHour()));
				}
				return resultList;
			}
		}
		DropProp dp = super.getDropList().get(0);
		if(dp != null){
			if(dp.getProp().getPropId()!=0) {
				resultList.add(new PropSimple(dp.getProp().getPropId(),RandomUtil.randInt(dp.getMinNum(),dp.getMaxNum()),dp.getProp().getHour()));
			}
		}
		return resultList;
	}
	
	class AddProbability{
		private int propId;
		private int add;
		private int per;
		private int total;
		public AddProbability(String config) {
			super();
			String[] cc = config.split(StringUtil.DEFAULT_FH);
			this.propId = Integer.parseInt(cc[0]);
			this.add = Integer.parseInt(cc[1]);
			this.per = Integer.parseInt(cc[2]);
			this.total = 0;
		}
		public int getTotal() {
			return total;
		}
		public void setTotal(int total) {
			this.total = total;
		}
		public int getPropId() {
			return propId;
		}
		public int getAdd() {
			return add;
		}
		public int getPer() {
			return per;
		}		
	}

	@Override
	public List<PropSimple> roll(List<PropSimple> filterIds) {
		return null;
	}
}
