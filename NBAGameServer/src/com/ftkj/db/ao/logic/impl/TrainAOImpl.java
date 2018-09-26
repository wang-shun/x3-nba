package com.ftkj.db.ao.logic.impl;

import java.util.List;

import com.ftkj.annotation.IOC;
import com.ftkj.db.ao.logic.ITrainAO;
import com.ftkj.db.conn.ao.BaseAO;
import com.ftkj.db.dao.logic.TrainDAO;
import com.ftkj.manager.train.LeagueTrain;
import com.ftkj.manager.train.TeamTrain;
import com.ftkj.manager.train.Train;

/**
 * @author tim.huang
 * 2017年7月19日
 *
 */
public class TrainAOImpl extends BaseAO implements ITrainAO {

	@IOC
	private TrainDAO trainDAO;	


    @Override
    public List<Train> getAllTrain() {   
        
        return trainDAO.getALlTrain();
    }

    @Override
    public List<TeamTrain> getAllTeamTrain() {
       
        return trainDAO.getALlTeamTrain();
    }

    @Override
    public List<Train> getTrainByTeamId(long teamId) {
        return trainDAO.getTrainByTeamId(teamId);
    }
    
    @Override
    public TeamTrain getTeamTrainByTeamId(long teamId) {
        return trainDAO.getTeamTrainByTeamId(teamId);
    }

    @Override
    public List<LeagueTrain> getLeagueTrainList() {
        return trainDAO.getLeagueTrainList();
    }

    @Override
    public void clearLeagueTrain() {
        trainDAO.clearLeagueTrain();
    }
}
