package com.ftkj.script.espn;

import java.util.List;

public class Events {
	public String uid;
	public String id;
	public List<Competitions> competitions;
	public String date;
	public Events(){}
	
	public Events(String uid,String id){
		this.uid = uid;
		this.id = id;
	}
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return "events [id=" + id + ", uid=" + uid + "]";
	}
	
	
}
