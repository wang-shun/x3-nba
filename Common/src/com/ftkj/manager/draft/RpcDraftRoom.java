package com.ftkj.manager.draft;

import java.io.Serializable;
import java.util.List;

/**
 * @author lmj
 * 跨服选秀序列化
 */
public class RpcDraftRoom implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int roomId;
	private List<Long> teamList;
	
	public RpcDraftRoom(int roomId, List<Long> teamList) {
		super();
		this.roomId = roomId;
		this.teamList = teamList;
	}

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public List<Long> getTeamList() {
		return teamList;
	}

	public void setTeamList(List<Long> teamList) {
		this.teamList = teamList;
	}
	
}
