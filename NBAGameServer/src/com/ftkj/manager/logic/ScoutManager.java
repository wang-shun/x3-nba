package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.DropConsole;
import com.ftkj.console.PlayerConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ENBAPlayerTeam;
import com.ftkj.enums.EPlayerGrade;
import com.ftkj.enums.EPropType;
import com.ftkj.enums.EStatus;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.event.param.ScountParam;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.besign.TeamBeSignPlayer;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.player.PlayerBean;
import com.ftkj.manager.player.PlayerTalent;
import com.ftkj.manager.player.TeamPlayer;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.prop.bean.PropBean;
import com.ftkj.manager.prop.bean.PropExtPlayerBean;
import com.ftkj.manager.scout.ScoutFree;
import com.ftkj.manager.scout.ScoutPlayer;
import com.ftkj.manager.scout.TeamScout;
import com.ftkj.manager.system.bean.DropBean;
import com.ftkj.manager.team.Team;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.PlayerCardPB.CollectData;
import com.ftkj.proto.PlayerCardPB.CollectData.Builder;
import com.ftkj.proto.PropPB;
import com.ftkj.proto.ScoutPB;
import com.ftkj.proto.ScoutPB.ScoutPlayerMainData;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.RandomUtil;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author tim.huang 2017年3月22日 球探
 */
public class ScoutManager extends AbstractBaseManager {
    private static final Logger log = LoggerFactory.getLogger(ScoutManager.class);
    private AtomicInteger scoutVersion;
    private DropBean ranScoutDrop;
    private DropBean rollDropA;
//    private DropBean rollDropB;
    private DropBean rollDropC;
    /**顶级招募1次掉落的物品*/
    private DropBean rollDropC_1;
//    private DropBean rollDropS;
    private DropBean rollDropS2;
    private DropBean rollDropS3;
    /**顶级招募一次必中绑定底薪的掉落ID*/
    private DropBean rollDropS3_1;
    private DropBean rollDropMustA;//普通招募必中S-底薪的掉落ID,DropBean
    private DropBean rollDropMustC;//顶级招募十连必中S+底薪的掉落ID
    private DropBean rollDropMustC_1;//顶级招募1次必中S+底薪的掉落ID
//    private DropBean helpRollDrop;
    private PropBean refreshProp;
    @IOC
    private PlayerManager playerManager;
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamMoneyManager teamMoneyManager;
    @IOC
    private PropManager propManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private PlayerCardManager playerCardManager;
    @IOC
    private GymManager localArenaManager;
    @IOC
    private BuffManager buffManager;
    @IOC
    private BeSignManager beSignManager;

    private static final int roll_A = 1;// 普通
//    private static final int roll_B = 2;// 高级
    /**顶级招募10次*/
    private static final int roll_C = 3;
    /**顶级招募1次*/
    private static final int roll_C_1 = 4;
    private static List<Integer> roll_A_Times;// 普通招募次数,普通招募必中底薪需要的累计次数（必中之后是额外获得）
    private static int roll_A_Max_Times;// 普通招募次数
    private static int roll_C_Max_Times;// 普通招募次数
    private static int roll_C_1_Max_Times;// 顶级招募1次必中底薪球员的最大招募次数
    private static List<Integer> roll_C_Times;// 顶级招募次数
    /**顶级招募1次的累计次数*/
    private static List<Integer> roll_C_1_Times;
    /**顶级招募一次需要绑定球员的品质要求集合*/
    private static List<String> roll_C_1_bindGradeList;
    private static int Scout_Roll_Times_A_Must;
    private static int Scout_Roll_Times_C_Must;
    private static int scout_Roll_Times_C_Must_1;//顶级招募一次必中S+底薪的必中底薪次数
    private static int _roll_A_Prop;
//    private static int _roll_B_Prop;
    private static int _roll_C_Prop;
    private static int _roll_Glod_Money;
    private static int _roll_Glod_Money_ten;
    private static int _roll_FK_Money;
    private static int _roll_FK_Money_ten;
    

