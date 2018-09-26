package com.ftkj.manager.battle;

import com.ftkj.cfg.battle.BattleStepBean;
import com.ftkj.db.domain.BattleInfoPO;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.TacticType;
import com.ftkj.enums.battle.EBattleAttribute;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.manager.battle.model.ActionReport;
import com.ftkj.manager.battle.model.BattleBuffer;
import com.ftkj.manager.battle.model.BattlePlayer;
import com.ftkj.manager.battle.model.BattlePosition;
import com.ftkj.manager.battle.model.BattleProp;
import com.ftkj.manager.battle.model.BattleSource;
import com.ftkj.manager.battle.model.BattleStat;
import com.ftkj.manager.battle.model.BattleTactics;
import com.ftkj.manager.battle.model.BattleTeam;
import com.ftkj.manager.battle.model.EndReport;
import com.ftkj.manager.battle.model.EndReport.AllStarMatchEnd;
import com.ftkj.manager.battle.model.EndReport.ArenaMatchEnd;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd;
import com.ftkj.manager.battle.model.EndReport.RankedMatchEnd.RankedTeam;
import com.ftkj.manager.battle.model.EndReport.StarletMatchEnd;
import com.ftkj.manager.battle.model.EndReport.TeamReport;
import com.ftkj.manager.battle.model.PlayerActStat;
import com.ftkj.manager.battle.model.RoundReport;
import com.ftkj.manager.battle.model.RoundReport.ReportTeam;
import com.ftkj.manager.battle.model.Skill;
import com.ftkj.manager.coach.CoachSkillBean;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.manager.team.TeamBattleStatus;
import com.ftkj.proto.BattlePB;
import com.ftkj.proto.BattlePB.BADAllStar;
import com.ftkj.proto.BattlePB.BADArena;
import com.ftkj.proto.BattlePB.BADArena.Builder;
import com.ftkj.proto.BattlePB.BADRanked;
import com.ftkj.proto.BattlePB.BADRankedTeam;
import com.ftkj.proto.BattlePB.BattleAdditionalData;
import com.ftkj.proto.BattlePB.BattleBufferData;
import com.ftkj.proto.BattlePB.BattlePKTeamData;
import com.ftkj.proto.BattlePB.BattlePlayerRoundData;
import com.ftkj.proto.BattlePB.BattlePlayerStatResp;
import com.ftkj.proto.BattlePB.BattlePropData;
import com.ftkj.proto.BattlePB.BattleRoundActionData;
import com.ftkj.proto.BattlePB.BattleStartData;
import com.ftkj.proto.BattlePB.TeamRedix;
import com.ftkj.proto.CommonPB.BattleHisResp;
import com.ftkj.proto.CommonPB.TeamSimpleData;
import com.ftkj.proto.GameLogPB;
import com.ftkj.proto.GameLogPB.BattleEndTeamResp;
import com.ftkj.proto.GameLogPB.BattlePlayerSourceData;
import com.ftkj.proto.TeamPB;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BattlePb {
    private static final Logger log = LoggerFactory.getLogger(BattlePb.class);

    private static BattlePB.BattleLineupTacticsData lineupTactics(BattleTactics ot,
                                                                  BattleTactics dt) {
        return BattlePB.BattleLineupTacticsData.newBuilder()
            .setFs(dt.getTactics().getId().getId())
            .setJg(ot.getTactics().getId().getId())
            .build();
    }

    private static BattlePB.BattleBufferData buff(BattleBuffer buff) {
        return BattlePB.BattleBufferData.newBuilder()
            .setTeamId(buff.getTeamId())
            .setBid(buff.getBuffer().getBid())
            .setEndRound(buff.getEndRound())
            .setVal(buff.getVal())
            .build();
    }

    private static BattlePB.BattlePlayerSkillData playerSkill(BattlePlayer bp) {
        Skill attack = bp.getPlayerSkill().getAttack();
        Skill defend = bp.getPlayerSkill().getDefend();
        return BattlePB.BattlePlayerSkillData.newBuilder()
            .setAttackSkillId(attack == null ? 0 : attack.getSkill().getSkillId())
            .setAttackSkillLevel(attack == null ? 0 : attack.getLevel())
            .setDefendSkillId(defend == null ? 0 : defend.getSkill().getSkillId())
            .setDefendSkillLevel(defend == null ? 0 : defend.getLevel())
            .setSkillPower(bp.getPlayerSkill().getSkillPower())
            .build();
    }

    public static BattlePB.BattleCoachSkillData coachSkill(long teamId, CoachSkillBean skillBean,
                                                           List<BattleBuffer> datas) {
        List<BattlePB.BattleBufferData> bufferDataList = datas.stream().map(buff -> buff(buff))
            .collect(Collectors.toList());
        return BattlePB.BattleCoachSkillData.newBuilder()
            .setTeamId(teamId)
            .setSkillId(skillBean.getSid())
            .addAllBuffList(bufferDataList)
            .build();
    }

    //

    static BattlePB.BattleMainData battleMainData(long teamId, boolean isBattleTeam, BattleSource source) {
        BattlePB.BattleInfoData infoData = battleInfoData(source);
        BattlePB.BattleMainData.Builder build = BattlePB.BattleMainData.newBuilder();
        build.setBattleInfo(infoData);
        if (isBattleTeam) {
            BattlePB.BattleTeamData teamData = team(source.getTeam(teamId));
            build.setTeamData(teamData);
        }

        if (source.getInfo().getBattleType() == EBattleType.擂台赛) {
            CustomPVPRoom room = source.getAttributeMap(0).getVal(EBattleAttribute.擂台赛房间);
            build.setCustomData(customData(room));
        }

        return build.build();
    }

    public static BattlePB.BattleCustomData customData(CustomPVPRoom room) {
        return BattlePB.BattleCustomData.newBuilder()
            .setGuessType(room.getGuessType().ordinal())
            .setHomeTeamId(room.getHomeTeam().getTeamId())
            .setLevelCodition(room.getLevelCondition())
            .setPkType(room.getPkType().ordinal())
            .setPositionCondition(room.getPositionCondition().getId())
            .setPowerCondition(room.getPowerCondition())
            .setRoomId(room.getRoomId())
            .setRoomMoney(room.getRoomMoney())
            .setRoomScore(room.getRoomScore())
            .setRoomStatus(room.getRoomStatus().ordinal())
            .setRoomTip(room.getRoomTip())
            .setWinCondition(room.getWinCondition().getType())
            .addAllStepCondition(room.getStepConditions().stream().map(EBattleStep::ordinal)
                .collect(Collectors.toList())).build();

    }

    private static BattlePB.BattleInfoData battleInfoData(BattleSource bs) {
        Skill home = bs.getRound().getSkill().getHomeSkill();
        Skill away = bs.getRound().getSkill().getAwaySkill();

        List<BattlePB.BattleStepData> stepDataList = Lists.newArrayList();
        for (BattleStepBean sb : bs.getStepConfig()) {
            stepDataList.add(step(sb.getStep(), sb.getRound()));
        }

        BattlePKTeamData ht = pKTeamData(bs.getHome(), bs.getReport().getHome(),
            home == null ? 0 : home.getSkill().getSkillId(), bs.getRound().getCurRound());
        BattlePKTeamData at = pKTeamData(bs.getAway(), bs.getReport().getAway(),
            away == null ? 0 : away.getSkill().getSkillId(), bs.getRound().getCurRound());
        return BattlePB.BattleInfoData.newBuilder()
            .setBattleId(bs.getInfo().getBattleId())
            .setBattleType(bs.getInfo().getBattleType().getId())
            .setHomeData(ht)
            .setAwayData(at)
            .addAllBattleStep(stepDataList)
            .setHomeTeamId(bs.getInfo().getHomeTeamId())
            .setAllRound(bs.getRound().getCurRound())
            .setStep(bs.getRound().getCurStep().ordinal())
            .setRound(bs.getReport().getRoundOfStep())
            .build();
    }

    public static BattlePB.BattleStepData step(EBattleStep step, int stepRound) {
        return BattlePB.BattleStepData.newBuilder().setStep(step.ordinal()).setTotal(stepRound)
            .build();
    }

    static BattlePB.BattlePKTeamData pKTeamData(ReportTeam rt, BattleTeam team, int curRound) {
        return pKTeamData(team, rt, 0, player(team), curRound);
    }

    static BattlePB.BattlePKTeamData pKTeamData(BattleTeam team, ReportTeam rt, int skillId,
                                                int curRound) {
        List<BattlePB.BattlePlayerData> players = player(team);
        return pKTeamData(team, rt, skillId, players, curRound);
    }

    private static List<BattleBufferData> buff(BattleTeam team, int curRound) {
        List<BattleBufferData> buffListsData = new ArrayList<>();
        for (BattleBuffer buf : team.getBuffers()) {
            if (buf.getEndRound() >= curRound) {
                BattleBufferData battleBufferData = buff(buf);
                buffListsData.add(battleBufferData);
            }
        }
        return buffListsData;
    }

    private static BattlePB.BattlePKTeamData pKTeamData(BattleTeam team,
                                                        ReportTeam rt,
                                                        int skillId,
                                                        List<BattlePB.BattlePlayerData> players,
                                                        int curRound) {
        return BattlePB.BattlePKTeamData.newBuilder()
            .setTeamId(team.getTeamId())
            .setTeamName(team.getName())
            .setTeamLogo(team.getLogo())
            .setLevel(team.getLevel())
            .setLeagueName("")
            .setAttackCap(rt.getOffenseCap())
            .setDefendCap(rt.getDefenseCap())
            .setSkillId(skillId)
            .addAllLineup(players)
            .addAllBuffList(buff(team, curRound))
            .setCoachId(team.getCoach() != null ? team.getCoach().getcId() : 0)
            .setTactics(lineupTactics(team.getPkTactics(TacticType.Offense),
                team.getPkTactics(TacticType.Defense)))
            .setScore(team.getScore())
            .build();
    }

    private static List<BattlePB.BattlePlayerData> player(BattleTeam team) {
        List<BattlePB.BattlePlayerData> data = Lists.newArrayList();
        //首发球员
        for (BattlePosition bp : team.getLineupPlayers().values()) {
            data.add(player(bp.getPlayer(), bp.getPosition()));
        }
        //替补球员
        for (BattlePlayer player : team.getPlayers()) {
            if (!player.isLineupPos()) {
                data.add(player(player, EPlayerPosition.NULL));
            }
        }
        return data;
    }

    private static BattlePB.BattlePlayerData player(BattlePlayer bp, EPlayerPosition pos) {
        //		log.error("Player Cap:{}-{}",bp.getPlayerId(),bp.getBaseCap());
        return BattlePB.BattlePlayerData.newBuilder()
            .setCap(bp.getBaseCap())
            .setPlayerId(bp.getPlayerId())
            .setPower((int) bp.getPower())
            .setPosition(pos.getId())
            .setPlayerPosition(bp.getPlayerPosition().getId())
            .setHonorLogo(bp.getHonorLogo())
            .setHonorLogoQuality(bp.getHonorLogoQuality())
            .setSkill(playerSkill(bp))
            .setMaxSkillPower(bp.getPlayerSkill().getMaxSkillPower())
            .addAllStat(playerStat(bp))
            .build();
    }

    private static List<BattlePlayerStatResp> playerStat(BattlePlayer bp) {
        return Collections.singletonList(playerStat(bp, EActionType.pf));
    }

    private static BattlePlayerStatResp playerStat(BattlePlayer bp, EActionType act) {
        BattlePlayerStatResp.Builder stat = BattlePlayerStatResp.newBuilder();
        stat.setType(act.getType());
        stat.setValue(bp.getRealTimeActionStats().getIntValue(act));
        return stat.build();
    }

    private static BattlePB.BattleTeamData team(BattleTeam team) {
        //		List<BattlePB.BattlePlayerData> playerListData = getBattlePlayerDataList(team, true);
        List<BattlePB.BattleTacticsData> tacticsListData = Lists.newArrayList();
        team.getTactics().values().forEach(tactics -> tacticsListData.add(tactics(tactics)));
        //
        return BattlePB.BattleTeamData.newBuilder()
            //			.addAllPlayers(playerListData)
            //                .addAllProps(propListData)
            .setBattleStat(stat(team.getStat()))
            .setTacticsData(lineupTactics(team.getPkTactics(TacticType.Offense),
                team.getPkTactics(TacticType.Defense)))
            .addAllTacticsList(tacticsListData).build();
    }

    private static BattlePB.BattlePropData prop(BattleProp bp) {
        return BattlePB.BattlePropData.newBuilder()
            .setPid(bp.getProp().getPropId())
            .setNum(bp.getNum())
            .build();
    }

    /** 士气情况 */
    private static BattlePB.BattleStatData stat(BattleStat stat) {
        //		log.debug("{}-士气情况{}", teamId,stat.getReplaceTacticCD());
        List<BattlePB.BattleCoachCDData> coachCDList = Lists.newArrayList();
        stat.getCoachCDMap().forEach((key, val) -> coachCDList.add(coachCd(key, val)));

        return BattlePB.BattleStatData.newBuilder()
            .setPropCD(stat.getPropCD())
            .setMorale(stat.getMorale())
            .addAllCoachCounts(coachCDList)
            .setCoachSecond(stat.getCoachCD())
            .setPlayerCD(stat.getReplacePlayerCD())
            .setTacticsCD(stat.getReplaceTacticCD())
            .setTacticNum(stat.getUseTacticsNum())
            .build();
    }

    private static BattlePB.BattleCoachCDData coachCd(int sid, int cd) {
        return BattlePB.BattleCoachCDData.newBuilder().setCd(cd).setSid(sid).build();
    }

    private static BattlePB.BattleTacticsData tactics(BattleTactics tac) {
        return BattlePB.BattleTacticsData.newBuilder()
            .setTid(tac.getTactics().getId().getId())
            .build();
    }

    /** 比赛回合数据 */
    public static BattlePB.BattleRoundMainData battleRoundMainData(BattleSource bs,
                                                                   RoundReport report) {
        try {
            BattleTeam home = bs.getHome();
            BattleTeam away = bs.getAway();
            EBattleStep step = report.getStep();
            List<BattleRoundActionData> subacts = roundAction(report);
            if (log.isDebugEnabled()) {
                log.debug("btget round data. bid {} htid {} atid {} round {} step {} score {}:{} gap {}" +
                        " act {} subactnum {} hints {} home {} away {} delay {}",
                    bs.getId(), home.getTeamId(), away.getTeamId(), bs.getRound().getCurRound(),
                    step.getStepName(),
                    home.getScore(), away.getScore(), Math.abs(home.getScore() - away.getScore()),
                    report.getAction().name(), subacts.size(),
                    report.getHints().size(), report.getHome(), report.getAway(),
                    bs.getRound().getRoundDelay());
            }

            BattlePB.BattleRoundMainData.Builder resp = BattlePB.BattleRoundMainData.newBuilder()
                .setBattleId(bs.getBattleId())
                .setBattleType(bs.getInfo().getBattleType().getId())
                .setStep(step.ordinal())
                .setRound(report.getRoundOfStep())
                .setHomeScore(report.getHomeScore())
                .setAwayScore(report.getAwayScore())
                .setHomeAttackCap(report.getHome().getOffenseCap())
                .setHomeDefendCap(report.getHome().getDefenseCap())
                .setAwayAttackCap(report.getAway().getOffenseCap())
                .setAwayDefendCap(report.getAway().getDefenseCap())
                .setActionType(report.getAction().ordinal())
                .addAllActionList(subacts) //回合行为信息
                .setHomeBattleStat(stat(home.getStat())) //士气情况
                .setAwayBattleStat(stat(away.getStat())) //士气情况
                .addAllHomePlayerData(playerRound(home)) //回合所有球员体力
                .addAllAwayPlayerData(playerRound(away)) //回合所有球员体力
                .setAllRound(bs.getRound().getCurRound());
            resp.addAllHints(report.getHints());
            return resp.build();
        } catch (Exception e) {
            log.error("测试报错战报. msg " + report.toString() + " msg " + e.getMessage(), e);
        }
        return null;
    }

    /** 回合所有球员体力 技能能量 */
    private static List<BattlePlayerRoundData> playerRound(BattleTeam bt) {
        return bt.getPlayers().stream()
            .map(BattlePb::playerRound).collect(Collectors.toList());
    }

    /** 回合所有球员体力 技能能量 */
    private static BattlePB.BattlePlayerRoundData playerRound(BattlePlayer player) {
        //		log.debug("回合所有球员体力信息:{}-[{}]-[{}]-技能能量-[{}]", player.getPlayerId(),player.getPower()
        //				,Math.round(player.getPower()),player.getPlayerSkill().getSkillPower());
        return BattlePB.BattlePlayerRoundData
            .newBuilder()
            .setPlayerId(player.getPlayerId())
            .setSkillPower(player.getPlayerSkill().getSkillPower())
            .setPower(Math.round(player.getPower()))
            .build();
    }

    /** 回合行为信息 */
    private static List<BattleRoundActionData> roundAction(RoundReport report) {
        List<BattleRoundActionData> roundActionList = Lists.newArrayList();
        while (report.getActionReportQueue().peek() != null) {
            ActionReport ar = report.getActionReportQueue().poll();
            if (ar == null) {
                continue;
            }
            roundActionList.add(roundAction(ar));
        }
        return roundActionList;
    }

    /** 回合行为信息 */
    private static BattlePB.BattleRoundActionData roundAction(ActionReport ar) {
        //        log.debug("回合行为信息:[{}]", ar);
        return BattlePB.BattleRoundActionData.newBuilder()
            .setActionId(ar.getType().getType())
            .setTeamId(ar.getTeamId())
            .setPlayerId(ar.getPlayerId())
            .setPower(ar.getPower())
            .setV1(ar.getV1())
            .setV2(ar.getV2())
            .setV3(ar.getV3())
            .setIsForce(ar.isForce()==true?1:0)
            .build();
    }

    public static BattlePB.BattleEndMainData battleEndMainData(long battleId, EndReport endReport) {
        return BattlePB.BattleEndMainData.newBuilder()
            .setEndInfo(endInfo(endReport))
            .setBattleId(battleId)
            .setWinTeamId(endReport.getWinTeamId())
            .build();
    }

    private static BattlePB.BattlePropData prop(PropSimple ps) {
        return BattlePB.BattlePropData.newBuilder().setPid(ps.getPropId()).setNum(ps.getNum())
            .build();
    }

    private static BattlePB.BattleEndInfoData endInfo(EndReport report) {
        PlayerActStat hpts = report.sortAndGetFirst(true, EActionType.pts);
        PlayerActStat apts = report.sortAndGetFirst(false, EActionType.pts);
        boolean pts = hpts.getValue(EActionType.pts) > apts.getValue(EActionType.pts);

        PlayerActStat hast = report.sortAndGetFirst(true, EActionType.ast);
        PlayerActStat aast = report.sortAndGetFirst(false, EActionType.ast);
        boolean ast = hast.getValue(EActionType.ast) > aast.getValue(EActionType.ast);

        PlayerActStat hreb = report.sortAndGetFirst(true, EActionType.reb);
        PlayerActStat areb = report.sortAndGetFirst(false, EActionType.reb);
        boolean reb = hreb.getValue(EActionType.reb) > areb.getValue(EActionType.reb);

        PlayerActStat hmvp = report.getMVPPlayer(true);
        PlayerActStat amvp = report.getMVPPlayer(false);

        return BattlePB.BattleEndInfoData.newBuilder()
            .setHomeTeam(endTeam(report.getHome(), prop(report, report.getHomeTeamId())))
            .setAwayTeam(endTeam(report.getAway(), prop(report, report.getAwayTeamId())))
            .setDfw(endPlayer(pts ? report.getHomeTeamId() : report.getAwayTeamId(), pts ? hpts : apts))
            .setZgw(endPlayer(ast ? report.getHomeTeamId() : report.getAwayTeamId(), ast ? hast : aast))
            .setLbw(endPlayer(reb ? report.getHomeTeamId() : report.getAwayTeamId(), reb ? hreb : areb))
            .setMvp(endPlayer(report.getWinTeamId(), report.getWinTeamId() == report.getHomeTeamId() ? hmvp : amvp))
            .setAdditional(additionData(report))
            .build();
    }

    private static List<BattlePropData> prop(EndReport report, long homeTeamId) {
        return report.getAwardList(homeTeamId).stream()
            .map(BattlePb::prop).collect(Collectors.toList());
    }

    /** 比赛结束附加消息 */
    private static BattleAdditionalData additionData(EndReport report) {
        BattleAdditionalData.Builder resp = BattleAdditionalData.newBuilder();
        if (report.getAdditional() == null || report.getAdditional().isEmpty()) {
            return resp.build();
        }
        if (report.getBattleType() == EBattleType.Main_Match_Normal
            || report.getBattleType() == EBattleType.Main_Match_Championship || report.getBattleType() == EBattleType.honorMatch) {
            Integer mainmathStar = report.getAdditional(EBattleAttribute.Main_Match_Star);
            if (mainmathStar != null) {
                resp.setStar(mainmathStar);
            }
        } else if (report.getBattleType() == EBattleType.Ranked_Match) {
            RankedMatchEnd rme = report.getAdditional(EBattleAttribute.Ranked_Match_End);
            if (rme != null) {
                BADRanked ranked = BADRanked.newBuilder()
                    .setHome(rankedTeam(rme.getHome()))
                    .setAway(rankedTeam(rme.getAway()))
                    .build();
                resp.setRanked(ranked);
            }
        } else if (report.getBattleType() == EBattleType.AllStar) {
            AllStarMatchEnd asme = report.getAdditional(EBattleAttribute.All_Star_Match_End);
            if (asme != null) {
                BADAllStar as = BADAllStar.newBuilder()
                    .setSrcLev(asme.getSrcLev())
                    .setCurrLev(asme.getCurrLev())
                    .setSrcTotalHp(asme.getSrcTotalHp())
                    .setSubTotalHp(asme.getSubTotalHp())
                    .build();
                resp.setAllStar(as);
            }
        } else if (report.getBattleType() == EBattleType.Arena) {
            ArenaMatchEnd arend = report.getAdditional(EBattleAttribute.Arena_Match_End);
            if (arend != null) {
                Builder arresp = BADArena.newBuilder();
                if (arend.getSelfRank() > 0) {
                    arresp.setSelfRank(arend.getSelfRank());
                }
                if (arend.getTargetRank() > 0) {
                    arresp.setTargetRank(arend.getTargetRank());
                }
                if (arend.getOldMaxRank() > 0) {
                    arresp.setOldMaxRank(arend.getOldMaxRank());
                }
                if (arend.getNewMaxRank() > 0) {
                    arresp.setNewMaxRank(arend.getNewMaxRank());
                }
                if (arend.getMaxRankAward() > 0) {
                    arresp.setMaxRankAward(arend.getMaxRankAward());
                }
                resp.setArena(arresp.build());
            }
        } else if (report.getBattleType() == EBattleType.Starlet_Dual_Meet) {
            StarletMatchEnd smend = report.getAdditional(EBattleAttribute.Starlet_Match_End);
            if (smend != null) {
                TeamRedix.Builder smresp = TeamRedix.newBuilder();
                if (smend.getCardType() > 0) {
                    smresp.setCardType(smend.getCardType());
                    smresp.setRedixNum(smend.getRedixNum());
                }
                resp.setRedix(smresp);
            }
        }
        return resp.build();
    }

    private static BADRankedTeam rankedTeam(RankedTeam team) {
        return BADRankedTeam.newBuilder()
            .setOldTier(team.getOldTier())
            .setNewTier(team.getNewTier())
            .setChangeRating(team.getChangeRating())
            .setFinalRating(team.getFinalRating())
            .build();
    }

    private static BattlePB.BattleEndPlayerData endPlayer(long teamId, PlayerActStat source) {
        return BattlePB.BattleEndPlayerData.newBuilder()
            .setDf(Math.round(source.getValue(EActionType.pts)))
            .setLb(Math.round(source.getValue(EActionType.reb)))
            .setPlayerId(source.getPlayerRid())
            .setTeamId(teamId)
            .setZg(Math.round(source.getValue(EActionType.ast)))
            .build();
    }

    private static BattlePB.BattleEndTeamData endTeam(TeamReport tr,
                                                      List<BattlePB.BattlePropData> propList) {
        return BattlePB.BattleEndTeamData.newBuilder()
            .setTeamId(tr.getTeamId())
            .setScore(tr.getScore())
            .setTeamLogo(tr.getLogo())
            .setTeamName(tr.getName())
            .addAllGiftList(propList)
            .build();
    }

    public static GameLogPB.BattleEndLogData getBattleEndLogData(BattleTeam home, BattleTeam away) {
        return GameLogPB.BattleEndLogData.newBuilder()
            .setHome(endTeam(home))
            .setAway(endTeam(away))
            .setCode(0)
            .build();

    }

    private static BattleEndTeamResp endTeam(BattleTeam bt) {
        List<BattlePlayerSourceData> players = bt.getPlayers().stream()
            .map(BattlePb::playerSource).collect(Collectors.toList());
        return BattleEndTeamResp.newBuilder()
            .setTeamId(bt.getTeamId())
            .setTeam(simpleTeamData(bt))
            .setScore(bt.getScore())
            .setStepScore(stepScore(bt))
            .addAllPlayers(players)
            .build();
    }

    private static TeamSimpleData.Builder simpleTeamData(BattleTeam bt) {
        return TeamSimpleData.newBuilder()
            .setTeamId(bt.getTeamId())
            .setName(bt.getName())
            .setLogo(bt.getLogo())
            .setLevel(bt.getLevel());
    }

    private static GameLogPB.BattlePlayerSourceData playerSource(BattlePlayer player) {
        return GameLogPB.BattlePlayerSourceData.newBuilder()
            .setDf((int) player.getRealTimeActionStats().getValue(EActionType.pts))
            .setFg((int) player.getRealTimeActionStats().getValue(EActionType.pf))
            .setFq((int) player.getRealTimeActionStats().getValue(EActionType.fta))
            .setFqmz((int) player.getRealTimeActionStats().getValue(EActionType.ftm))
            .setGm((int) player.getRealTimeActionStats().getValue(EActionType.blk))
            .setLb((int) player.getRealTimeActionStats().getValue(EActionType.reb))
            .setPlayerId(player.getPlayerId())
            .setPower((int) player.getPower())
            .setQd((int) player.getRealTimeActionStats().getValue(EActionType.stl))
            .setSf((int) player.getRealTimeActionStats().getValue(EActionType._3pa))
            .setSfmz((int) player.getRealTimeActionStats().getValue(EActionType._3pm))
            .setSw((int) player.getRealTimeActionStats().getValue(EActionType.to))
            .setTl((int) player.getRealTimeActionStats().getValue(EActionType.fga))
            .setTlmz((int) player.getRealTimeActionStats().getValue(EActionType.fgm))
            .setZg((int) player.getRealTimeActionStats().getValue(EActionType.ast))
            .setTime((int) player.getRealTimeActionStats().getValue(EActionType.min))
            .setPos(player.getLpPos().getId())
            .build();
    }

    private static GameLogPB.BattleStepScoreData stepScore(BattleTeam team) {
        return GameLogPB.BattleStepScoreData.newBuilder()
            .setStep1(team.getStepScore(EBattleStep.First_Period))
            .setStep2(team.getStepScore(EBattleStep.Second_Period))
            .setStep3(team.getStepScore(EBattleStep.Thrid_Period))
            .setStep4(team.getStepScore(EBattleStep.Fourth_Period))
            .setStepot(team.getStepScore(EBattleStep.Overtime))
            .setTeamId(team.getTeamId())
            .build();
    }

    static BattlePB.BattleHalfTimePlayerData halfTimePlayerData(BattlePlayer player) {
        return BattlePB.BattleHalfTimePlayerData.newBuilder()
            .setDf((int) player.getRealTimeActionStats().getValue(EActionType.pts))
            .setLb((int) player.getRealTimeActionStats().getValue(EActionType.reb))
            .setTl((int) player.getRealTimeActionStats().getValue(EActionType.fga))
            .setTlmz((int) player.getRealTimeActionStats().getValue(EActionType.fgm))
            .setZg((int) player.getRealTimeActionStats().getValue(EActionType.ast))
            .setPlayerId(player.getPlayerId())
            .build();
    }

    public static BattleHisResp.Builder historyResp(BattleInfoPO e) {
        BattleHisResp.Builder resp = BattleHisResp.newBuilder()
            .setBattleId(e.getBattleId())
            .setBattleType(e.getBattleType())
            .setHomeTeamId(e.getHomeTeamId())
            .setHomeTeamName(e.getHomeTeamName())
            .setHomeScore(e.getHomeScore())
            .setAwayTeamId(e.getAwayTeamId())
            .setAwayTeamName(e.getAwayTeamName())
            .setAwayScore(e.getAwayScore())
            .setCreateTime(e.getCreateTime().getMillis());
        if (e.getVi1() > 0) {
            resp.setVi1(e.getVi1());
        }
        if (e.getVi2() > 0) {
            resp.setVi2(e.getVi2());
        }
        if (e.getVi3() > 0) {
            resp.setVi3(e.getVi3());
        }
        if (e.getVi4() > 0) {
            resp.setVi4(e.getVi4());
        }

        if (e.getVl1() > 0) {
            resp.setVl1(e.getVl1());
        }
        if (e.getVl2() > 0) {
            resp.setVl2(e.getVl2());
        }
        if (e.getVl3() > 0) {
            resp.setVl3(e.getVl3());
        }
        if (e.getVl4() > 0) {
            resp.setVl4(e.getVl4());
        }

        if (e.getStr1() != null && !e.getStr1().isEmpty()) {
            resp.setStr1(e.getStr1());
        }
        if (e.getStr2() != null && !e.getStr2().isEmpty()) {
            resp.setStr2(e.getStr2());
        }
        return resp;
    }

    public static BattleStartData battleStartResp(BattleSource bs) {
        return battleStartResp(bs, null);
    }

    public static BattleStartData battleStartResp(BattleSource bs, String nodeIp) {
        BattleStartData.Builder resp = BattleStartData.newBuilder()
            .setType(bs.getInfo().getBattleType().getId())
            .setBattleId(bs.getId())
            .setHome(bs.getHomeTid())
            .setAway(bs.getAwayTid());
        if (nodeIp != null && !nodeIp.isEmpty()) {
            resp.setNodeIp(nodeIp);
        }
        return resp.build();
    }

    public static BattleStartData battleStartResp(EBattleType type, long bid, long home, long away, String nodeIp) {
        BattleStartData.Builder resp = BattleStartData.newBuilder()
            .setType(type.getId())
            .setBattleId(bid)
            .setHome(home)
            .setAway(away);
        if (nodeIp != null && !nodeIp.isEmpty()) {
            resp.setNodeIp(nodeIp);
        }
        return resp.build();
    }

    public static TeamPB.TeamBattleStatusData battleStat(TeamBattleStatus status) {
        return TeamPB.TeamBattleStatusData.newBuilder()
            .setBattleId(status.getBattleId())
            .setBattleType(status.getType().getId())
            .setNode(status.getNodeIp())
            .build();

    }

}
