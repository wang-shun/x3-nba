package com.ftkj.cfg.card;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.util.excel.RowData;

public class PlayerCardGradeCap extends ExcelBean {

	private int type;
	private int quality;
	private int starLv;
	private List<EPlayerGrade> gradeList;
	private List<Float> cardCapList;
	private List<Float> baseCapList;
	
	@Override
	public void initExec(RowData row) {
		String v1 = row.get("playerGrade");
		gradeList = Arrays.stream(v1.split(",")).map(g-> EPlayerGrade.convertByName(g)).collect(Collectors.toList());
		//
		String v2 = row.get("cardCap");
		cardCapList = Arrays.stream(v2.split(",")).map(g-> Float.valueOf(g) / 10000f).collect(Collectors.toList());
		//
		String v3 = row.get("baseCap");
		baseCapList = Arrays.stream(v3.split(",")).map(g-> Float.valueOf(g) / 10000f).collect(Collectors.toList());
	}
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getQuality() {
		return quality;
	}
	public void setQuality(int quality) {
		this.quality = quality;
	}
	public int getStarLv() {
		return starLv;
	}
	public void setStarLv(int starLv) {
		this.starLv = starLv;
	}

	public List<EPlayerGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<EPlayerGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<Float> getCardCapList() {
		return cardCapList;
	}

	public void setCardCapList(List<Float> cardCapList) {
		this.cardCapList = cardCapList;
	}

	public List<Float> getBaseCapList() {
		return baseCapList;
	}

	public void setBaseCapList(List<Float> baseCapList) {
		this.baseCapList = baseCapList;
	}
	
	public float getCardCap(EPlayerGrade grade) {
		int index = gradeList.indexOf(grade);
		return cardCapList.get(index == -1 ? 0 : index);
	}
	public float getBaseCap(EPlayerGrade grade) {
		int index = gradeList.indexOf(grade);
		// 不能有X，收集了X会越界
		return baseCapList.get(index == -1 ? 0 : index);
	}
	
}
