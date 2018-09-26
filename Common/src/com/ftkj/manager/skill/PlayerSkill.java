package com.ftkj.manager.skill;

import java.util.List;

import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.console.SkillConsole;
import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

/**
 * @author tim.huang
 * 2017年9月11日
 * 球员单个技能
 */
public class PlayerSkill extends AsynchronousBatchDB {
    
	private static final long serialVersionUID = 1L;
	/** 球队ID*/
	private long teamId;
	/** 球员基础ID*/
	private int playerId;
	/** 技能攻击*/
	private int attack;
    /** 技能防御*/
	private int defend;	
	/** 技能树*/
	private List<SkillTree> skillTree;
	
	public PlayerSkill(long teamId, int playerId) {
		this.skillTree = Lists.newArrayList();
		this.teamId = teamId;
		this.playerId = playerId;
	}
	
	public int getMaxSkillPower(){
		SkillTree st = getSkillTree(6);
		
		if(st!=null && st.getMaxSkillLevel()>0){
			return st.superSkillPower();
		}
		SkillTree st2 = getSkillTree(4);
		if(st2!=null && st2.getMaxSkillLevel()>0){
			return st2.superSkillPower();
		}
		return 10000;
	}


	public PlayerSkill() {
		this.skillTree = Lists.newArrayList();
	}


	public SkillTree getSkillTree(int step){
		SkillTree tree = this.skillTree.stream().filter(s->s.getStep()==step).findFirst().orElse(null);
		if(tree == null){
			tree = new SkillTree(step, 0, 0, 0, 0);
			this.skillTree.add(tree);
		}
		return tree;
	}
	
	public int getMaxStep() {
		int step = this.skillTree.stream().filter(s-> s.getMaxSkillLevel()>0).mapToInt(s-> s.getStep()).max().orElse(0); 
		return step;
	}
	
	public List<SkillTree> getSkillTree() {
		return skillTree;
	}