    @ClientMethod(code = ServiceCode.ScoutManager_showRollPlayer)
    public void showRollPlayer() {
        long teamId = getTeamId();
        int ACount = getRollCount(teamId, roll_A);
        int CCount = getRollCount(teamId, roll_C);
        int c_1_Count = getRollCount(teamId, roll_C_1);
        ScoutFree sf = getScoutFree(teamId);
        ScoutPB.ScoutRollMain data = ScoutPB.ScoutRollMain.newBuilder()
            .addRollList(getScoutRollData(roll_A, ACount, sf.getSecond(roll_A)))
            .addRollList(getScoutRollData(roll_C, CCount, sf.getSecond(roll_C)))
            .addRollList(getScoutRollData(roll_C_1, c_1_Count, sf.getSecond(roll_C_1)))
            .build();
        sendMessage(data);
    }

    private ScoutPB.ScoutRollData getScoutRollData(int type, int count, int second) {
        return ScoutPB.ScoutRollData.newBuilder().setCount(count).setSecond(second).setType(type).build();
    }

    /**
     * 招募
     *
     * @param type
     * @param count
     */
    @ClientMethod(code = ServiceCode.ScoutManager_rollPlayer)
    public void rollPlayer(int type, int count) {
        if (count <= 0) {
            count = 1;
        } else if (count > 1) {
            count = 10;
        }
        ErrorCode ret = rollPlayer0(type, count);
        if (ret.isError()) {
            sendMessage(ScoutPlayerMainData.newBuilder().setCode(ret.code).build());
        }
    }
    
