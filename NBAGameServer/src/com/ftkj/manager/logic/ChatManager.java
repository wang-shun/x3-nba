package com.ftkj.manager.logic;

import com.ftkj.annotation.IOC;
import com.ftkj.console.ConfigConsole;
import com.ftkj.console.ServiceConsole;
import com.ftkj.db.ao.logic.ITeamAO;
import com.ftkj.enums.EChat;
import com.ftkj.enums.EConfigKey;
import com.ftkj.enums.EGameTip;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ETaskCondition;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.chat.ChatOfflineInfo;
import com.ftkj.manager.friend.TeamFriends;
import com.ftkj.manager.logic.log.GameChatLogManager;
import com.ftkj.manager.team.Team;
import com.ftkj.manager.team.TeamStatus;
import com.ftkj.proto.ChatPB;
import com.ftkj.proto.ChatPB.ChatMsg;
import com.ftkj.proto.ChatPB.ChatOfflineMsg;
import com.ftkj.proto.ChatPB.ChatOfflineMsgList;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.GameSource;
import com.ftkj.server.RedisKey;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceManager;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.util.DateTimeUtil;
import com.ftkj.util.SensitivewordFilter;
import com.google.common.collect.Sets;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tim.huang
 * 2017年6月1日
 * 聊天
 */
public class ChatManager extends BaseManager {
    @IOC
    private ITeamAO teamAO;
    @IOC
    private TeamManager teamManager;
    @IOC
    private TeamStatusManager teamStatusManager;
    @IOC
    private LeagueManager leagueManager;
    @IOC
    private TaskManager taskManager;
    @IOC
    private FriendManager friendManager;
    @IOC
    private JedisUtil redis;

    private int _minChatLevel;
    private static final int _spChatSecond = 1;
    private SensitivewordFilter txtFilter;

    private Set<Long> _backChatList;

