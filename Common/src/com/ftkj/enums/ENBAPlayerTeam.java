package com.ftkj.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年3月15日
 * NBA球队枚举
 */
public enum ENBAPlayerTeam {
	丹佛掘金(130,"den"),
	亚特兰大老鹰(113,"atl"),
	休斯顿火箭(111,"hou"),
	俄克拉荷马城雷霆(126,"okc"),
	克里夫兰骑士(119,"cle"),
	华盛顿奇才(115,"wsh"),
	印第安纳步行者(118,"ind"),
	圣安东尼奥马刺(102,"sa"),
	夏洛特黄蜂(116,"cha"),
	多伦多猛龙(129,"tor"),
	奥兰多魔术(108,"orl"),
	孟菲斯灰熊(103,"mem"),
	密尔沃基雄鹿(110,"mil"),
	底特律活塞(117,"det"),
	新奥尔良鹈鹕(122,"no"),
	新泽西篮网(128,"bkn"),
	明尼苏达森林狼(124,"min"),
	波士顿凯尔特人(114,"bos"),
	波特兰开拓者(125,"por"),
	洛杉矶快船(104,"lac"),
	洛杉矶湖人(105,"lal"),
	犹他爵士(127,"utah"),
	纽约尼克斯(109,"ny"),
	芝加哥公牛(101,"chi"),
	菲尼克斯太阳(123,"phx"),
	萨克拉门托国王(106,"sac"),
	费城76人(120,"phi"),
	达拉斯小牛(121,"dal"),
	迈阿密热火(107,"mia"),
	金州勇士(112,"gs"),
	X球队(131,""),
	退役(-1,""),
	无队(0,""),
	;
	
	private int tid;
	private String name;
	ENBAPlayerTeam(int tid,String name){
		this.tid = tid;
		this.name = name;
	}
	public int getTid() {
		return tid;
	}

	public String getName() {
		return name;
	}

	//通过ID，取对应的战术枚举
	public static final Map<Integer,ENBAPlayerTeam> NBAPlayerTeamEnumMap = new HashMap<Integer, ENBAPlayerTeam>();
	public static final Map<String,ENBAPlayerTeam> NBAPlayerTeamNameEnumMap = new HashMap<String, ENBAPlayerTeam>();
	static{
		for(ENBAPlayerTeam et : ENBAPlayerTeam.values()){
			NBAPlayerTeamEnumMap.put(et.getTid(), et);
			NBAPlayerTeamNameEnumMap.put(et.getName(), et);
		}
	}
	
	
	
	public static ENBAPlayerTeam getENBAPlayerTeam(int id){
		return NBAPlayerTeamEnumMap.get(id);
	}
	public static ENBAPlayerTeam getENBAPlayerTeam(String name){
		return NBAPlayerTeamNameEnumMap.get(name);
	}
	

	
	
}
