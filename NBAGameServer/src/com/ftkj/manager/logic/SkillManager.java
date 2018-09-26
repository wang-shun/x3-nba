package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.cfg.SkillLevelBean;
import com.ftkj.cfg.SkillPositionBean;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.SkillConsole;
import com.ftkj.db.ao.logic.ISkillAO;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.OfflineOperation;
import com.ftkj.manager.player.Player;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.skill.PlayerSkill;
import com.ftkj.manager.skill.SkillBean;
import com.ftkj.manager.skill.SkillTree;
import com.ftkj.manager.skill.TeamSkill;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.SkillPB;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.RandomUtil;
import com.ftkj.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author tim.huang
 * 2017年9月26日
 *
 */
public class SkillManager extends BaseManager implements OfflineOperation {
	
	@IOC
	private ISkillAO skillAO;
	
	@IOC
	private PropManager propManager;
	
	@IOC
	private TaskManager taskManager;
	
	@IOC
	private TeamManager teamManager;
	
	@IOC
	private PlayerManager playerManager;
	
	private Map<Long,TeamSkill> teamSkillMap;

	
	
	@ClientMethod(code = ServiceCode.SkillManager_showSkillMain)
	public void showSkillMain(){
		long teamId = getTeamId();
		TeamSkill ts = getTeamSkill(teamId);
		sendMessage(getSkillMain(teamId, ts));
	}
	