    /**
     * 世界聊天
     *
     * @param msg
     */
    @ClientMethod(code = ServiceCode.ChatManager_chatWorld)
    public void chatWorld(String msg) {
        long teamId = getTeamId();
        // 是否在禁言列表
        if (this._backChatList.contains(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ChatLock.code).build());
            return;
        }
        if (msg.length() >= ConfigConsole.getGlobal().chatMsgWroldCountLimit) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_1.code).build());
            return;
        }
        msg = repShieldText(msg);

        Team team = teamManager.getTeam(teamId);
        if (team.getLevel() < _minChatLevel) {//球队等级不足
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_2.code).build());
            return;
        }
        TeamStatus status = teamStatusManager.get(teamId);
        DateTime now = DateTime.now();
        if (status.getWorldChat() != null && DateTimeUtil.secondBetween(status.getWorldChat()
            , now) < _spChatSecond) {//聊天间隔CD未到
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_3.code).build());
            return;
        }
        //
        status.setWorldChat(now);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        sendMessage(ServiceConsole.getChatKey(EChat.世界聊天), ChatPB.ChatMsgData
            .newBuilder()
            .setMsg(msg)
            .setType(EChat.世界聊天.getType())
            .setTeam(teamManager.teamResp(teamId))
            .build(), ServiceCode.Push_Chat_World);
        //GameKeyValLogManager.Log(GameKeyValLogManager.聊天, msg);
        GameChatLogManager.Log(teamId, team.getName(), team.getLevel(), leagueManager.getLeagueId(teamId), "null", EChat.世界聊天.getType(), msg);
    }

    /**
     * 联盟聊天
     *
     * @param msg
     */
    @ClientMethod(code = ServiceCode.ChatManager_chatLeague)
    public void chatLeague(String msg) {
        long teamId = getTeamId();
        // 是否在禁言列表
        if (this._backChatList.contains(teamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ChatLock.code).build());
            return;
        }
        if (msg.length() >= ConfigConsole.getGlobal().chatMsgWroldCountLimit) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_1.code).build());
            return;
        }
        msg = repShieldText(msg);

        int leagueId = leagueManager.getLeagueId(teamId);
        if (leagueId == 0) {//玩家未加入联盟
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.League_5.code).build());
            return;
        }
        Team team = teamManager.getTeam(teamId);
        taskManager.updateTask(teamId, ETaskCondition.联盟聊天, 1, EModuleCode.联盟.getName());
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
        sendMessage(ServiceConsole.getChatKey(EChat.联盟聊天) + leagueId, ChatPB.ChatMsgData
            .newBuilder()
            .setMsg(msg)
            .setType(EChat.联盟聊天.getType())
            .setTeam(teamManager.teamResp(teamId))
            .build(), ServiceCode.Push_Chat_World);
        GameChatLogManager.Log(teamId, team.getName(), team.getLevel(), leagueManager.getLeagueId(teamId), "null", EChat.联盟聊天.getType(), msg);
    }

    /**
     * 私聊
     * @param targetTeamId 目标球队ID
     * @param msg
     */
    @ClientMethod(code = ServiceCode.ChatManager_chatPrivate)
    public void chatPrivate(long targetTeamId, String msg) {
        long sendTeamId = getTeamId();
        // 是否在禁言列表
        if (this._backChatList.contains(sendTeamId)) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.ChatLock.code).build());
            return;
        }
        if (msg.length() >= ConfigConsole.getGlobal().chatMsgWroldCountLimit) {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.chat_1.code).build());
            return;
        }
        msg = repShieldText(msg);
        Team team = teamManager.getTeam(sendTeamId);

        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setMsg(msg).setBigNum(targetTeamId).build());

        if (GameSource.isOline(targetTeamId)) {
            sendMessage(targetTeamId, ChatPB.ChatMsgData
                .newBuilder()
                .setMsg(msg)
                .setType(EChat.私聊.getType())
                .setTeam(teamManager.teamResp(sendTeamId))
                .build(), ServiceCode.Push_Chat_World);
        }else {
        	// 判断接收者玩家是否再发送者好友列表是黑名单
            TeamFriends tf = friendManager.getTeamFriends(sendTeamId);
            if (tf.isBlackName(targetTeamId)) {
                return;
            }
            
            // 没有在线标记离线消息未读1
            String key = getChatOfflineMsgIsReadKey(targetTeamId, sendTeamId);
        	redis.set(key, "1");
		} 
        
        saveChatPrivateMsg(sendTeamId, targetTeamId, msg);
        GameChatLogManager.Log(sendTeamId, team.getName(), team.getLevel(), 
        		leagueManager.getLeagueId(sendTeamId), "null", EChat.私聊.getType(), msg);
    }
    
    /**
     * 保存玩家私聊最新10条(读取配置数据)聊天消息。
     * @param senderTeamId		消息发送者
     * @param targetTeamId		消息接收者
     * @param msg				消息内容
     */
    private void saveChatPrivateMsg(long senderTeamId, long targetTeamId, String msg){
        // 把发送者添加到接收者的留言的球队ID列表
        String sendersKey = getChatOfflineMsgSendersKey(targetTeamId);
        Set<String> senders = redis.smembers(sendersKey);
        if (senders == null) {
            senders = new HashSet<>();
        }
        
        String mem = redis.str(senderTeamId);
        if (!senders.contains(mem)) {
            senders.add(mem);
            redis.sadd(sendersKey, mem);
        }
        
        // 把接收者也添加到发送者的留言的球队ID列表
        String sendersKey2 = getChatOfflineMsgSendersKey(senderTeamId);
        Set<String> senders2 = redis.smembers(sendersKey2);
        if (senders2 == null) {
            senders2 = new HashSet<>();
        }
        
        String mem2 = redis.str(targetTeamId);
        if (!senders.contains(mem2)) {
            senders2.add(mem2);
            redis.sadd(sendersKey2, mem2);
        }
        
        //消息发送者，根据接收者teamId保存发送的消息
        long[] teamIdArray = sortTwoLongNum(targetTeamId, senderTeamId);
        ChatOfflineInfo chatOfflineInfo = new ChatOfflineInfo();
        chatOfflineInfo.setTeamId(senderTeamId);
        chatOfflineInfo.setContent(msg);
        chatOfflineInfo.setCreateTime(System.currentTimeMillis());
        
    	//私聊好友之间的消息共用保存，生成的msgKey,根据teamId从小到大
    	String msgKey = getChatOfflineMsgKey(teamIdArray[0], teamIdArray[1]);
    	List<ChatOfflineInfo> offlineList = redis.getList(msgKey);
    	if (offlineList == null) {
    		offlineList = new ArrayList<>();
    	}else {
    		if (offlineList.size() >= ConfigConsole.global().chatOfflineMsgLimit) {
    			offlineList.remove(0);
    		}
    	}
    	
    	offlineList.add(chatOfflineInfo);
    	redis.rpush(msgKey, offlineList);
    }
    
    /**
     * 从小到大排序
     * @param long1
     * @param long2
     * @return
     */
    private long[] sortTwoLongNum(long long1, long long2){
    	long[] array = {long1, long2};
    	if (long1 > long2) {
			array[0] = long2;
			array[1] = long1;
		}
    	
    	return array;
    }

    /**
     * 获取球队离线信息
     *
     * @param msg
     */
    @ClientMethod(code = ServiceCode.ChatManager_chatOffline)
    public void getOfflineInfos() {
        long teamId = getTeamId();
        ChatOfflineMsgList.Builder builder = ChatOfflineMsgList.newBuilder();
        String sendersKey = getChatOfflineMsgSendersKey(teamId);
        Set<String> senders = redis.smembers(sendersKey);
        if (senders == null || senders.isEmpty()) {
            sendMessage(builder.build());
            return;
        }
        log.debug("chat off tid {} senders {}", teamId, senders.size());
        
        //修改功能
        for (String sender : senders) {
            Long sendTeamId = Long.valueOf(sender);
            long[] array = sortTwoLongNum(sendTeamId, teamId);
            String msgKey = getChatOfflineMsgKey(array[0], array[1]);
            List<ChatOfflineInfo> list = redis.getList(msgKey);
            
            if (list != null && !list.isEmpty()) {
                log.debug("chat off tid {} sender {} msg {}", teamId, sender, list.size());
                builder.addOffline(buildOfflineMsg(teamId, sendTeamId, list));
            }
        }
        
        sendMessage(builder.build());
    }
    
    /**
     * 私聊消息已读
     * @param senderTeamId	发送私聊的好友teamId
     */
    @ClientMethod(code = ServiceCode.ChatManager_chatMsgIsRead)
    public void chatPrivateMagRead(long senderTeamId) {
    	long targetTeamId = getTeamId();
    	String key = getChatOfflineMsgIsReadKey(targetTeamId, senderTeamId);
    	redis.set(key, "0");
    	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    private ChatOfflineMsg.Builder buildOfflineMsg(Long targetTeamId, long sendTeamId, List<ChatOfflineInfo> chatOfflineList) {
        ChatOfflineMsg.Builder builder = ChatOfflineMsg.newBuilder();
        
        Integer isReadFlag = redis.getInt(getChatOfflineMsgIsReadKey(targetTeamId, sendTeamId));
        builder.setIsRead(isReadFlag == null ? 0 : isReadFlag);
        builder.setTeam(teamManager.teamResp(sendTeamId));
        
        for (ChatOfflineInfo chatOfflineInfo : chatOfflineList) {
            builder.addChatInfo(buildChatMsg(chatOfflineInfo));
        }
        
        return builder;
    }

    private ChatMsg.Builder buildChatMsg(ChatOfflineInfo chatOfflineInfo) {
        ChatMsg.Builder msg = ChatMsg.newBuilder();
        msg.setTeamId(chatOfflineInfo.getTeamId());
        msg.setCerateTime(chatOfflineInfo.getCreateTime());
        msg.setContent(chatOfflineInfo.getContent());
        return msg;
    }

    /**
     * 禁言名单
     *
     * @param teamId
     * @param type
     */
    void addBackTeam(long teamId, int type) {
        if (type == 1) {
            this._backChatList.add(teamId);
        } else {
            this._backChatList.remove(teamId);
        }
    }

    public boolean shieldText(String text) {
        return this.txtFilter.isContaintSensitiveWord(text, 2);
    }

    public String repShieldText(String text) {
        return this.txtFilter.replaceSensitiveWord(text, 2, "*");
    }

    /**
     * 注册聊天监听
     *
     * @param teamId
     */
    public void registerChat(long teamId, int leagueId) {
        ServiceManager.addService(ServiceConsole.getChatKey(EChat.世界聊天), teamId);
        registerLeagueChat(teamId, leagueId);
    }

    public void registerLeagueChat(long teamId, int leagueId) {
        if (leagueId != 0) { ServiceManager.addService(ServiceConsole.getChatKey(EChat.联盟聊天) + leagueId, teamId); }
    }

    /**
     * 推送世界聊天
     *
     * @param tip
     * @param level
     * @param vals
     */
    public void pushGameTip(EGameTip tip, int level, String... vals) {
        String val = Arrays.stream(vals).collect(Collectors.joining(","));
        ChatPB.GameTipData data = ChatPB.GameTipData.newBuilder().setLevel(level).setModuleId(tip.getId()).setVals(val).build();
        sendMessage(ServiceConsole.getChatKey(EChat.世界聊天), data, ServiceCode.Push_Window_All);
    }

    @Override
    public void initConfig() {
        _minChatLevel = ConfigConsole.getIntVal(EConfigKey.Chat_Level);
    }

    @Override
    public void instanceAfter() {
        String txt = "";
        txtFilter = SensitivewordFilter.getInstance(Sets.newHashSet(txt.split("[,]")));
        this._backChatList = Sets.newHashSet();
        // 加载所有禁言球队
        this._backChatList.addAll(teamAO.getChatBlackTeamList());
    }

    private static String getChatOfflineMsgKey(long targetTeamId, long sendTeamId) {
        return RedisKey.Chat_Offline_Msg + targetTeamId + ":" + sendTeamId;
    }

    private static String getChatOfflineMsgSendersKey(long targetTeamId) {
        return RedisKey.Chat_Offline_sendTeamIds + targetTeamId;
    }
    
    /**
     * redis存储离线消息是否读取的Key
     * @param targetTeamId	消息接收者的teamId
     * @param sendTeamId	消息发送者的teamId
     * @return
     */
    private static String getChatOfflineMsgIsReadKey(long targetTeamId, long sendTeamId) {
        return RedisKey.Chat_Offline_Msg_Is_Read + targetTeamId + "_" + sendTeamId;
    }
}
