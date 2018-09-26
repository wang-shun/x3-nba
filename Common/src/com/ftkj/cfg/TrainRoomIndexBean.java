package com.ftkj.cfg;

import java.util.List;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年7月17日
 * 训练馆各个下标
 */
public class TrainRoomIndexBean extends ExcelBean{
	private int id;
	private int roomId;
	private int indexId;
	private int count;
	private int roomType;
	private PropSimple boxProp1;
	private static final int b1 = 60*60*4;
	private PropSimple boxProp2;
	private static final int b2 = 60*60*8;
	private PropSimple boxProp3;
	private static final int b3 = 60*60*12;
	private PropSimple boxProp4;
	private static final int b4 = 60*60*16;
	private PropSimple boxProp5;
	private static final int b5 = 60*60*20;
	private PropSimple boxProp6;
	private static final int b6 = 60*60*24;
	
	@Override
	public void initExec(RowData row) {
		this.boxProp1 = PropSimple.getPropBeanByStringNotConfig(row.get("box1")).get(0);
		this.boxProp2 = PropSimple.getPropBeanByStringNotConfig(row.get("box2")).get(0);
		this.boxProp3 = PropSimple.getPropBeanByStringNotConfig(row.get("box3")).get(0);
		this.boxProp4 = PropSimple.getPropBeanByStringNotConfig(row.get("box4")).get(0);
		this.boxProp5 = PropSimple.getPropBeanByStringNotConfig(row.get("box5")).get(0);
		this.boxProp6 = PropSimple.getPropBeanByStringNotConfig(row.get("box6")).get(0);
	}
	
	public List<PropSimple> getGifts(int second,int decCount,int leagueCount,float vipBuff){
		List<PropSimple> gifts = Lists.newArrayList();
		int cc =  count*second-decCount;
		leagueCount = 100 + leagueCount<=0?1:leagueCount;
		if(leagueCount>=3){
			leagueCount = leagueCount * 5 - 5;
			cc = (int)(cc * leagueCount/100); 
		}
		vipBuff = vipBuff ==0?1:vipBuff;
		gifts.add(new PropSimple(1401, cc<=0?1:(int)(cc * vipBuff)));
		second+=3;
		if(second>=b1) {
			gifts.add(this.boxProp1);
		}
		if(second>=b2) {
			gifts.add(this.boxProp2);
		}
		if(second>=b3) {
			gifts.add(this.boxProp3);
		}
		if(second>=b4) {
			gifts.add(this.boxProp4);
		}
		if(second>=b5) {
			gifts.add(this.boxProp5);
		}
		if(second>=b6) {
			gifts.add(this.boxProp6);
		}
		return gifts;
	}
	
	//最高可获得
	public int getGrabGift(int second){
		return Math.round(count*second*0.1f);
	}
	
	public int getGrabGiftS(int second,int desCount){
		return count*second-desCount;
	}
	
	public int getId() {
		return id;
	}

	public int getRoomId() {
		return roomId;
	}
	public int getIndexId() {
		return indexId;
	}
	public int getCount() {
		return count;
	}

	
	
	public int getRoomType() {
		return roomType;
	}

	public void setRoomType(int roomType) {
		this.roomType = roomType;
	}

	@Override
	public String toString() {
		return "TrainRoomIndexBean [id=" + id + ", roomId=" + roomId + ", indexId=" + indexId + ", count=" + count
				+ "]";
	}
	
}