    private ErrorCode rollPlayer0(int type, int count) {
        long teamId = getTeamId();
        DropBean drop = rollDropC;// 抽取普通掉落宝
        DropBean special = rollDropS2;// 累积次数额外获得掉落包
        int rollProp = _roll_C_Prop;// 消耗道具
        List<Integer> roll_Times = roll_C_Times;
        DropBean rollDropMust = rollDropMustC;
        int scoutRollTimesMust = Scout_Roll_Times_C_Must;
        int rollMax_Times = roll_C_Max_Times;
        EPlayerGrade ePlayerGrade = EPlayerGrade.S2;
        
        if (type == roll_A) {
            drop = rollDropA;// 抽取普通掉落宝
            special = rollDropS3;// 累积次数额外获得掉落包
            rollProp = _roll_A_Prop;// 消耗道具
            roll_Times = roll_A_Times;//普通招募必中底薪需要的累计次数（必中之后是额外获得）
            rollDropMust = rollDropMustA;//普通招募必中S-底薪的掉落ID
            scoutRollTimesMust = Scout_Roll_Times_A_Must;//普通招募必中S-底薪的必中底薪次数
            rollMax_Times = roll_A_Max_Times;//普通招募必中底薪需要的累计次数（必中之后是额外获得）,当中的最大次数
            ePlayerGrade = EPlayerGrade.S1;//球员等级S-
        }else if(type == roll_C_1){//顶级一次招募修改了新的需求：独立获取配置数据，只消耗顶级招募卡，累计次数单独计算。
        	drop = rollDropC_1;
            special = rollDropS3_1;// 累积次数额外获得掉落包
            roll_Times = roll_C_1_Times;//顶级招募1次必中底薪需要的累计次数（必中之后是额外获得）
            rollDropMust = rollDropMustC_1;//顶级招募1招募必中S+底薪的掉落ID
            scoutRollTimesMust = scout_Roll_Times_C_Must_1;//顶级招募1次必中S+底薪的必中底薪次数
            rollMax_Times = roll_C_1_Max_Times;//顶级招募必中底薪需要的累计次数（必中之后是额外获得）,当中的最大次数
		}
        
        boolean helpStep = false;
        Team team = teamManager.getTeam(teamId);
        boolean free = false;
        ScoutFree sf = getScoutFree(teamId);
        if (sf.getSecond(type) <= 0 && count <= 1) {
            free = true;
        }
        
        // 如果是25级引导，招募也是免费
        boolean newbie = team.getHelp().contains("l=25_270010");
        if (!newbie) {//非新手玩家，判断代签球员数量是否超过了规定的数值
            TeamBeSignPlayer teamBeSign = beSignManager.getTeamBeSignPlayer(teamId);
            if (teamBeSign != null && teamBeSign.getBeSignList().size() >= ConfigConsole.global().scoutBeSignMaxNum) {
                return ErrorCode.Scount_BeSign_Num;
            }
        }
        
        if (team.getLevel() == 25 && newbie) {
            free = true;
        }
        
        List<ScoutPB.ScoutPlayerInfoData> list = Lists.newArrayList();
        List<PropSimple> awardList = Lists.newArrayList();
        if (!free) {//非免费招募，判断消耗的资源是否足够，足够则直接扣除，否则返回错误码
            if (type == roll_A) {
                if (!teamMoneyManager.updateTeamMoney(teamId, 0, count > 1 ? -_roll_Glod_Money_ten : -_roll_Glod_Money, 0, 0,
                    true, ModuleLog.getModuleLog(EModuleCode.球探, "招募"))) {
                    log.debug("道具数量不足无法招募{}-{}-{}", teamId, type, count);
                    return ErrorCode.Error;
                }
            }else if (type == roll_C_1) {
				if (propManager.delProp(teamId, rollProp, count, true, true) == null) {
					log.debug("道具数量不足无法招募{}-{}-{}", teamId, type, count);
					ModuleLog.getModuleLog(EModuleCode.球探, "招募");
		            return ErrorCode.Error;
				}
			} else if (type == roll_C) {
                if (!teamMoneyManager.updateTeamMoney(teamId, count > 1 ? -_roll_FK_Money_ten : -_roll_FK_Money, 0, 0, 0, true,
                    ModuleLog.getModuleLog(EModuleCode.球探, "招募"))) {
                	log.debug("道具数量不足无法招募{}-{}-{}", teamId, type, count);
                    return ErrorCode.Error;
                }
            }
        }
        List<ScoutPlayer> playerResult = Lists.newArrayList();
        PropSimple ps = null;
        ScoutPlayer player = null;
        boolean openSpecial = false;
        int oldcount = getRollCount(teamId, type);
        int curCount = oldcount;
        // 普通免费招募不算累计招募次数
        if (!(free && type == roll_A )) {
        	curCount = curCount + count;
		}
        
        for (int times : roll_Times) {//根据累计招募次数，判断是否必得底薪球员
            if (oldcount < times && curCount >= times) {
                openSpecial = true;
                break;
            }
        }
        
        DropBean rollBean = null;
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        int notGradeTimes = getRollNotGardeTimes(teamId, type);
        for (int i = 0, x = 0; i < count && x <= 200; x++) {
            if (openSpecial) {//必得底薪球员
                if (notGradeTimes + 1 >= scoutRollTimesMust) {
                    rollBean = rollDropMust;
                    saveRollNotGardeTimes(teamId, type, 0);// 重置
                } else {
                    rollBean = special;
                }
            } else {
                rollBean = drop;
            }
            ps = rollBean.roll().get(0);
            PropBean prop = PropConsole.getProp(ps.getPropId());
            if (prop == null) {
                continue;
            }
            if (!openSpecial && prop.getPropType() == EPropType.Common.getType()) {
                awardList.add(ps);
            } else {
                PropExtPlayerBean propPlayer = PropConsole.getPlayerProp(ps.getPropId());

                PlayerBean pb = PlayerConsole.getPlayerBean(propPlayer.getHeroId());
                if (playerResult.stream().filter(pl -> pl.getPlayerId() == pb.getPlayerRid()).findFirst().isPresent()) {
                    continue;// 已经存在该球员
                }
                if (pb.getTeam() == ENBAPlayerTeam.退役 || pb.getTeam() == ENBAPlayerTeam.无队) {
                    continue;
                }
                boolean isBasePrice = playerManager.isMinPricePlayer(pb.getPlayerRid(), pb.getPrice());
                if (!openSpecial && pb.getGrade().ordinal() >= EPlayerGrade.A2.ordinal() && isBasePrice
                    && RandomUtil.randInt(100) < 50) {
                    continue;// 底薪再命中一次
                }
                PlayerTalent talent = PlayerTalent.createPlayerTalent(teamId, pb.getPlayerRid(), 
                    		tp.getNewTalentId(), PlayerManager._initDrop, false);
                tp.putPlayerTalent(talent);
                talent.save();
                int price = 0;
                if (openSpecial) {
                    price = playerManager.getPlayerMinPrice(pb.getPlayerRid());
                    if (pb.getGrade() != ePlayerGrade) {//必中的球员等级不是S-或者S+,则必中底薪球员次数加1
                        saveRollNotGardeTimes(teamId, type, notGradeTimes + 1);// 保存次数
                    } else {
                        saveRollNotGardeTimes(teamId, type, 0);// 重置
                    }
                    isBasePrice = true;
                } else {
                    price = pb.getPrice();
                    isBasePrice = false;
                }
                
                boolean bind = propPlayer.isBind();
                //顶级招募判断球员是否需要绑定
                if (type == roll_C_1 && roll_C_1_bindGradeList.contains(pb.getGrade().getGrade())) {
                	bind = true;
                }   
                
                player = new ScoutPlayer(pb.getPlayerRid(), price, i, i == 5, isBasePrice, talent);
                player.setBind(bind);
                player.setOpenSpecial(openSpecial);
                playerResult.add(player);
            }
            if (openSpecial) {
                openSpecial = false;
                continue;
            }
            i++;
        }
        
        //更新招募次数，条件是：非免费招募或者免费招募而且是顶级1次招募
        if (!free || (free && type == roll_C_1)) {
        	if (curCount >= rollMax_Times) {
                saveRollCount(teamId, type, 0);// 重置累积次数
            } else {
                saveRollCount(teamId, type, curCount);
            }
		}

        if (!helpStep) {
            if (awardList.size() > 0) {
                propManager.addPropList(teamId, awardList, true, ModuleLog.getModuleLog(EModuleCode.招募, "招募获得"));
            }
            if (playerResult.size() > 0) {
                beSignManager.addBeSignPlayers(teamId, playerResult, ModuleLog.getModuleLog(EModuleCode.招募, "招募"));
            }
            List<Integer> grades =
                playerResult.stream().map(sc -> PlayerConsole.getPlayerBean(sc.getPlayerId())).filter(pb -> pb != null)
                    .map(pb -> pb.getGrade().ordinal()).collect(Collectors.toList());
            for (int grade : grades)
                taskManager.updateTask(teamId, ETaskCondition.招募球员, 1, "" + grade);
        }
        if (free) {
            sf.updateLevel(type);
            saveScoutFree(teamId, sf);
        }
        Collections.shuffle(playerResult);
        playerResult.forEach(pp -> list.add(getScoutPlayerInfoData(pp, pp.getTalent())));
        // 发送事件
        EventBusManager.post(EEventType.招募, new ScountParam(teamId, type, count));
        List<PropPB.PropSimpleData> propLists = PropManager.getPropSimpleListData(awardList);
        sendMessage(ScoutPlayerMainData.newBuilder().addAllPlayers(list).addAllAwardList(propLists).setCode(0).build());

        return ErrorCode.Success;
    }