	public int getAttack() {
		return attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getDefend() {
		return defend;
	}

	public void setDefend(int defend) {
		this.defend = defend;
	}



	public void setSkillTree(List<SkillTree> skillTree) {
		this.skillTree = skillTree;
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

	private static final String defaultTree = "0,0,0,0";
	
	@Override
	public String getSource() {
		SkillTree st1 = getSkillTree(1);
		SkillTree st2 = getSkillTree(2);
		SkillTree st3 = getSkillTree(3);
		SkillTree st4 = getSkillTree(4);
		SkillTree st5 = getSkillTree(5);
		SkillTree st6 = getSkillTree(6);
		
		return StringUtil.formatSQL(this.teamId,this.playerId,this.attack,this.defend,st1==null?defaultTree:st1.getSql()
				,st2==null?defaultTree:st2.getSql()
						,st3==null?defaultTree:st3.getSql()
								,st4==null?defaultTree:st4.getSql()
										,st5==null?defaultTree:st5.getSql()
												,st6==null?defaultTree:st6.getSql());
	}
	

	@Override
	public String getRowNames() {
		return "team_id, player_id, attack, defend, step1, step2, step3, step4, step5, step6";
	}

    @Override
    public List<Object> getRowParameterList() {
        SkillTree st1 = getSkillTree(1);
        SkillTree st2 = getSkillTree(2);
        SkillTree st3 = getSkillTree(3);
        SkillTree st4 = getSkillTree(4);
        SkillTree st5 = getSkillTree(5);
        SkillTree st6 = getSkillTree(6);

        return Lists.newArrayList(this.teamId,this.playerId,this.attack,this.defend,st1==null?defaultTree:st1.getSql()
            ,st2==null?defaultTree:st2.getSql()
            ,st3==null?defaultTree:st3.getSql()
            ,st4==null?defaultTree:st4.getSql()
            ,st5==null?defaultTree:st5.getSql()
            ,st6==null?defaultTree:st6.getSql());
    }

	@Override
	public String getTableName() {
		return "t_u_skill";
	}
	
	@Override
	public void del() {
		
	}
	
	/**
	 * 球员位置变化后，技能自适应转换
	 * 技能转换重置playerId后，根据player的位置重新初始化技能
	 * @param oldPlayer
	 * @param newPlayer
	 * @param attack
	 * @param defend
	 */
	public void resetPosSkill(int oldPlayer, int newPlayer, EPlayerPosition oldPosition, EPlayerPosition newPosition, int attack, int defend) {
		this.playerId = newPlayer;
		//
//		PlayerBean  pb1 = PlayerConsole.getPlayerBean(oldPlayer);
//		PlayerBean  pb2 = PlayerConsole.getPlayerBean(newPlayer);
		int oldPos = oldPosition.getId();
		int newPos = newPosition.getId();
		/*
		 * 1~3 是同样的
		 * 4~5 不用处理
		 * 5~6 专属技能，仅有一个
		 */
		// 先做位置处理
		int t1 = getSkillType(oldPos);
		int t2 = getSkillType(newPos);
		if(t1 != t2) {
			// 要做对调处理
			if(oldPos <=3 ) {
				SkillTree s1 = getSkillTree(1);
				s1.updateLevel(0, s1.getSkillLevel(2));
				s1.updateLevel(1, s1.getSkillLevel(3));
				s1.updateLevel(2, 0);
				s1.updateLevel(3, 0);
				SkillTree s2 = getSkillTree(2);
				s2.updateLevel(1, s2.getSkillLevel(2));
				s2.updateLevel(2, 0);
			}else {
				SkillTree s1 = getSkillTree(1);
				s1.updateLevel(2, s1.getSkillLevel(0));
				s1.updateLevel(3, s1.getSkillLevel(1));
				s1.updateLevel(0, 0);
				s1.updateLevel(1, 0);
				SkillTree s2 = getSkillTree(2);
				s2.updateLevel(2, s2.getSkillLevel(1));
				s2.updateLevel(1, 0);
			}
		}
		// 如果是专属做专属处理
		if(SkillConsole.checkPlayerSkillPositionBean(this.playerId)) {
			// 第五层
			SkillPositionBean oldPosBean1 = SkillConsole.getSkillPositionBean(oldPosition, 5, oldPlayer);
			SkillPositionBean newPosBean1 = SkillConsole.getSkillPositionBean(oldPosition, 5, newPlayer);
			SkillPositionBean oldPosBean2 = SkillConsole.getSkillPositionBean(oldPosition, 6, oldPlayer);
			SkillPositionBean newPosBean2 = SkillConsole.getSkillPositionBean(oldPosition, 6, newPlayer);
			int s5Level = 0;
			SkillTree s5Tree = getSkillTree(5);
			int oldIndex = oldPosBean1.getNextSkillIndex(0);
			s5Level = oldIndex == 4 ? 0 : s5Tree.getSkillLevel(oldIndex);
			int newIndex = newPosBean1.getNextSkillIndex(0);
			s5Tree.updateLevel(newIndex, s5Level);
			s5Tree.updateLevel(oldIndex, 0);
			// 第六层
			int s6Level = 0;
			SkillTree s6Tree = getSkillTree(6);
			int oldIndex2 = oldPosBean2.getNextSkillIndex(0);
			s6Level = oldIndex2 == 4 ? 0 : s6Tree.getSkillLevel(oldIndex2);
			int newIndex2 = newPosBean2.getNextSkillIndex(0);
			s5Tree.updateLevel(newIndex2, s6Level);
			s5Tree.updateLevel(oldIndex2, 0);
		}
		// 重新选择最高等级技能 技能ID，等级
		setAttack(attack);
		setDefend(defend);
		// 穿
		save();
	}
	
	/**
	 * 1~3位置是技能分类1，4~5是分类2
	 * @param posInt
	 * @return
	 */
	private int getSkillType(int posInt) {
		if(posInt <= 3) {
			return 1;
		}
		return 2;
	}



    //	/**
//	 * 自动装备技能，转换后调用,暂不实现
//	 * @param position
//	 * @param playerId
//	 */
//	private void autoEqui(EPlayerPosition position, int playerId) {
//		int attackId=0; 
//		int attackLv=0;
//		int defendId=0; 
//		int defendLv=0;
//		for(int step : new int[]{1, 4, 6}) {
//			SkillPositionBean temp = SkillConsole.getSkillPositionBean(position, step, playerId);
//			if(temp == null) continue;
//			// 进攻技能
//			SkillBean sb = temp.getSkill1();
//		}
//	}
	
}
