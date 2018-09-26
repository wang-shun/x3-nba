package com.ftkj.x3.client.net;

import com.ftkj.enums.EMoneyType;
import com.ftkj.enums.EPayType;
import com.ftkj.enums.ErrorCode;
import com.ftkj.proto.DefaultPB.DefaultData;
import com.ftkj.server.GMCode.GmCommand;
import com.ftkj.server.ServiceCode.Code;
import com.ftkj.x3.client.X3RunnableTask;
import com.ftkj.x3.client.model.ClientUser;
import com.ftkj.x3.client.net.X3ClientMsgHandler.ClientCode;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.x3.client.task.GmMessage;
import com.ftkj.x3.client.task.helper.X3TaskHelper;
import com.ftkj.x3.client.util.MessageUtil;
import com.ftkj.xxs.client.net.XxsUserClient;
import com.ftkj.xxs.net.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * 用户客户端
 *
 * @author luch
 */
public class UserClient extends XxsUserClient {
    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private final GmMessage gm = new GmMessage();

    public UserClient(X3ClientMsgHandler msgHandler) {
        super(msgHandler);
    }

    @Override
    public ClientUser getUser() {
        return (ClientUser) super.getUser();
    }

    @Override
    public ClientUser user() {
        return (ClientUser) super.user();
    }

    public long tid() {
        return user().getTid();
    }

    public long getTid() {
        return user().getTid();
    }

    @Override
    public boolean isSuccess(int retCode) {
        return Ret.success().ret() == retCode;
    }

    /**
     * 消息是否处理失败
     *
     * @return true 处理失败
     */
    public boolean isError(DefaultData msg) {
        return msg.getCode() != ErrorCode.Ret_Success;
    }

    /**
     * 消息是否处理失败
     *
     * @return true 处理失败
     */
    public boolean isError(int retCode) {
        return retCode != ErrorCode.Ret_Success;
    }

    public Ret reqCommon(UserClient uc, ClientUser cu, int reqServiceCode, int pushCode, Object... reqParams) {
        ClientCode reqCode = new ClientCode(Code.convert(reqServiceCode));
        AsyncWaitLatch awl = null;
        if (pushCode > 0) {
            awl = uc.createAysnWaitLatch(new ClientCode(Code.convert((pushCode))));
        }
        Message msg = uc.writeAndGet(MessageUtil.createReq(reqServiceCode, reqParams));
        DefaultData resp = X3RunnableTask.parseFrom(msg);
        if (uc.isError(resp)) {
            Ret ret = resp != null ? Ret.convert(resp.getCode()) : Ret.nulll();
            log.warn("tid {} req {} params {}. fail {}", cu.tid(), reqCode.getCodeName(), Arrays.toString(reqParams), ret);
            return ret;
        }
        if (awl != null) {
            uc.waitAysnLatchRelease(awl);
        }
        log.info("tid {} req {} params {} succ", uc.tid(), reqCode.getCodeName(), Arrays.toString(reqParams));
        return Ret.success();
    }

    public <T extends com.google.protobuf.Message> T req(UserClient uc, ClientUser cu,
                                                         int reqServiceCode,
                                                         T respParser,
                                                         Object... reqParams) {
        ClientCode reqCode = new ClientCode(Code.convert(reqServiceCode));
        Message msg = uc.writeAndGet(MessageUtil.createReq(reqServiceCode, reqParams));
        T resp = X3TaskHelper.parseFrom(respParser, msg);
        log.info("tid {} req {} {} params {} succ",
                cu.tid(), reqServiceCode, reqCode.getCodeName(), Arrays.toString(reqParams));
        return resp;
    }
    //====================== gm 命令 ==========================

    /** 添加道具 */
    public ClientMessage gmAddProp(int propId, int num) {
        return gm.addProp(tid(), propId, num);
    }

    /** 添加货币 */
    public ClientMessage gmAddMoney(int num, EPayType type) {
        return gm.addMoney(tid(), num, type.getType());
    }

    /** 添加货币 */
    public ClientMessage gmAddMoney(int money, int gold, int exp) {
        return gm.addMoney(tid(), money, gold, exp);
    }

    /** 更新货币, 正加负减 */
    public ClientMessage gmCurrency(EMoneyType mt, int num) {
        return createGM(GmCommand.Team_Currency, mt.getConfigName(), num);
    }

    protected ClientReqMessage createGM(GmCommand gc, Object... params) {
        return MessageUtil.createGM(gc, params);
    }
}