    private ScoutPB.ScoutPlayerInfoData getScoutPlayerInfoData(ScoutPlayer pp, PlayerTalent pt) {
        return ScoutPB.ScoutPlayerInfoData.newBuilder()
            .setPlayerId(pp.getPlayerId())
            .setIsBasePrice(pp.isBasePrice())
            .setPrice(pp.getPrice())
            .setOpenSpecial(pp.isOpenSpecial())
            .setTalent(PlayerManager.getPlayerTalentData(pt))
            .setBind(pp.isBind())
            .build();
    }
    
    private int getRollCount(long teamId, int type) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Roll_Count) + "_" + type;
        int count = redis.getIntNullIsZero(key);
        return count;
    }
    
    private void saveRollCount(long teamId, int type, int count) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Roll_Count) + "_" + type;
        redis.set(key, count + "");
    }
    
    private int getRollNotGardeTimes(long teamId, int type) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Roll_Not_Garde_Times) + "_" + type;
        int count = redis.getIntNullIsZero(key);
        return count;
    }
    
    private void saveRollNotGardeTimes(long teamId, int type, int count) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Roll_Not_Garde_Times) + "_" + type;
        redis.set(key, count + "");
    }

    private ScoutFree getScoutFree(long teamId) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Free);
        ScoutFree sf = redis.getObj(key);
        if (sf == null) {
            sf = new ScoutFree();
            saveScoutFree(teamId, sf);
        }
        return sf;
    }

    private void saveScoutFree(long teamId, ScoutFree sf) {
        String key = RedisKey.getKey(teamId, RedisKey.Scout_Free);
        redis.set(key, sf);
    }

    @Override
    public void instanceAfter() {
        this.scoutVersion = new AtomicInteger();
    }

    @Override
    public void initConfig() {
        refreshProp = PropConsole.getProp(ConfigConsole.getIntVal(EConfigKey.SCOUT_NUM));
        ranScoutDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Drop_Scout_Player));
        rollDropA = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_A));
