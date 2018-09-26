package com.ftkj.tool.quartz.job;

import com.ftkj.db.conn.dao.DBManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

/**
 * @author tim.huang
 * 2016年2月25日
 * 系统异步更新数据定时器
 */
public class AsynchronousDBJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) {
        DBManager.run(false);
    }

}
