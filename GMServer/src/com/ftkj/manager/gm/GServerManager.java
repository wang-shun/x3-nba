package com.ftkj.manager.gm;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.UnCheck;
import com.ftkj.console.GameConsole;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.User;
import com.ftkj.manager.common.NodeManager;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GMCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.util.MD5Util;
import com.ftkj.util.StringUtil;

import org.apache.mina.core.session.IoSession;

public class GServerManager extends BaseManager {

    @IOC
    private NodeManager nodeManager;

    private static final String LogicKey = "dalsdjfD479f1323lkp8737NDKJ3208FNJ15KWas";

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_loginManager)
    public void loginManager(long teamId, String md5) {
        String m = MD5Util.encodeMD5(teamId, LogicKey).toLowerCase();
        IoSession session = getSession();
        if (GameSource.isDebug || md5.toLowerCase().equals(m)) {
            User user = GameSource.getUser(teamId);

            if (user != null && user.getSession().isActive() && user.getSession().getId() != session.getId()) {
                GameSource.offlineUser(teamId);
            }
            session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
            GameSource.online(teamId, session);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).setBigNum(teamId).build());
        } else {
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(GameConsole.Game_Load_Code_Error).setBigNum(teamId).build());
        }

    }

    /**
     * 刷新身价
     *
     * @param node
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_reloadNBAData)
    public void reloadNBAData(String node) {
        log.error("刷新球员身价node={}", node);
        if ("All".equals(node)) {
            RPCMessageManager.sendMessage(CrossCode.WebManager_reloadAllNodeNBAData, node, 1);
        } else {
            RPCMessageManager.sendMessage(CrossCode.WebManager_reloadNBAData, node, 1);
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_reloadNBAPKData)
    public void reloadNBAPKData() {
        log.error("刷新今日赛程");
        RPCMessageManager.sendMessage(CrossCode.WebManager_reloadAllNodeNBAPKData, null);
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_tipAllGame)
    public void tipAllGame(String msg, String node) {
        log.error("全服跑马灯{} {}", node, msg);
        RPCMessageManager.sendMessage(CrossCode.WebManager_pushAllGame, null, msg, node);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_closeServer)
    public void closeServer(String node) {
        log.error("停服{}", node);
        RPCMessageManager.sendMessage(CrossCode.WebManager_closeServer, node);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_sendEMail)
    public void gmSendTAll(int type, String title, String content, String awardConfig, String teamStrIds, String node) {
        log.error("发送全服邮件,{} {} {} {} {} {}", type, title, content, awardConfig, teamStrIds, node);
        if (node.contains("All")) {
            nodeManager.getALLRPCServer()
                    .forEach(no -> RPCMessageManager.sendMessage(CrossCode.WebManager_sendEmail, no.getServerName(),
                            type, title, content, awardConfig, teamStrIds));
        } else {
            String[] n = StringUtil.toStringArray(node, StringUtil.DEFAULT_ST);
            for (String nn : n) {
                RPCMessageManager.sendMessage(CrossCode.WebManager_sendEmail, nn,
                        type, title, content, awardConfig, teamStrIds);
            }
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }
    
    /**
     * 运营后台手工触发发送竞猜活动的奖励邮件.
     * @param id		比赛的Id
     * @param winId		1主队赢,2客队赢
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_sendGameGuessEMail)
    public void gmSendGameGuessRewardEmail(int id, int winId){
    	log.info("发送竞猜活动奖励邮件比赛:id={}", id);
    	RPCMessageManager.sendMessage(CrossCode.WebManager_sendGameGuessEMail, null, id, winId);
    	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }
    
    /**
     * 运营后台更新竞猜活动数据调用(更新比赛为已发奖励不调用此方法).
     * @param id	比赛的Id
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_updateGameGuessData)
    public void gmUpdateGameGuessData(int id){
    	log.info("运营后台更新了竞猜活动比赛:id={}", id);
    	RPCMessageManager.sendMessage(CrossCode.WebManager_updateGameGuessData, null, id);
    	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }
    
    /**
     * 运行后台,客服回复了玩家提问调用.
     * @param id	比赛的Id
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_customerResp)
    public void gmCustomerResp(int csId, String respStr, String node){
    	log.info("运行后台,客服回复了玩家提问:csid={}|resp={}|node={}", csId, respStr, node);
    	String resp = null;
    	try {
			resp = URLDecoder.decode(respStr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("{}", e.getMessage());
		}
    	
    	RPCMessageManager.sendMessage(CrossCode.WebManager_customerResp, node, csId, resp);
    	sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    /**
     * 封号
     * 踢玩家下线，并加入登录黑名单
     *
     * @param type   0
     * @param teamId
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_lockTeam)
    public void lockTeam(int type, long teamId, String node) {
        log.error("封号，踢玩家下线{},{},{}", type, teamId, node);
        RPCMessageManager.sendMessage(CrossCode.WebManager_lockTeam, node, teamId, type);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_chatController)
    public void chatController(int type, long teamId, String node) {
        log.error("封世界聊天{},{},{}", type, teamId, node);
        RPCMessageManager.sendMessage(CrossCode.WebManager_chatController, node, teamId, type);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @UnCheck
    @ClientMethod(code = GMCode.GMManager_resetServerData)
    public void resetServerData(String node) {
        log.error("清档node={}", node);
        RPCMessageManager.sendMessage(CrossCode.WebManager_resetServerData, node);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    /**
     * 重置多人赛
     *
     * @param node
     * @param matchId
     * @param seqId
     */
    @UnCheck
    @ClientMethod(code = GMCode.GMManager_resetMatch)
    public void resetMatch(String node, int matchId, int seqId) {
        log.error("重置多人赛 node={}, matchId={}, seqId={}", node, matchId, seqId);
        RPCMessageManager.sendMessage(CrossCode.WebManager_resetMatch, node, matchId, seqId);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(0).build());
    }

    @Override
    public void instanceAfter() {

    }

}
