package com.ftkj.domain.data;

import java.io.Serializable;
import java.util.Date;

public class NBASeason implements Serializable{
	private static final long serialVersionUID = -4238341509126723876L;
	private int id;
	private Date startDate;
	private Date endDate;
	
	public NBASeason() {}
	//
	public static NBASeason create(int id,Date startDate, Date endDate) {
		NBASeason s=new NBASeason();
		s.setId(id);
		s.setStartDate(startDate);
		s.setEndDate(endDate);
		return s;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}
