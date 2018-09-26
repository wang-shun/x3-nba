package com.ftkj.manager.common;

import com.ftkj.annotation.RPCMethod;
import com.ftkj.annotation.UnCheck;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.event.EEventType;
import com.ftkj.event.EventBusManager;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.proto.DefaultPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.RPCMessageManager;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.rpc.RPCServer;
import com.ftkj.tool.zookeep.ZookeepServer;
import com.ftkj.util.MD5Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * @author tim.huang
 * 2017年4月25日
 * 服务器保存其他所有节点信息，方便客户端远程调用
 * 服务器移除，列表信息不会移除。
 * rpcMap只是用来指明服务器地址
 */
public class NodeManager extends BaseManager {
    private static final Logger log = LoggerFactory.getLogger(NodeManager.class);
    private Map<String, RPCServer> rpcMap;
    //sid转节点名称类
    private Map<Integer, String> sidToNodeMap;

    @RPCMethod(code = CrossCode.NodeManager_registerNode, pool = EServerNode.Node, type = ERPCType.ALLNODE)
    public void registerNode(RPCServer server) {
        log.debug("有新的服务器节点注册，记录该节点信息[{}]", server.toString());
        rpcMap.put(server.getServerName(), server);
        if (EServerNode.Logic.equals(server.getPool())) {
            sidToNodeMap.put(server.getSid(), server.getServerName());
        }
        RPCMessageManager.sendMessage(CrossCode.NodeManager_callBackRegisterNode, server.getServerName(), ZookeepServer.getInstance().getServer());
        EventBusManager.post(EEventType.服务器节点注册, server);
    }

    @RPCMethod(code = CrossCode.NodeManager_callBackRegisterNode, pool = EServerNode.Node, type = ERPCType.NONE)
    public void callBackRegisterNode(RPCServer server) {//回调注册服务器
        log.debug("收到服务器节点注册，记录该节点信息[{}]", server.toString());
        rpcMap.put(server.getServerName(), server);
        if (EServerNode.Logic.equals(server.getPool())) {
            sidToNodeMap.put(server.getSid(), server.getServerName());
        }
    }

    private static final String nodeTokenKey = "dskj24fJOf65dJ023pLJa612nNfhBap234712jmKHNf96uih49812hj";
    private static final int TimeOut = 5 * 60 * 1000;

    @ClientMethod(code = ServiceCode.NodeManager_getNodeToken)
    public void getNodeToken(long times) {
        long teamId = getTeamId();
        String token = MD5Util.encodeMD5(nodeTokenKey + teamId + times + nodeTokenKey);
        sendMessage(DefaultPB.DefaultData.newBuilder().setMsg(token).build());
    }

    public String getNode(int sid) {
        return this.sidToNodeMap.get(sid);
    }

    @UnCheck
    @ClientMethod(code = ServiceCode.NodeManager_loginNode)
    public void loginNode(long teamId, String token, int sid, long times) {
        String tk = MD5Util.encodeMD5(nodeTokenKey + teamId + times + nodeTokenKey);
        //		long now = System.currentTimeMillis();
        //		if(now-times>= TimeOut || !token.equals(tk)){
        //			sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.OtherError.code).build());
        //			return;
        //		}
        IoSession session = getSession();
        session.setAttribute(GameSource.MINA_SESSION_ATTR_KEY_TEAMID, teamId);
        GameSource.online(teamId, session);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 获得服务器时间
     */
    @ClientMethod(code = ServiceCode.NodeManager_loadServerTime)
    public void loadServerTime() {
        sendMessage(DefaultPB.DefaultData.newBuilder()
                .setBigNum(System.currentTimeMillis())
                .build());
    }

    public RPCServer getServer(String nodeName) {
        return rpcMap.get(nodeName);
    }

    public String getServerIP(String nodeName) {
        if (nodeName == null || "".equals(nodeName)) { return ""; }
        RPCServer node = getServer(nodeName);
        if (node == null) { return ""; }
        return node.getAddress() + ":" + node.getPort();
    }

    public List<RPCServer> getALLRPCServer() {
        return Lists.newArrayList(rpcMap.values());
    }

    /**
     * 停服接口
     */
    @RPCMethod(code = CrossCode.WebManager_closeServer, pool = EServerNode.Node, type = ERPCType.NONE)
    @UnCheck
    public void closeServer() {
        log.error("远程停服");
        System.exit(0);
    }

    @Override
    public void instanceAfter() {
        rpcMap = Maps.newConcurrentMap();
        //注册自己的名称
        rpcMap.put(GameSource.serverName, ZookeepServer.getInstance().getServer());
        RPCMessageManager.sendMessage(CrossCode.NodeManager_registerNode, null, ZookeepServer.getInstance().getServer());
        sidToNodeMap = Maps.newHashMap();
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Node.getOrder();
    }

}
