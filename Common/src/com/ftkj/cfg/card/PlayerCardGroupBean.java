package com.ftkj.cfg.card;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

/**
 * 球星卡卡组配置
 * @author Jay
 * @time:2018年3月7日 下午5:00:22
 */
public class PlayerCardGroupBean extends ExcelBean {

	private String name;
	private int type;
	private int tab;
	private int rate;
	private PropSimple repeatedDrop;
	private int rollDrop;
	private List<Integer> playerList;
	
	@Override
	public void initExec(RowData row) {
		String[] strList = row.get("playerListConfig").toString().split(",");
		this.playerList = Arrays.stream(strList).filter(s-> !s.equals("")).mapToInt(s-> Integer.valueOf(s)).boxed().collect(Collectors.toList());
		//
		this.repeatedDrop = PropSimple.getPropSimpleByString(row.get("repetition"));
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
	public int getTab() {
		return tab;
	}
	public void setTab(int tab) {
		this.tab = tab;
	}
	public int getRate() {
		return rate;
	}
	public void setRate(int rate) {
		this.rate = rate;
	}
	public PropSimple getRepeatedDrop() {
		return repeatedDrop;
	}

	public void setRepeatedDrop(PropSimple repeatedDrop) {
		this.repeatedDrop = repeatedDrop;
	}

	public int getRollDrop() {
		return rollDrop;
	}
	public void setRollDrop(int rollDrop) {
		this.rollDrop = rollDrop;
	}
	public List<Integer> getPlayerList() {
		return playerList;
	}
	public void setPlayerList(List<Integer> playerList) {
		this.playerList = playerList;
	}
	
}
