package com.ftkj.db.dao.logic;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillTree;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author tim.huang
 * 2017年9月27日
 *
 */
public class SkillDAO extends GameConnectionDAO {
	
	
	private RowHandler<PlayerSkill> PLAYERSKILL = new RowHandler<PlayerSkill>() {
		
		@Override
		public PlayerSkill handleRow(ResultSetRow row) throws Exception {
			PlayerSkill skill = new PlayerSkill();
			skill.setAttack(row.getInt("attack"));
			skill.setDefend(row.getInt("defend"));
			skill.setPlayerId(row.getInt("player_id"));
			
			int[]step1 = StringUtil.toIntArray(row.getString("step1"),StringUtil.DEFAULT_ST);
			int[] step2 = StringUtil.toIntArray(row.getString("step2"),StringUtil.DEFAULT_ST);
			int[] step3 = StringUtil.toIntArray(row.getString("step3"),StringUtil.DEFAULT_ST);
			int[] step4 = StringUtil.toIntArray(row.getString("step4"),StringUtil.DEFAULT_ST);
			int[] step5 = StringUtil.toIntArray(row.getString("step5"),StringUtil.DEFAULT_ST);
			int[] step6 = StringUtil.toIntArray(row.getString("step6"),StringUtil.DEFAULT_ST);
			skill.setTeamId(row.getLong("team_id"));
//			String skillStepStr = String.join(",", step1,step2,step3,step4,step5,step6);
			List<SkillTree> skillTree = Lists.newArrayList();
			skillTree.add(new SkillTree(1,step1[0],step1[1],step1[2],step1[3]));
			skillTree.add(new SkillTree(2,step2[0],step2[1],step2[2],step2[3]));
			skillTree.add(new SkillTree(3,step3[0],step3[1],step3[2],step3[3]));
			skillTree.add(new SkillTree(4,step4[0],step4[1],step4[2],step4[3]));
			skillTree.add(new SkillTree(5,step5[0],step5[1],step5[2],step5[3]));
			skillTree.add(new SkillTree(6,step6[0],step6[1],step6[2],step6[3]));
			skill.setSkillTree(skillTree);
			return skill;
		}
	};
	
	
	
	
	
	
	
	public List<PlayerSkill> getPlayerSkillList(long teamId) {
		String sql = "select * from t_u_skill where team_id = ?";
		return queryForList(sql, PLAYERSKILL, teamId);
	}
	
}
