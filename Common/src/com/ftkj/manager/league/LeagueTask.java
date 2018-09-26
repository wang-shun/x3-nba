package com.ftkj.manager.league;

import java.io.Serializable;
import java.util.Arrays;

import com.ftkj.enums.EStatus;

/**
 * @author tim.huang
 * 2017年5月26日
 *
 */
public class LeagueTask implements Serializable{

	private static final long serialVersionUID = 1L;

	private int id;
	private int[] counts;
	
	/**EStatus*/
	private int status;
	private int total;
	
//	private DateTime endTime;
//	private DateTime startTime;
	
	private LeagueTask(){}

	public static LeagueTask createLeagueTask(LeagueTaskBean taskBean){
		LeagueTask task = new LeagueTask();
		task.setId(taskBean.getTid());
		boolean bool = taskBean.getTaskProps() == null || taskBean.getTaskProps().size() == 0;
		int length = bool ? 1 : taskBean.getTaskProps().size();
		task.setCounts(new int[length]);
		Arrays.fill(task.getCounts(), 0);
		//日常任务自动开启
		task.setStatus(EStatus.Open.getId());
		return task;
	}
	
	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

//	public boolean isStart(){
//		DateTime now = DateTime.now();
//		return now.isAfter(this.startTime) && now.isBefore(this.endTime);
//	}
//	
//	public DateTime getStartTime() {
//		return startTime;
//	}
//
//	public void setStartTime(DateTime startTime) {
//		this.startTime = startTime;
//	}
//
//	public DateTime getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(DateTime endTime) {
//		this.endTime = endTime;
//	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int[] getCounts() {
		return counts;
	}

	public void setCounts(int[] counts) {
		this.counts = counts;
	}

	/**
	 * 1任务开启
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	
	
}
   