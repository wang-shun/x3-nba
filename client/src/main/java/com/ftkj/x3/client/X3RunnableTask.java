package com.ftkj.x3.client;

import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.ServiceCode.Code;
import com.ftkj.x3.client.X3SpringConfig.AppConfig;
import com.ftkj.x3.client.net.UserClient;
import com.ftkj.x3.client.net.X3ClientMsgHandler.ClientCode;
import com.ftkj.x3.client.proto.ClientPbUtil;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.helper.LoginHelper;
import com.ftkj.x3.client.task.helper.X3TaskHelper;
import com.ftkj.xxs.client.RunnableTask;
import com.ftkj.xxs.client.XxsClientStart;
import com.ftkj.xxs.client.XxsClientStop;
import com.ftkj.xxs.net.Message;
import com.ftkj.xxs.util.Server;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author luch
 */
public abstract class X3RunnableTask extends RunnableTask implements X3Task {
    @Autowired
    private LoginHelper loginHelper;
    @Autowired
    private ClientConfig clientConfig;

    public static final int SEC = 1000;
    public static final int MINUT = 60 * SEC;
    public static final int HOUR = 60 * MINUT;

    /** 初始化, 并且运行 task */
    public final void run(String activeSpringProfile, String[] args, boolean autoClose) {
        initAndRun(AppConfig.class, activeSpringProfile, args, autoClose);
    }

    protected abstract Ret run0(String[] args);

    @Override
    protected Class<? extends XxsClientStart> getClientStartClazz() {
        return ClientStart.class;
    }

    @Override
    protected Class<? extends XxsClientStop> getClientStopClazz() {
        return ClientStop.class;
    }

    @Override
    public Ret success() {
        return Ret.success();
    }

    protected Ret succ() {
        return Ret.success();
    }

    @Override
    protected void beforeStart() {
        super.beforeStart();
    }

    public UserClient loginMainAccount() {
        return loginHelper.loginMainAccount();
    }

    public UserClient loginMainAccount(Server server) {
        return loginHelper.loginMainAccount(server);
    }

    public UserClient loginAnotherAccount() {
        return loginHelper.loginAnotherAccount();
    }

    public UserClient loginBotAccount() {
        return loginHelper.loginBotAccount();
    }

    public UserClient login(long accountId, String teamName) {
        return loginHelper.login(accountId, teamName);
    }

    public UserClient login(Server server, long accountId, String teamName) {
        return loginHelper.login(server, accountId, teamName);
    }

    public void logout(UserClient uc) {
        loginHelper.logout(uc);
    }

    public Resp<UserClient> login2(long accountId, String teamName) {
        return loginHelper.login2(accountId, teamName);
    }

    public Resp<UserClient> login2(Server server, long accountId, String teamName, boolean printSplitLineLog, boolean forceLogin) {
        return loginHelper.login2(server, accountId, teamName, printSplitLineLog, forceLogin);
    }

    protected boolean isSimpleLog() {
        return clientConfig.isSimpleLog();
    }

    protected boolean isGMEnable() {
        return clientConfig.isGm();
    }

    public static DefaultData parseFrom(Message msg) {
        return X3Task.parseFrom(DefaultData.getDefaultInstance(), msg);
    }

    public static <T extends com.google.protobuf.Message> T parseFrom(T parser, Message msg) {
        return X3TaskHelper.parseFrom(parser, msg);
    }

    public static String shortDebug(com.google.protobuf.Message protobufMsg) {
        return ClientPbUtil.shortDebug(protobufMsg);
    }

    public static String shortDebug(Collection<? extends com.google.protobuf.Message> protobufMsg) {
        return protobufMsg.stream()
            .map(X3RunnableTask::shortDebug).collect(Collectors.joining(", "));
    }

    public static ClientCode code(int code) {
        return new ClientCode(Code.convert(code));
    }
}
