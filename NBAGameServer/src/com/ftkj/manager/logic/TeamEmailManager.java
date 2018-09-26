package com.ftkj.manager.logic;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.ftkj.annotation.IOC;
import com.ftkj.annotation.RPCMethod;
import com.ftkj.cfg.EmailViewBean;
import com.ftkj.console.EmailConsole;
import com.ftkj.console.PropConsole;
import com.ftkj.db.ao.logic.IEmailAO;
import com.ftkj.db.domain.EmailPO;
import com.ftkj.enums.EEmailType;
import com.ftkj.enums.EModuleCode;
import com.ftkj.enums.ERPCType;
import com.ftkj.enums.EServerNode;
import com.ftkj.enums.ErrorCode;
import com.ftkj.manager.BaseManager;
import com.ftkj.manager.ClientMethod;
import com.ftkj.manager.email.Email;
import com.ftkj.manager.email.TeamEmail;
import com.ftkj.manager.logic.log.ModuleLog;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.proto.DefaultPB;
import com.ftkj.proto.EmailPB;
import com.ftkj.proto.EmailPB.TeamEmailData.Builder;
import com.ftkj.proto.PropPB;
import com.ftkj.server.CrossCode;
import com.ftkj.server.GameSource;
import com.ftkj.server.ManagerOrder;
import com.ftkj.server.ServiceCode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Jay
 * @Description:邮件系统
 * @time:2017年4月24日 下午5:55:39
 */
public class TeamEmailManager extends BaseManager {

    private Map<Long, TeamEmail> teamEmailMap;
    /**
     * 一定会初始化
     * teamId: seqId
     * eid = new AtomicInteger(maxId);
     */
    private Map<Long, AtomicInteger> teamEmailSeq;

    @IOC
    private IEmailAO emailAO;
    @IOC
    private GameManager gameManager;

    @IOC
    private PropManager propManager;
    @IOC
    private TeamManager teamManager;

    @Override
    public void instanceAfter() {
        teamEmailMap = Maps.newConcurrentMap();
        teamEmailSeq = Maps.newConcurrentMap();
        initSeqMap();
    }

    private void initSeqMap() {
        Map<Long, Integer> map = emailAO.getTeamEmailSeqMap();
        if (map == null) {
            return;
        }
        for (long teamId : map.keySet()) {
            teamEmailSeq.put(teamId, new AtomicInteger(map.get(teamId)));
        }
    }

    private AtomicInteger getSeqAtomic(long teamId) {
        if (!teamEmailSeq.containsKey(teamId)) {
            teamEmailSeq.put(teamId, new AtomicInteger(0));
        }
        return teamEmailSeq.get(teamId);
    }

    public TeamEmail getTeamEmail(long teamId) {
        TeamEmail te = teamEmailMap.get(teamId);
        if (te == null) {
            List<EmailPO> list = emailAO.getTeamEmailList(teamId);
            te = new TeamEmail(teamId, getSeqAtomic(teamId), list);
            teamEmailMap.put(teamId, te);
            //GameSource.checkGcData(teamId); 邮件不回收
        }
        return te;
    }

    /**
     * 只插入DB的方法；
     * 这样插入自增ID会有问题
     *
     * @param teamId
     * @return
     */
    @Deprecated
    public TeamEmail getTeamEmailNotOnline(long teamId) {
        return new TeamEmail(teamId, getSeqAtomic(teamId), null);
    }

//    @Override
//    public void offline(long teamId) {
//        teamEmailMap.remove(teamId);
//    }

//    @Override
//    public void dataGC(long teamId) {
//        teamEmailMap.remove(teamId);
//    }

    /**
     * 邮件列表
     */
    @ClientMethod(code = ServiceCode.Email_List)
    public void showList() {
        long teamId = getTeamId();
        sendMessage(getEmailListData(teamId));
    }

    public EmailPB.TeamEmailData getEmailListData(long teamId) {
        return getTeamEmailData(getTeamEmail(teamId));
    }

    public EmailPB.TeamEmailData getTeamEmailData(TeamEmail te) {
        Builder data = EmailPB.TeamEmailData.newBuilder();
        List<EmailPB.EmailData> list = Lists.newArrayList();
        for (Email email : te.getEmailList()) {
            list.add(getEmailData(email));
        }
        data.addAllEmailList(list);
        return data.build();
    }

    public EmailPB.EmailData getEmailData(Email email) {
        return EmailPB.EmailData.newBuilder()
                .setId(email.getId())
                .setType(email.getType())
                .setViewId(email.getViewId())
                .setTitle(email.getTitle())
                .setContent(email.getContent())
                .setStatus(email.getStatus())
                .setCreateTime(email.getCreateTime().getMillis())
                .addAllPropList(getPropListData(email.getAwardConfig()))
                .build();
    }

