package com.ftkj.x3.client.proto;

import com.ftkj.console.EquiConsole;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleType;
import com.ftkj.proto.BuffPB.BuffData;
import com.ftkj.proto.EmailPB.EmailData;
import com.ftkj.proto.EmailPB.TeamEmailData;
import com.ftkj.proto.FriendPB.FriendData;
import com.ftkj.proto.FriendPB.TeamFriendData;
import com.ftkj.proto.PlayerPB.PlayerData;
import com.ftkj.proto.PlayerPB.PlayerGradeData;
import com.ftkj.proto.PlayerPB.PlayerGradeMainData;
import com.ftkj.proto.PlayerPB.PlayerSimpleData;
import com.ftkj.proto.PropPB.PropData;
import com.ftkj.proto.PropPB.TeamPropsData;
import com.ftkj.proto.TeamEquiPB.EquiData;
import com.ftkj.proto.TeamEquiPB.PlayerEquiData;
import com.ftkj.proto.TeamEquiPB.TeamEquiData;
import com.ftkj.proto.TeamPB.TeamBattleStatusData;
import com.ftkj.proto.TeamPB.TeamMoneyData;
import com.ftkj.proto.TeamPB.TeamStatusData;
import com.ftkj.proto.TrainPlayerPB.TrainPlayerData;
import com.ftkj.proto.VipPB.VipData;
import com.ftkj.x3.client.model.ClientBuff;
import com.ftkj.x3.client.model.ClientEmail;
import com.ftkj.x3.client.model.ClientEquip;
import com.ftkj.x3.client.model.ClientFriend;
import com.ftkj.x3.client.model.ClientMoney;
import com.ftkj.x3.client.model.ClientPlayer;
import com.ftkj.x3.client.model.ClientPlayerEquip;
import com.ftkj.x3.client.model.ClientPlayerGrade;
import com.ftkj.x3.client.model.ClientProp;
import com.ftkj.x3.client.model.ClientTeamBattleStatus;
import com.ftkj.x3.client.model.ClientTeamEquip;
import com.ftkj.x3.client.model.ClientTeamFriends;
import com.ftkj.x3.client.model.ClientTeamPlayer;
import com.ftkj.x3.client.model.ClientTeamStatus;
import com.ftkj.x3.client.model.ClientTrainPlayer;
import com.ftkj.x3.client.model.ClientVip;
import com.ftkj.xxs.util.Maps;
import com.google.protobuf.TextFormat;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author luch
 */
public class ClientPbUtil {
    private static final Logger log = LoggerFactory.getLogger(ClientPbUtil.class);

    public static String shortDebug(com.google.protobuf.Message msg) {
        if (msg == null) {
            return "null";
        }
        return TextFormat.shortDebugString(msg);
    }

    public static ClientTeamEquip createEquip(TeamEquiData ted) {
        ClientTeamEquip te = new ClientTeamEquip();
        te.setEquips(createEquip(ted.getPlayerEquiListList()));
        te.setClothes(createEquip(ted.getClothesEquiListList()));
        return te;
    }

    private static Map<Integer, ClientPlayerEquip> createEquip(List<PlayerEquiData> peds) {
        Map<Integer, ClientPlayerEquip> map = new HashMap<>(peds.size());
        for (PlayerEquiData ped : peds) {
            map.put(ped.getPlayerId(), createEquip(ped));
        }
        return map;
    }

    private static ClientPlayerEquip createEquip(PlayerEquiData ped) {
        ClientPlayerEquip pe = new ClientPlayerEquip();
        Map<Integer, ClientEquip> map = new HashMap<>(ped.getListCount());
        for (EquiData ed : ped.getListList()) {
            int type = EquiConsole.getEquiBean(ed.getEid()).getType();
            map.put(type, createEquip(ed));
        }
        pe.setEquips(map);
        return pe;
    }

    private static ClientEquip createEquip(EquiData ed) {
        ClientEquip e = new ClientEquip();
        e.setId(ed.getId());
        e.setEquId(ed.getEid());
        e.setPlayerId(ed.getPlayerId());
        e.setStrLv(ed.getStrlv());
        e.setStrBless(ed.getStrBless());
        e.setEndTime(new DateTime(ed.getEndTime() * 1000));
        e.setEquiTeam(ed.getEquiTeam());
        return e;
    }

    public static ClientMoney createMoney(TeamMoneyData tmd) {
        ClientMoney tm = new ClientMoney();
        updateMoney(tmd, tm);
        return tm;
    }

    public static void updateMoney(TeamMoneyData tmd, ClientMoney tm) {
        tm.setExp(tmd.getExp());
        tm.setGold(tmd.getGold());
        tm.setMoney(tmd.getMoney());
        tm.setBdMoney(tmd.getBdMoney());
    }

    public static ClientTeamFriends createFriends(TeamFriendData fd) {
        ClientTeamFriends tf = new ClientTeamFriends();
        tf.setFriends(createFriends(fd.getFriendListList()));
        tf.setBlackList(createFriends(fd.getBlackListList()));
        return tf;
    }

    private static Map<Long, ClientFriend> createFriends(List<FriendData> list) {
        Map<Long, ClientFriend> map = new HashMap<>(list.size());
        for (FriendData fd : list) {
            map.put(fd.getTeamId(), createFriend(fd));
        }
        return map;
    }

    private static ClientFriend createFriend(FriendData fd) {
        ClientFriend f = new ClientFriend();
        f.setFriendTeamId(fd.getTeamId());
        return f;
    }

    public static Map<Integer, ClientTrainPlayer> createTrainPlayer(List<TrainPlayerData> tpds) {
        Map<Integer, ClientTrainPlayer> map = Maps.newConcurrentMap(tpds.size());
        for (TrainPlayerData tpd : tpds) {
            map.put(tpd.getPlayerId(), createTrainPlayer(tpd));
        }
        return map;
    }

