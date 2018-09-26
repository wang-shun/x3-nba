package com.ftkj.db.domain;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

/**
 * 新秀球员降薪记录日志表
 * @author mr.lei
 * @time 2018-8-6 15:40:12
 */
public class PlayerLowerSalaryLogPO extends AsynchronousBatchDB {

    private static final long serialVersionUID = 1L;

    /** id */
    private long plsId;
    /** 球队Id */
    private long teamId;
    /** 新秀球员Id */
    private int playerId;
    /** 降薪前工资*/
    private int beforeSalary;
    /** 降薪后工资 */
    private int afterSalary;
    /** 降薪的额度 */
    private int amount;
    /** 日期 */
    private DateTime createTime;

    public PlayerLowerSalaryLogPO() {
    }
    
    public PlayerLowerSalaryLogPO(long plsId, long teamId, int playerId,
			int beforeSalary, int afterSalary, int amount, DateTime createTime) {
		super();
		this.plsId = plsId;
		this.teamId = teamId;
		this.playerId = playerId;
		this.beforeSalary = beforeSalary;
		this.afterSalary = afterSalary;
		this.amount = amount;
		this.createTime = DateTime.now();
	}

    public DateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(DateTime createTime) {
        this.createTime = createTime;
    }

    public long getPlsId() {
		return plsId;
	}

	public void setPlsId(long plsId) {
		this.plsId = plsId;
	}

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getBeforeSalary() {
		return beforeSalary;
	}

	public void setBeforeSalary(int beforeSalary) {
		this.beforeSalary = beforeSalary;
	}

	public int getAfterSalary() {
		return afterSalary;
	}

	public void setAfterSalary(int afterSalary) {
		this.afterSalary = afterSalary;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	@Override
    public String getSource() {
        return StringUtil.formatSQL(this.plsId,
		this.teamId,
		this.playerId,
		this.beforeSalary,
		this.afterSalary,
		this.amount,
		this.createTime);
    }

    @Override
    public String getRowNames() {
        return "pls_id,team_id,player_id,before_salary,after_salary,amount,create_time";
    }

    @Override
    public List<Object> getRowParameterList() {
        return Lists.newArrayList(this.plsId,
        		this.teamId,
        		this.playerId,
        		this.beforeSalary,
        		this.afterSalary,
        		this.amount,
        		this.createTime);
    }

    @Override
    public synchronized void save() {
        super.save();
    }

    @Override
    public String getTableName() {
        return "t_u_player_lower_salary_log";
    }

    @Override
    public void del() {

    }

}