    private List<PropPB.PropSimpleData> getPropListData(String awardConfig) {
        List<PropSimple> list = PropSimple.getPropBeanByStringNotConfig(awardConfig);
        List<PropPB.PropSimpleData> dataList = Lists.newArrayList();
        for (PropSimple p : list) {
            dataList.add(PropPB.PropSimpleData.newBuilder()
                    .setPropId(p.getPropId())
                    .setNum(p.getNum())
                    .build());
        }
        return dataList;
    }

    /**
     * 只读
     */
    @ClientMethod(code = ServiceCode.Email_Read)
    public void readEmail(int id) {
        // 只读类型，只更新状态
        long teamId = getTeamId();
        TeamEmail te = getTeamEmail(teamId);
        if (!te.checkEmail(id)) {
            log.debug("找不到邮件{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        Email email = te.getEmail(id);
        if (email.getStatus() == 0 && email.getAwardConfig().equals("")) {
            email.setStatus(1);
            email.save();
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 读取
     * 接收，领取
     */
    @ClientMethod(code = ServiceCode.Email_Receive)
    public void receiveEmail(int id) {
        // 只读类型，只更新状态
        long teamId = getTeamId();
        TeamEmail te = getTeamEmail(teamId);
        if (!te.checkEmail(id)) {
            log.debug("找不到邮件{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return; // 找不到邮件
        }
        Email email = te.getEmail(id);
        if (email.getStatus() == 1) {
            log.debug("已领{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        if (!email.getAwardConfig().equals("")) {
            List<PropSimple> props = PropSimple.getPropBeanByStringNotConfig(email.getAwardConfig());
            propManager.addPropList(teamId, props, true, ModuleLog.getModuleLog(EModuleCode.邮件, email.getTitle()));
        }
        email.setStatus(1);
        email.save();
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).setBigNum(id).build());
    }

    /**
     * 删除，只能删除已读取
     */
    @ClientMethod(code = ServiceCode.Email_Delete)
    public void delEmail(int id) {
        long teamId = getTeamId();
        TeamEmail te = getTeamEmail(teamId);
        if (!te.checkEmail(id)) {
            log.debug("找不到邮件{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        Email email = te.getEmail(id);
        if (email.getStatus() == 0) {
            log.debug("未读邮件{}", id);
            sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Error.code).build());
            return;
        }
        te.deleteEmail(id);
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 一键领取
     * 领取所有未领有附件的
     */
    @ClientMethod(code = ServiceCode.Email_Receive_All)
    public void receiveAll() {
        long teamId = getTeamId();
        TeamEmail te = getTeamEmail(teamId);
        for (Email e : te.getEmailList()) {
            if (e.getStatus() == 1) { continue; }
            if (!e.getAwardConfig().equals("")) {
                List<PropSimple> props = PropSimple.getPropBeanByStringNotConfig(e.getAwardConfig());
                propManager.addPropList(teamId, props, true, ModuleLog.getModuleLog(EModuleCode.邮件, e.getTitle()));
            }
            e.setStatus(1);
            e.save();
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 一键删除
     * 删除所有已读，已领取
     */
    @ClientMethod(code = ServiceCode.Email_Delete_All)
    public void delAllReceive() {
        long teamId = getTeamId();
        TeamEmail te = getTeamEmail(teamId);
        for (Email e : te.getEmailList()) {
            if (e.getStatus() == 0) { continue; }
            te.deleteEmail(e.getId());
        }
        sendMessage(DefaultPB.DefaultData.newBuilder().setCode(ErrorCode.Success.code).build());
    }

    /**
     * 发送动态参数模版邮件
     *
     * @param teamId        球队id
     * @param viewId        模版id {@link EmailViewBean}
     * @param titleParams   标题参数
     * @param contentParams 正文参数
     * @param props         道具列表
     */
    public void sendEmailWithParamTemplate(long teamId,
                                           int viewId,
                                           Map<String, String> titleParams,
                                           Map<String, String> contentParams,
                                           List<PropSimple> props) {
        sendEmailFinal(teamId, EEmailType.Param_Template.getType(), viewId,
                getParams(titleParams),
                getParams(contentParams),
                PropSimple.getPropStringByListNotConfig(props));
    }

    /**
     * 发送动态参数模版邮件
     *
     * @param teamId        球队id
     * @param viewId        模版id {@link EmailViewBean}
     * @param titleParams   标题参数
     * @param contentParams 正文参数
     * @param props         道具列表
     */
    public void sendEmailWithParamTemplate(long teamId,
                                           int viewId,
                                           List<String> titleParams,
                                           List<String> contentParams,
                                           List<PropSimple> props) {
        try {
            sendEmailFinal(teamId, EEmailType.Param_Template.getType(), viewId,
                    getParams(titleParams),
                    getParams(contentParams),
                    PropSimple.getPropStringByListNotConfig(props));
        } catch (Exception e) {
            log.error("mail " + e.getMessage(), e);
        }
    }

    private static String getParams(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.entrySet().stream()
                .map(e -> e.getKey() + ":" + e.getValue())
                .collect(Collectors.joining(","));
    }

    private static String getParams(List<String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        return params.stream().collect(Collectors.joining(","));
    }

    /**
     * 发送邮件
     *
     * @param teamId
     * @param type
     * @param viewId
     * @param title
     * @param content
     * @param awardConfig 格式 tid:num,tid:num
     */
    public void sendEmailFinal(long teamId, int type, int viewId, String title, String content, String awardConfig) {
        if (GameSource.isNPC(teamId)) {
            return;
        }
        // 验证奖励是否合法
        if (awardConfig != null && !awardConfig.equals("") && awardConfig.contains(":")) {
            try {
                List<PropSimple> list = PropSimple.getPropBeanByStringNotConfig(awardConfig);
                for (PropSimple ps : list) {
                    if (PropConsole.getProp(ps.getPropId()) == null) {
                        awardConfig = "";
                        break;
                    }
                }
            } catch (Exception e) {
                // 有异常的奖励直接设置空
                awardConfig = "";
            }
        } else {
            awardConfig = "";
        }
        Email email = getTeamEmail(teamId).sendEmail(type, viewId, title, content, awardConfig);
        sendMessage(teamId, getEmailData(email), ServiceCode.Email_New_Topic);
    }

    /**
     * 根据模块内容发邮件
     *
     * @param teamId
     * @param viewId        模板ID
     * @param contentParams 邮件模板参数
     * @param awardConfig   奖励
     */
    public void sendEmail(long teamId, int viewId, String contentParams, String awardConfig) {
        EmailViewBean bean = EmailConsole.getEmailView(viewId);
        if (bean == null) {
            bean = new EmailViewBean();
            bean.setId(0);
            bean.setType(1);
            bean.setTitle("标题");
            bean.setContent("找不到系统邮件模板");
        }
        sendEmailFinal(teamId, bean.getType(), bean.getId(), bean.getTitle(), contentParams, awardConfig);
    }

    /**
     * 给所有玩家发邮件
     *
     * @param type         : 1,所有球队； 2,所有在线；  3,自定义球队（teamIds参数，多个id用,分割）
     * @param title        : 标题
     * @param content      : 内容
     * @param awardConfig: 奖励配置，格式：  id:num:0,id:num:0...
     * @param teamIds:     指定球队ID，type=3时有效，多个id用,分割
     */
    @RPCMethod(code = CrossCode.WebManager_sendEmail, pool = EServerNode.Logic, type = ERPCType.NONE)
    public void gmSendTAll(int type, String title, String content, String awardConfig, String teamStrIds) {
        Set<Long> teamIds = null;
        if (type < 0 || type > 3) {
            return;
        } else if (type == 1) {
            teamIds = teamManager.getAllTeam();
        } else if (type == 2) {
            teamIds = GameSource.getUsers().stream()
                    .map(u -> u.getTeamId())
                    .distinct()
                    .collect(Collectors.toSet());
        } else if (type == 3) {
            teamIds = Arrays.stream(teamStrIds.split(","))
                    .filter(s -> !s.equals(""))
                    .mapToLong(s -> Long.valueOf(s))
                    .filter(s -> teamManager.existTeam(s))
                    .boxed()
                    .collect(Collectors.toSet());
        }
        // 发送邮件奖励
        sendEmailList(teamIds, type, 0, title, content, awardConfig);
    }

    /**
     * 给指定的列表发邮件
     */
    public void sendEmailList(Collection<Long> teamIds, int type, int viewId, String title, String content, String awardConfig) {
        if (teamIds == null || teamIds.size() == 0) {
            return;
        }
        for (long teamId : teamIds) {
            sendEmailFinal(teamId, type, viewId, title, content, awardConfig);
        }
    }

    @Override
    public int getOrder() {
        return ManagerOrder.Email.getOrder();
    }

    /**
     * 检查系统补偿邮件
     */
    public void checkSystemSendToTeam() {
        DateTime now = DateTime.now();
        if (now.minuteOfHour().get() % 5 != 0) {
            return;
        }
        //
        List<Integer> sendList = emailAO.getTeamEmailSendList();
        if (sendList == null) { return; }
        //        for (int snedId : sendList) {
        //            //  邮件补偿，暂不用此方案
        //        }
    }

    /**
     * 定时检查在线用户的系统邮件，并推送
     */
    public void checkTeamRevices() {

    }

}
