package com.ftkj.manager.bid;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EStatus;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.StringUtil;

import java.util.List;

/**
 * @author tim.huang 2018年3月26日 球员竞价，训练数据
 */
public class PlayerTrainInfo extends AsynchronousBatchDB {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private long teamId;
	private int position;
	private EPlayerGrade grade;
	private EPlayerGrade maxGrade;
	private int status;
	private int group;
	private DateTime endTime;

	public PlayerTrainInfo(long teamId) {
		this();
		this.teamId = teamId;
	}

	public PlayerTrainInfo() {
		this.status = EStatus.Close.getId();
		this.grade = EPlayerGrade.D1;
		this.maxGrade = EPlayerGrade.D1;
		this.endTime = DateTime.now();
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public int getGroup() {
		return group;
	}

	public int getSecond() {
		if (this.endTime == null) {
			return 0;
		}
		int second = DateTimeUtil.secondBetween(DateTime.now(), this.endTime);
		return second <= 0 ? 0 : second;
	}

	public void updatePlayer(EPlayerGrade maxGrade, int position,int group) {
		this.grade = EPlayerGrade.D1;
		this.maxGrade = maxGrade;
		this.position = position;
		this.group = group;
		// this.status = EStatus.Close.getId();
		// this.endTime = DateTime.now().plusHours(20);
		this.save();
	}

	public void open() {
		this.status = EStatus.Open.getId();
		this.save();
		this.endTime = DateTime.now().plusHours(20);
	}

	public int getPosition() {
		return position;
	}

	public boolean isMax() {
		return EPlayerGrade.getGrade(this.grade).equals(
				EPlayerGrade.getGrade(this.maxGrade));
	}

	public EPlayerGrade levelUpGrade() {
		if (this.grade == EPlayerGrade.D1) {
			this.grade = EPlayerGrade.B1;
		} else if (this.grade == EPlayerGrade.B1) {
			this.grade = EPlayerGrade.A1;
		} else if (this.grade == EPlayerGrade.A1) {
			this.grade = EPlayerGrade.S1;
		}
		this.save();
		return this.grade;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public void endBid() {
		this.maxGrade = EPlayerGrade.D1;
		this.status = EStatus.Close.getId();
		this.save();
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public EPlayerGrade getGrade() {
		return grade;
	}

	public EPlayerGrade getMaxGrade() {
		return maxGrade;
	}

	public void setGrade(String grade) {
		this.grade = EPlayerGrade.convertByName(grade);
	}

	public void setMaxGrade(String maxGrade) {
		this.maxGrade = EPlayerGrade.convertByName(maxGrade);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public String getSource() {
		return StringUtil.formatSQL(this.teamId, this.group,this.position,
				this.grade.getGrade(), this.maxGrade.getGrade(), this.status,
				this.endTime);
	}

	@Override
	public String getRowNames() {
		return "team_id, `group`,`position`,grade, max_grade, `status`, end_time";
	}

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.teamId, this.group,this.position,
            this.grade.getGrade(), this.maxGrade.getGrade(), this.status,
            this.endTime);
    }

	@Override
	public String getTableName() {
		return "t_u_player_bid";
	}

	@Override
	public void del() {

	}


}
