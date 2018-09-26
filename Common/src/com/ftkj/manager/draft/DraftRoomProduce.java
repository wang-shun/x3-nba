package com.ftkj.manager.draft;

import java.io.Serializable;

/**
 * @author tim.huang
 * 2017年5月5日
 * 选秀房间生产对象
 */
public class DraftRoomProduce implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private DraftRoomBean roomBean;
	private DraftRoom curRoom;
	
	public DraftRoomProduce(DraftRoomBean roomBean, DraftRoom curRoom) {
		super();
		this.roomBean = roomBean;
		this.curRoom = curRoom;
	}
	public DraftRoomBean getRoomBean() {
		return roomBean;
	}
	public void setRoomBean(DraftRoomBean roomBean) {
		this.roomBean = roomBean;
	}
	public DraftRoom getCurRoom() {
		return curRoom;
	}
	public void setCurRoom(DraftRoom curRoom) {
		this.curRoom = curRoom;
	}
	
	
	
}
