package com.ftkj.console;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ftkj.cfg.ArchiveBean;

public class ArchiveConsole {

	private static Map<Integer, List<ArchiveBean>> archiveMap;
	
	public static int TransCard_Item_ID = 1051;
	
	public static void init() {
		archiveMap = CM.archiveList.stream().collect(Collectors.groupingBy(v->v.getType(),Collectors.toList()));
		
	}
	
	
	/**
	 * 装备所需要数量
	 * @param strLv
	 * @param lv
	 * @param qua
	 * @return
	 */
	public static int getEquiNeedNum(int strLv, int lv, int qua) {
		int num = 0;
		num += getTypeNum(3, strLv);
		num += getTypeNum(4, lv);
		num += getTypeNum(5, qua);
		return num;
	}
	
	
	public static int getPlayerTrainNeedNum(int lv) {
		return getTypeNum(2, lv);
	}
	
	public static int getPlayerGradeNeedNum(int grade) {
		return getTypeNum(1, grade);
	}
	
	public static int getPlayerSkillNeedNum(int grade) {
		return getTypeNum(6, grade);
	}
	
	public static int getTypeNum(int type, int value) {
		ArchiveBean a1 = archiveMap.get(type).stream().filter(s-> value >= s.getStart() && value <= s.getEnded()).findFirst().orElse(null);
		return a1 == null ? 0 : a1.getNeedNum();
	}
}