//        rollDropB = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_B));
        rollDropC = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_C));
        rollDropC_1 = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_C_1));
//        helpRollDrop = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_Help));
        _roll_A_Prop = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_A);
//        _roll_B_Prop = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_B);
        _roll_C_Prop = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Prop_C);

//        rollDropS = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_S));
        rollDropS2 = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_S2));
        rollDropS3 = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_S3));
        rollDropS3_1 = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_S2_1));
        getRanScoutPlayer(0, false);

        _roll_Glod_Money = ConfigConsole.getIntVal(EConfigKey.Recruit_Need_1);
        _roll_Glod_Money_ten = ConfigConsole.getIntVal(EConfigKey.Recruit_Need_10);
        _roll_FK_Money = ConfigConsole.getIntVal(EConfigKey.Top_Recruit_Need_1);
        _roll_FK_Money_ten = ConfigConsole.getIntVal(EConfigKey.Top_Recruit_Need_10);

        roll_C_Times =
            Arrays.stream(ConfigConsole.getVal(EConfigKey.Scout_Roll_Times_C).split(",")).mapToInt(n -> Integer.valueOf(n))
                .boxed().collect(Collectors.toList());
        roll_A_Times =
            Arrays.stream(ConfigConsole.getVal(EConfigKey.Scout_Roll_Times_A).split(","))
                .filter(s -> s != null && !s.isEmpty()).mapToInt(n -> Integer.valueOf(n)).boxed()
                .collect(Collectors.toList());
        roll_C_1_Times =
                Arrays.stream(ConfigConsole.getVal(EConfigKey.Scout_Roll_Times_C_1).split(",")).mapToInt(n -> Integer.valueOf(n))
                    .boxed().collect(Collectors.toList());
        roll_C_1_bindGradeList = 
        		Arrays.stream(ConfigConsole.getVal(EConfigKey.Scout_Roll_Drop_C_1_Bind_Grade).split(",")).collect(Collectors.toList());
        roll_A_Max_Times = findMaxValue(roll_A_Times);
        roll_C_Max_Times = findMaxValue(roll_C_Times);
        roll_C_1_Max_Times = findMaxValue(roll_C_1_Times);
        rollDropMustA = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_A_Must));
        rollDropMustC = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_C_Must));
        rollDropMustC_1 = DropConsole.getDrop(ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Drop_C_Must_1));
        Scout_Roll_Times_A_Must = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Times_A_Must);
        Scout_Roll_Times_C_Must = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Times_C_Must);
        scout_Roll_Times_C_Must_1 = ConfigConsole.getIntVal(EConfigKey.Scout_Roll_Times_C_Must_1);
    }

