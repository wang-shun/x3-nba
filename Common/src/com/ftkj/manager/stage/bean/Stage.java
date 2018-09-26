package com.ftkj.manager.stage.bean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ftkj.console.StageConsole;
import com.ftkj.db.domain.StagePO;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.stage.cfg.StageBean;

public class Stage {

	private StagePO stagePO;
	private List<String> matchScore;
	
	public Stage(StagePO stagePO) {
		this.stagePO = stagePO;
		String val = stagePO.getScore();
		this.matchScore = Arrays.stream((val==null?"":val).split(",")).collect(Collectors.toList());
	}

	public void save() {
		StringBuilder sb = new StringBuilder();
		for(String s : matchScore) {
			if(s.equals("")) {
				continue;
			}
			sb.append(s).append(",");
		}
		stagePO.setScore(sb.toString());
		stagePO.save();
	}
	
	public int getScene() {
		return this.stagePO.getScene();
	}
	
	public long getTeamId() {
		return this.stagePO.getTeamId();
	}

	public void setScene(int scene) {
		this.stagePO.setScene(scene);
	}

	public int getStageId() {
		return this.stagePO.getStageId();
	}

	public void setStageId(int stageId) {
		this.stagePO.setStageId(stageId);
	}

	public int getStep() {
		return this.stagePO.getStep();
	}

	public void setStep(int step) {
		this.stagePO.setStep(step);
	}
	
	public List<String> getMatchScore() {
		return matchScore;
	}

	/**
	 * 通关后处理
	 * 最后一关时，scene要变成1
	 * @param report
	 */
	public void endPkReport(EndReport report) {
		//
		int stageId = this.stagePO.getStageId(); 
		// 季后赛记录比分
		StageBean bean = StageConsole.getStageBean(stageId);
		if((bean.getType()==2 && bean.getStep() == 16) || (bean.getType()==1 && bean.getStep() == 82)) {
			setScene(1);
		}
		if(bean.getStageId()+1 < StageConsole.MAX_STAGE) {
			setStageId(bean.getStageId() + 1);
		}
		// 记录比分
		this.matchScore.add(report.getHomeScore() + ":" + report.getAwayScore());
		this.save();
	}
	
	
	/**
	 * 取当前关卡对手的NPCID
	 * @return
	 */
	public long getNpcId() {
		return StageConsole.getStageBean(this.getStageId()).getNpcId();
	}
	
	/**
	 * 进入下一赛季
	 * @return
	 */
	public void nextScene() {
		this.setScene(0);
		this.save();
	}

}