	/**
	 * @param playerId
	 * @param step 从1开始(层级)
	 * @param index 从0开始
	 */
	@ClientMethod(code = ServiceCode.SkillManager_levelUp)
	public void levelUp(int playerId,int step,int index){
		long teamId = getTeamId();
		if(!PlayerConsole.existPlayer(playerId)) {
			log.debug("球员不存在");
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		EPlayerPosition position = playerManager.getTeamPlayer(teamId).getplayerPosition(playerId);
		SkillPositionBean positionBean = SkillConsole.getSkillPositionBean(position, step,playerId);
		
		if(positionBean == null){
			log.debug("升级失败，所需SkillPositionBean配置不存在{}-{}",position,step);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		SkillBean skill =positionBean.getSkill(index);
		if(skill == null){
			log.debug("升级失败，所需SkillBean配置不存在{}-{}-{}",position,step,index);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		TeamSkill ts = getTeamSkill(teamId);
		PlayerSkill ps = ts.getPlayerSkill(playerId);
		if(ps == null){
			ps = new PlayerSkill(teamId,playerId);
			ts.getPlayerSkillMap().put(playerId, ps);
		}
		SkillTree tree = ps.getSkillTree(step);
		int curLevel = tree.getSkillLevel(index);
		int maxLevel = skill.getMaxLevel();
		if(curLevel+1>maxLevel){
			log.debug("升级失败，技能超过等级上限{}-{}-{}",skill.getSkillId(),curLevel,maxLevel);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}		
		SkillLevelBean  levelBean = SkillConsole.getSkillLevelBean(step, curLevel+1);
		if(levelBean == null){
			log.debug("升级失败，技能等级不存在{}-{}-{}",skill.getSkillId(),curLevel,maxLevel);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		boolean ok = true;
		if(step == 6){//第六层，特殊处理
			SkillPositionBean tmpPostion = SkillConsole.getSkillPositionBean(position, 3,playerId);
			SkillTree tt = ps.getSkillTree(3);
			if(tmpPostion.getSkill1().attack()==skill.attack() && tt.getS1()<tmpPostion.getSkill1().getMaxLevel()){//
				ok = false;
			}else if(tmpPostion.getSkill2().attack()==skill.attack() && tt.getS2()<tmpPostion.getSkill2().getMaxLevel()){
				ok = false;
			}else if(tmpPostion.getSkill3().attack()==skill.attack() && tt.getS3()<tmpPostion.getSkill3().getMaxLevel()){
				ok = false;
			}else if(tmpPostion.getSkill4().attack()==skill.attack() && tt.getS4()<tmpPostion.getSkill4().getMaxLevel()){
				ok = false;
			}
		}
		if(!ok){
			log.debug("升级失败，升级第六层技能未达到顶级{}-{}-{}",skill.getSkillId(),curLevel,maxLevel);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
		if(step>1 && step<6){//不是第一层要验证上一层限制
			SkillTree tt = ps.getSkillTree(step-1);
			int needAllLevel = getNeedAllLevel(step);
			if(tt == null || tt.getAllLevel()<needAllLevel){
				log.debug("升级失败，技能上一层数据不存在{}-{}-{}",skill.getSkillId(),curLevel,maxLevel);
				sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
				return;
			}
		}
		
		//升级消耗道具扣除
		if(propManager.delProp(teamId,levelBean.getNeedPropId(),levelBean.getNum(),true,true)==null){
			log.debug("升级失败，道具不足{}-{}-{}",skill.getSkillId(),curLevel,maxLevel);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		//走升级概率
		int rate = levelBean.getRate();
		int failRate = getSkillLevelFailRate(teamId, playerId, skill.getSkillId());
		rate += failRate;
		int ran = RandomUtil.randInt(10000);
		
		if(levelBean.getMaxRate()>0){
			rate = rate>levelBean.getMaxRate()?levelBean.getMaxRate():rate;
		}
		
		Team team = teamManager.getTeam(teamId);
		if("g=336".equals(team.getHelp()) || "g=358".equals(team.getHelp())){
			rate+=10000;
		}
		
		if(ran>rate){//失败
			int num = failRate+levelBean.getFailAdd();
			saveSkillLevelFailRate(teamId, playerId, skill.getSkillId(),
					num > levelBean.getMaxRate() ? levelBean.getMaxRate() : num);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(""+curLevel).build());
			return;
		}else {
			// 升级成功,叠加的概率重置为0
			saveSkillLevelFailRate(teamId, playerId, skill.getSkillId(), 0);
		}
		
		int level = curLevel+1;
		tree.updateLevel(index, level);
		ps.save();
		taskManager.updateTask(teamId, ETaskCondition.球员技能, 1, step + ","+level);
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(""+level).build());
	}
	
	public void createTeamPlayerSkill(long teamId,int playerId){
		PlayerBean  pb = PlayerConsole.getPlayerBean(playerId);
		EPlayerPosition position = pb.getPosition()[0];
		SkillPositionBean positionBean = SkillConsole.getSkillPositionBean(position, 1, playerId);
		
		if(positionBean == null){
			log.debug("升级失败，所需SkillPositionBean配置不存在{}-{}",position,1);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		SkillBean skill =null;
		int i = 0;
		for( ; i < 4;i++){
			skill = positionBean.getSkill(i);
			if(skill!=null) break;
		}
		
		
		TeamSkill ts = getTeamSkill(teamId);
		PlayerSkill ps = ts.getPlayerSkill(playerId);
		if(ps == null){
			ps = new PlayerSkill(teamId,playerId);
			ts.getPlayerSkillMap().put(playerId, ps);
		}
		SkillTree tree = ps.getSkillTree(1);
		int level = 5;
		tree.updateLevel(i, level);
		ps.save();
//		taskManager.updateTask(teamId, ETaskCondition.球员技能, 1, EModuleCode.球员技能.getName());
//		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(""+level).build());
		
	}
	
	
	@ClientMethod(code = ServiceCode.SkillManager_equPlayerSkill)
	public void equPlayerSkill(int playerId,int attackId,int defendId){
		long teamId = getTeamId();
		TeamSkill ts = getTeamSkill(teamId);
		PlayerSkill ps = ts.getPlayerSkill(playerId);
//		PlayerBean  pb = PlayerConsole.getPlayerBean(playerId);
		//EPlayerPosition position = pb.getPosition()[0];
		Player player = playerManager.getTeamPlayer(teamId).getPlayer(playerId);
		if(player == null || ps == null){
			log.debug("技能装备异常,有一个不存在的技能{}-{}-{}",playerId,attackId,defendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		List<SkillPositionBean> stepList = ps.getSkillTree().stream()
				.map(sk->SkillConsole.getSkillPositionBean(player.getPlayerPosition(),sk.getStep(),playerId))
				.filter(sk->sk!=null)
				.collect(Collectors.toList());
		boolean at = attackId==0?true:stepList.stream().filter(sk->sk.hasSkill(attackId)).findFirst().isPresent();
		boolean de = defendId==0?true:stepList.stream().filter(sk->sk.hasSkill(defendId)).findFirst().isPresent();
		if(!at || !de){//其中有一个不存在
			log.debug("技能装备异常,有一个不存在的技能{}-{}-{}",playerId,attackId,defendId);
			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
			return;
		}
		
		ps.setAttack(attackId);
		ps.setDefend(defendId);
		ps.save();
		sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
	}
	
	private int getNeedAllLevel(int step){
		if(step == 2){
			return 10;
		}else if(step == 3){
			return 5;
		}else if(step == 4){
			return 5;
		}else if(step == 5){
			return 5;
		}
		return 10;
	}
	
	
	public TeamSkill getTeamSkill(long teamId){
		TeamSkill ts = teamSkillMap.get(teamId);
		if(ts == null){
			List<PlayerSkill> psList = skillAO.getPlayerSkillList(teamId);
			ts = new TeamSkill(psList);
			this.teamSkillMap.put(teamId, ts);
		}
		return ts;
	}
	
	
	public SkillPB.SkillMainData getSkillMain(long teamId,TeamSkill ts){
		List<SkillPB.SkillPlayerData> playerList = ts.getPlayerSkillMap().values()
				.stream().map(pl->getSkillPlayerData(pl))
				.collect(Collectors.toList());
		
		List<SkillPB.SkillFailData> failList = Lists.newArrayList();
		Map<String,String> failMap = getSkillLevelFailMap(teamId);
		failMap.forEach((key,val)->{
			//ss[0]=playerId,ss[1]=skillId
			int ss[] = StringUtil.toIntArray(key, StringUtil.DEFAULT_ST);
			failList.add(getSkillFailData(ss,Ints.tryParse(val)));
		});
		
		return SkillPB.SkillMainData.newBuilder().addAllPlayerSkillList(playerList).addAllFailList(failList).build();
	}
	
	private SkillPB.SkillFailData getSkillFailData(int ss[],int val){
		return SkillPB.SkillFailData.newBuilder().setPlayerId(ss[0]).setSkillId(ss[1]).setAdd(val).build();
	}
	
	
	private SkillPB.SkillPlayerData getSkillPlayerData(PlayerSkill skill){
		List<SkillPB.SkillStepData> skillStepList = skill.getSkillTree()
				.stream().map(s->getSkillStepData(s))
				.collect(Collectors.toList());
		
		return SkillPB.SkillPlayerData.newBuilder()
				.setPlayerId(skill.getPlayerId())
				.addAllSkillStepList(skillStepList)
				.setAttack(skill.getAttack())
				.setDefend(skill.getDefend())
				.build();
		
		
		
	}
	
	private SkillPB.SkillStepData getSkillStepData(SkillTree skillTree){
		return SkillPB.SkillStepData.newBuilder()
				.setStep(skillTree.getStep())
				.setS1(skillTree.getS1())
				.setS2(skillTree.getS2())
				.setS3(skillTree.getS3())
				.setS4(skillTree.getS4())
				.build();
				
	}
	
	
	
	
	
	
	private Map<String,String> getSkillLevelFailMap(long teamId){
		Map<String,String> result = redis.getMapAllKeyValues(RedisKey.getDayKey(teamId, RedisKey.Skill_Level_Fail));
		return result;
	}
	
	private int getSkillLevelFailRate(long teamId,int playerId,int skillId){
		String field = playerId + "," + skillId;
		String result = redis.hget(RedisKey.getDayKey(teamId, RedisKey.Skill_Level_Fail), field);
		return result == null?0:Ints.tryParse(result);
	}
	
	private void saveSkillLevelFailRate(long teamId,int playerId,int skillId,int val){
		String field = playerId + "," + skillId;
		redis.putMapValueExp(RedisKey.getDayKey(teamId, RedisKey.Skill_Level_Fail), field, ""+val);
	}
	
	/**
	 * 转换技能
	 * @param teamId
	 * @param player1
	 * @param player2
	 */
	public void transSkill(long teamId, int player1, int player2, EPlayerPosition p1Position, EPlayerPosition p2Position) {
		TeamSkill teamSkill = getTeamSkill(teamId);
		PlayerSkill skill1 = teamSkill.getPlayerSkill(player1);
		PlayerSkill skill2 = teamSkill.getPlayerSkill(player2);
		if(skill1 == null){
			skill1 = new PlayerSkill(teamId,player1);
			teamSkill.getPlayerSkillMap().put(player1, skill1);
		}
		if(skill2 == null){
			skill2 = new PlayerSkill(teamId,player2);
			teamSkill.getPlayerSkillMap().put(player2, skill2);
		}
		int attack1 = skill1.getAttack();
		int defend1 = skill1.getDefend();
		int attack2 = skill2.getAttack();
		int defend2 = skill2.getDefend();
		skill1.resetPosSkill(player1, player2, p1Position, p2Position, 0, 0);
		skill2.resetPosSkill(player2, player1, p2Position, p1Position, 0, 0);
		teamSkill.getPlayerSkillMap().put(skill1.getPlayerId(), skill1);
		teamSkill.getPlayerSkillMap().put(skill2.getPlayerId(), skill2);
	}
	
	@Override
	public void offline(long teamId) {
		teamSkillMap.remove(teamId);
	}

    @Override
    public void dataGC(long teamId) {
        teamSkillMap.remove(teamId);
    }

    @Override
	public void instanceAfter() {
		teamSkillMap = Maps.newConcurrentMap();
	}

}