//    public void findMaxValueA(List<Integer> lists) {
//        for (int value : lists) {
//            if (roll_A_Max_Times < value) {
//
//                roll_A_Max_Times = value;
//            }
//        }
//    }
//
//    public void findMaxValueC(List<Integer> lists) {
//        for (int value : lists) {
//            if (roll_C_Max_Times < value) {
//
//                roll_C_Max_Times = value;
//            }
//        }
//    }
    
    public int findMaxValue(List<Integer> lists) {
    	int maxNum = 0;
        for (int value : lists) {
            if (maxNum < value) {
            	maxNum = value;
            }
        }
        
        return maxNum;
    }
    
    

    /**
     * 主界面
     */
    @ClientMethod(code = ServiceCode.ScoutManager_showScoutMain)
    public void showScoutMain() {
        long teamId = getTeamId();
        TeamScout team = getScout(teamId);
        sendMessage(getScoutMain(team));
    }

    /**
     * 制卡
     *
     * @param index
     */
    @ClientMethod(code = ServiceCode.ScoutManager_MakeCard)
    public void makeCard(int index) {
        long teamId = getTeamId();
        TeamScout ts = getScout(teamId);
        ScoutPlayer player = ts.getPlayer(index);
        if (player == null) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;// 签约的球员不存在
        }
        if (player.getStatus() == EStatus.ScoutSign) {
            sendMessage(CollectData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;// 签约的球员不存在
        }
        setScout(teamId, ts);
        player.sign();
        // 收集
        Builder builder = playerCardManager.markCard(teamId, new int[]{player.getPlayerId()});
        sendMessage(builder.setCode(ErrorCode.Success.code).build());
    }

    /**
     * 签约接口
     *
     * @param index
     */
    @ClientMethod(code = ServiceCode.ScoutManager_signPlayer)
    @Deprecated
    public void signPlayer(int index) {
        long teamId = getTeamId();
        TeamScout ts = getScout(teamId);
        ScoutPlayer player = ts.getPlayer(index);
        if (player == null || player.getStatus() == EStatus.ScoutSign) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;// 签约的球员不存在
        }
        ErrorCode StatusCode =
            signPlayer(teamId, player.getPlayerId(), player.getPrice(), 0, player.isBind(), ModuleLog.getModuleLog(EModuleCode.球探, ""));
        if (StatusCode == ErrorCode.Success) {
            player.sign();
            taskManager.updateTask(teamId, ETaskCondition.球探签约, 1, PlayerConsole.getPlayerBean(player.getPlayerId())
                .getGrade().getGrade());
            setScout(teamId, ts);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(StatusCode.code).build());
    }

    /**
     * 制作球员之魂
     *
     * @param index
     */
    @ClientMethod(code = ServiceCode.ScoutManager_makeArenaPlayer)
    public void makeArenaPlayer(int index) {
        long teamId = getTeamId();
        TeamScout ts = getScout(teamId);
        ScoutPlayer player = ts.getPlayer(index);
        if (player == null || player.getStatus() == EStatus.ScoutSign) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Player_Null.code).build());
            return;// 签约的球员不存在
        }
        if (!teamMoneyManager.updateTeamMoney(teamId, 0, -player.getPrice(), 0, 0, true,
            ModuleLog.getModuleLog(EModuleCode.球探, "制作球员之魂"))) {
            // 经费不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Money_0.code).build());
            return;
        }
        player.sign();
        int pid = localArenaManager.addPlayerLineup(teamId, player.getPlayerId());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg("" + pid).build());
    }

    /**
     * 签约公用接口，领取进阵容的都可以走这一套
     *
     * @param teamId
     * @param playerId
     * @param price
     * @param modulCode
     */
    public ErrorCode signPlayer(long teamId, int playerId, int price, int tid, ModuleLog modulCode) {
        return signPlayer(teamId, playerId, price, tid, false, modulCode);
    }

    /**
     * 签约公用接口，领取进阵容的都可以走这一套
     */
    ErrorCode signPlayer(long teamId, int playerId, int price, int tid, boolean bind, ModuleLog modulCode) {
        TeamPlayer tp = playerManager.getTeamPlayer(teamId);
        return playerManager.addPlayerAuto(teamId, playerId, price, tp.getPlayerTalent(tid), bind, modulCode);
    }

    /**
     * 免费次数
     *
     * @return
     */
    private int getFreeCount(long teamId) {
        // int count = buffManager.getBuffSet(teamId, EBuffType.球探免费刷新次数).getValueSum().intValue();
        String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Scount_Free_Frush_Count);
        int use = redis.getIntNullIsZero(key);
        // return count - use;
        return use;
    }

    /**
     * 刷新
     */
    @ClientMethod(code = ServiceCode.ScoutManager_refreshScout)
    public void refreshScout() {
        long teamId = getTeamId();
        TeamScout ts = getScout(teamId);
        // 免费次数
        int freeCount = getFreeCount(teamId);
        if (freeCount > 0) {
            String key = RedisKey.getDayKey(teamId, RedisKey.VIP_Scount_Free_Frush_Count);
            redis.set(key, (redis.getIntNullIsZero(key) + 1) + "", RedisKey.DAY);
        } else if (propManager.delProp(teamId, refreshProp.getPropId(), 1, true, true) == null) {
            sendMessage(getScoutMain(ts));
            return;
        }
        ts.updatePlayer(getRanScoutPlayer(teamId, false), getVersion());
        setScout(teamId, ts);
        sendMessage(getScoutMain(ts));
    }

    private ScoutPB.ScoutMain getScoutMain(TeamScout team) {
        List<ScoutPB.ScoutPlayerData> playerList = Lists.newArrayList();
        team.getPlayers().forEach(player -> playerList.add(getScoutPlayerData(player)));
        //
        DateTime next = DateTime.now().plusHours(1).withMinuteOfHour(0).withSecondOfMinute(0);
        int refTime = DateTimeUtil.secondBetween(DateTime.now(), next);
        return ScoutPB.ScoutMain.newBuilder().setCode(0).setRefTime(refTime).addAllPlayerList(playerList).build();
    }

    private ScoutPB.ScoutPlayerData getScoutPlayerData(ScoutPlayer player) {
        return ScoutPB.ScoutPlayerData.newBuilder()
            .setPlayerId(player.getPlayerId())
            .setPrice(player.getPrice())
            .setStatus(player.getStatus().getId())
            .setIndex(player.getIndex())
            .setVip(player.isVip())
            .setBind(player.isBind())
            .build();
    }

    private int getVersion() {
        return this.scoutVersion.get();
    }

    public void updateVersion() {
        this.scoutVersion.incrementAndGet();
    }

    private TeamScout getScout(long teamId) {
        TeamScout ts = redis.getObj(RedisKey.getKey(teamId, RedisKey.Team_Scout));
        if (ts == null) {// 玩家无球探数据，实例化一个
            ts = new TeamScout();
        }
        if (ts.getVersion() != getVersion()) {// 当前球探版本与玩家球探版本不匹配,更新新的球探数据s
            ts.updatePlayer(getRanScoutPlayer(teamId, true), getVersion());
            setScout(teamId, ts);
        }
        return ts;
    }

    private void setScout(long teamId, TeamScout ts) {
        redis.set(RedisKey.getKey(teamId, RedisKey.Team_Scout), ts);
    }

    private List<ScoutPlayer> getRanScoutPlayer(long teamId, boolean system) {
        List<ScoutPlayer> result = Lists.newArrayList();
        PropSimple ps = null;
        ScoutPlayer player = null;
        for (int i = 0; i < 6; ) {
            ps = ranScoutDrop.roll().get(0);
            //
            PropBean propB = PropConsole.getProp(ps.getPropId());
            if (propB == null) {
                continue;
            }
            if (propB.getType() != EPropType.Player && propB.getType() != EPropType.Wrap_Player) {
                continue;
            }
            PropExtPlayerBean prop = PropConsole.getPlayerProp(propB);
            PlayerBean pb = PlayerConsole.getPlayerBean(prop.getHeroId());
            if (result.stream().filter(pl -> pl.getPlayerId() == pb.getPlayerRid()).findFirst().isPresent()) {
                continue;// 已经存在该球员
            }
            if (pb.getTeam() == ENBAPlayerTeam.退役 || pb.getTeam() == ENBAPlayerTeam.无队) {
                continue;
            }
            boolean isBasePrice = playerManager.isMinPricePlayer(pb.getPlayerRid(), pb.getPrice());
            if (pb.getGrade().ordinal() >= EPlayerGrade.A2.ordinal() && isBasePrice && RandomUtil.randInt(100) < 50) {
                continue;// 底薪再命中一次
            }
            if (system && pb.getGrade().ordinal() > EPlayerGrade.A2.ordinal() && teamManager.getTeam(teamId).getLevel() <= 20) {
                continue;// 系统刷新,刷不出A+以上球员
            }

            player = new ScoutPlayer(pb.getPlayerRid(), pb.getPrice(), i, i == 5, isBasePrice, null);
            player.setBind(prop.isBind());
            result.add(player);
            i++;
        }
        return result;
    }

}
