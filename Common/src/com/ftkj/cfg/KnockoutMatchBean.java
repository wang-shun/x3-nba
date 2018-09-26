package com.ftkj.cfg;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Lists;

/**
 * 多人赛配置
 * @author Jay
 * @time:2017年5月17日 下午7:23:34
 */
public class KnockoutMatchBean extends ExcelBean {

	private int tid;
	private int id;
	private String name;
	private int type;
	private int vtype;
	/**
	 * 周期，周几会开
	 */
	private List<Integer> startDay = Lists.newArrayList();
	// 小时数
	private int starHour;
	// 分钟数
	private int startMin;
	//
	private int needLv;
	private int needMaxLv;
	private List<PropSimple> needPropList;
	private int minTeam;
	private int maxTeam;
	private List<PropSimple> beyondPropList;
	private int beyondMax;
	private String stepBoxes;
	//邮件编号
	private int emailId;
	//自动补满NPC 
	private int autoNpc;
	private int sort;
	
	/**
	 * 最低战力限
	 */
	private int needCombat;
	
	/**
	 *  最大战力限制
	 */
	private int needMaxCombat;
	
	
	@Override
	public void initExec(RowData row) {
		String startTime = row.get("startTime");
		// 按格式解析
		if(this.type == 1) {
			String[] time = startTime.split("[:]");
			// 周几会开天
			this.startDay = Arrays.stream(row.get("dayWeek").toString().split(",")).mapToInt(s-> new Integer(s)).boxed().collect(Collectors.toList());
			// 小时
			this.starHour = Integer.parseInt(time[0]);
			// 分钟
			this.startMin = Integer.parseInt(time[1]);
		}
		//
		String needPropStr = row.get("needProp");
		if(needPropStr != null && needPropStr.indexOf(":") != -1) {
			this.needPropList = PropSimple.getPropBeanByStringNotConfig(row.get("needProp"));
		}
		String beyondPropStr = row.get("beyondProp");
		if(beyondPropStr != null && beyondPropStr.indexOf(":") != -1) {
			this.beyondPropList = PropSimple.getPropBeanByStringNotConfig(row.get("beyondProp"));
		}
	}
	public int getMatchId() {
		return this.tid + this.id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getNeedLv() {
		return needLv;
	}
	public void setNeedLv(int needLv) {
		this.needLv = needLv;
	}
	public int getMinTeam() {
		return minTeam;
	}
	public void setMinTeam(int minTeam) {
		this.minTeam = minTeam;
	}
	public int getMaxTeam() {
		return maxTeam;
	}
	public void setMaxTeam(int maxTeam) {
		this.maxTeam = maxTeam;
	}
	public int getBeyondMax() {
		return beyondMax;
	}
	public void setBeyondMax(int beyondMax) {
		this.beyondMax = beyondMax;
	}
	public String getStepBoxes() {
		return stepBoxes;
	}
	public void setStepBoxes(String stepBoxes) {
		this.stepBoxes = stepBoxes;
	}
	public List<PropSimple> getNeedPropList() {
		return needPropList;
	}
	public void setNeedPropList(List<PropSimple> needPropList) {
		this.needPropList = needPropList;
	}
	public List<PropSimple> getBeyondPropList() {
		return beyondPropList;
	}
	public void setBeyondPropList(List<PropSimple> beyondPropList) {
		this.beyondPropList = beyondPropList;
	}

	public int getStartMin() {
		return startMin;
	}

	public void setStartMin(int startMin) {
		this.startMin = startMin;
	}

	public int getStarHour() {
		return starHour;
	}

	public void setStarHour(int starHour) {
		this.starHour = starHour;
	}

	public int getEmailId() {
		return emailId;
	}

	public void setEmailId(int emailId) {
		this.emailId = emailId;
	}

	public int getAutoNpc() {
		return autoNpc;
	}

	public void setAutoNpc(int autoNpc) {
		this.autoNpc = autoNpc;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getNeedMaxLv() {
		return needMaxLv;
	}

	public void setNeedMaxLv(int needMaxLv) {
		this.needMaxLv = needMaxLv;
	}
	public List<Integer> getStartDay() {
		return startDay;
	}
	public void setStartDay(List<Integer> startDay) {
		this.startDay = startDay;
	}
	public int getVtype() {
		return vtype;
	}
	public void setVtype(int vtype) {
		this.vtype = vtype;
	}
  public int getNeedCombat() {
    return needCombat;
  }
  public void setNeedCombat(int needCombat) {
    this.needCombat = needCombat;
  }
  public int getNeedMaxCombat() {
    return needMaxCombat;
  }
  public void setNeedMaxCombat(int needMaxCombat) {
    this.needMaxCombat = needMaxCombat;
  }
	
	
}
