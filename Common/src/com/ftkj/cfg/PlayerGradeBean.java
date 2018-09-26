package com.ftkj.cfg;

import java.util.Map;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Maps;

/**
 * @author tim.huang 2017年9月28日 球员升级
 */
public class PlayerGradeBean extends ExcelBean {
	private int grade;
	private int needExp;
	private int sgjg;
	private int sgfs;
	private int cjg;
	private int cfs;
	private int sfjg;
	private int sffs;
	private int pfjg;
	private int pffs;
	private int pgjg;
	private int pgfs;
	private int needLevel;

	private Map<EPlayerPosition, int[]> powerMap;

	public int[] getPower(EPlayerPosition position) {
		return powerMap.get(position);
	}

	public int getNeedLevel() {
		return needLevel;
	}

	public int getGrade() {
		return grade;
	}

	public int getNeedExp() {
		return needExp;
	}

	@Override
	public void initExec(RowData row) {
		this.powerMap = Maps.newHashMap();
		this.powerMap.put(EPlayerPosition.C, new int[] { cjg, cfs });
		this.powerMap.put(EPlayerPosition.PF, new int[] { pfjg, pffs });
		this.powerMap.put(EPlayerPosition.PG, new int[] { pgjg, pgfs });
		this.powerMap.put(EPlayerPosition.SF, new int[] { sfjg, sffs });
		this.powerMap.put(EPlayerPosition.SG, new int[] { sgjg, sgfs });
	}

}