    private static ClientTrainPlayer createTrainPlayer(TrainPlayerData tpd) {
        ClientTrainPlayer tp = new ClientTrainPlayer();
        tp.setPlayerId(tpd.getPlayerId());
//        tp.setItem(tpd.getItem());
//        tp.setStep(tpd.getStep());
        return tp;
    }

    public static ClientVip createVip(VipData vd) {
        ClientVip vip = new ClientVip();
        vip.setTeamId(vd.getTeamId());
        vip.setLevel(vd.getLevel());
        vip.setAddMoney(vd.getExp());
        vip.setBuyStatus(vd.getBuyStatusList());
        return vip;
    }

    public static Map<Integer, ClientProp> createProp(long tid, TeamPropsData tpd) {
        Map<Integer, ClientProp> map = Maps.newConcurrentMap(tpd.getPropListCount());
        for (PropData pd : tpd.getPropListList()) {
            if (map.containsKey(pd.getPropId())) {
                log.warn("prop exists tid {} propid {}", tid, pd.getPropId());
            }
            map.put(pd.getPropId(), createProp(pd));
        }
        return map;
    }

    private static ClientProp createProp(PropData pd) {
        ClientProp p = new ClientProp();
        p.setId(pd.getId());
        p.setPropId(pd.getPropId());
        p.setNum(pd.getNum());
        p.setEndSec(pd.getEndTime());
        return p;
    }

    public static Map<Integer, ClientEmail> createEmail(TeamEmailData ted) {
        Map<Integer, ClientEmail> map = Maps.newConcurrentMap(ted.getEmailListCount());
        for (EmailData ed : ted.getEmailListList()) {
            map.put(ed.getId(), createEmail(ed));
        }
        return map;
    }

    private static ClientEmail createEmail(EmailData ed) {
        ClientEmail e = new ClientEmail();
        e.setId(ed.getId());
        e.setType(ed.getType());
        e.setViewId(ed.getViewId());
        e.setTitle(ed.getTitle());
        e.setContent(ed.getContent());
        e.setStatus(ed.getStatus());
        e.setCreateTime(new DateTime(ed.getCreateTime()));
        e.setProps(ed.getPropListList());
        return e;
    }

    public static ClientTeamPlayer createPlayer(PlayerGradeMainData pgmd, List<PlayerData> lineups, List<PlayerData> stroages) {
        ClientTeamPlayer tp = new ClientTeamPlayer();
        tp.setLineups(createPlayer(lineups));
        tp.setStoragePlayers(createPlayer(stroages));
        for (PlayerGradeData pgd : pgmd.getPlayerGradeListList()) {
            tp.addPlayerGrade(createPlayerGrade(pgd));
        }
        return tp;
    }

    private static ClientPlayerGrade createPlayerGrade(PlayerGradeData pgd) {
        ClientPlayerGrade pg = new ClientPlayerGrade();
        pg.setPlayerId(pgd.getPlayerId());
        pg.setGrade(pgd.getGrade());
        pg.setExp(pgd.getExp());

        return pg;
    }

    private static ConcurrentNavigableMap<Integer, ClientPlayer> createPlayer(List<PlayerData> pds) {
        ConcurrentNavigableMap<Integer, ClientPlayer> prs = new ConcurrentSkipListMap<>();
        for (PlayerData pd : pds) {
            prs.put(pd.getPid(), createPlayer(pd));
        }
        return prs;
    }

    private static ClientPlayer createPlayer(PlayerData pd) {
        ClientPlayer p = new ClientPlayer();
        p.setId(pd.getPid());
        p.setPlayerRid(pd.getPlayerId());
        p.setPos(EPlayerPosition.getEPlayerPosition(pd.getPlayerPosition()));
        p.setLineupPos(EPlayerPosition.getEPlayerPosition(pd.getPosition()));
        p.setPrice(pd.getPrice());
        p.setStorage(pd.getStatus());

        p.setAttack(pd.getAttack());
        p.setDefend(pd.getDefend());

        return p;
    }

    public static ClientPlayer createPlayer(PlayerSimpleData pd) {
        ClientPlayer p = new ClientPlayer();
        p.setId(pd.getPid());
        p.setPlayerRid(pd.getPlayerId());
        p.setPos(EPlayerPosition.getEPlayerPosition(pd.getPlayerPosition()));
        p.setLineupPos(EPlayerPosition.getEPlayerPosition(pd.getPosition()));
        p.setPrice(pd.getPrice());
        return p;
    }

    public static Map<Integer, ClientBuff> createBuff(List<BuffData> bds) {
        Map<Integer, ClientBuff> map = Maps.newConcurrentMap(bds.size());
        for (BuffData bd : bds) {
            map.put(bd.getId(), createBuff(bd));
        }
        return map;
    }

    private static ClientBuff createBuff(BuffData bd) {
        ClientBuff buff = new ClientBuff();
        buff.setId(bd.getId());
        buff.setParams(bd.getValuesList());
        buff.setEndTime(new DateTime(bd.getEndTime()));
        return buff;
    }

    public static ClientTeamStatus createStatus(TeamStatusData tsd) {
        ClientTeamStatus ts = new ClientTeamStatus();
        ts.setDraftRoomId(tsd.getDraftRoomId());
        for (TeamBattleStatusData sd : tsd.getBattleStatusList()) {
            ClientTeamBattleStatus tbs = new ClientTeamBattleStatus(EBattleType.getBattleType(sd.getBattleType()),
                    sd.getBattleId(),
                    sd.getNode());
            ts.getBattles().put(tbs.getBattleId(), tbs);
        }
        return ts;
    }
}
