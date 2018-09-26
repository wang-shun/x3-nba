package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.console.SkillConsole;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.util.excel.RowData;

/**
 * 球员的层级对应的技能
 */
public class SkillPositionBean extends ExcelBean {
	
	private int playerId;
	private EPlayerPosition skillPosition;
	private int step;
	
	private int s1;
	private int s2;
	private int s3;
	private int s4;
	
	
	private SkillBean skill1;
	private SkillBean skill2;
	private SkillBean skill3;
	private SkillBean skill4;
	
	public boolean hasSkill(int skillId){
		
		return s1==skillId || 
				s2 == skillId ||
				s3 == skillId ||
				s4 == skillId;
				
	}
	
	public void initSkill(){
		this.skill1 = SkillConsole.getSkillBean(this.s1);
		this.skill2 = SkillConsole.getSkillBean(this.s2);
		this.skill3 = SkillConsole.getSkillBean(this.s3);
		this.skill4 = SkillConsole.getSkillBean(this.s4);
	}
	
	public SkillBean getSkill(int index){
		switch(index){
			case 0:{
				return skill1;
			}
			case 1:{
				return skill2;
			}
			case 2:{
				return skill3;
			}
			case 3:{
				return skill4;
			}
		}
		return null;
	}
	
	public int getNextSkillIndex(int startIndex) {
		if(startIndex > 3) {
			return 4;
		}
		SkillBean b = getSkill(startIndex);
		if(b != null) {
			return startIndex;
		} else {
			return getNextSkillIndex(startIndex++);
		}
	}
	
	public int getPlayerId() {
		return playerId;
	}

	public EPlayerPosition getSkillPosition() {
		return skillPosition;
	}

	public int getStep() {
		return step;
	}

	public SkillBean getSkill1() {
		return skill1;
	}

	public SkillBean getSkill2() {
		return skill2;
	}

	public SkillBean getSkill3() {
		return skill3;
	}

	public SkillBean getSkill4() {
		return skill4;
	}
	
	

	public int getS1() {
		return s1;
	}

	public int getS2() {
		return s2;
	}

	public int getS3() {
		return s3;
	}

	public int getS4() {
		return s4;
	}

	@Override
	public void initExec(RowData row) {
		int pid = row.get("position");
		this.skillPosition = EPlayerPosition.getEPlayerPosition(pid);
	}

}
