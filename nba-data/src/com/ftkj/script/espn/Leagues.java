package com.ftkj.script.espn;

import java.util.List;

public class Leagues {
	private String uid;
	private String id;
	private String name;
	
	public Leagues(){}
	
	public Leagues(String uid,String id,String name){
		this.uid = uid;
		this.id = id;
		this.name = name;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "leagues [id=" + id + ", name=" + name + ", uid=" + uid + "]";
	}	
	
}
