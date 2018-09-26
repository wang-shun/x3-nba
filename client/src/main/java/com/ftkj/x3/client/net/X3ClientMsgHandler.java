package com.ftkj.x3.client.net;

import com.ftkj.enums.ErrorCode;
import com.ftkj.server.ServiceCode;
import com.ftkj.server.ServiceCode.Code;
import com.ftkj.x3.client.ClientConfig;
import com.ftkj.x3.client.proto.Ret;
import com.ftkj.xxs.client.net.AsyncListenerFactory;
import com.ftkj.xxs.client.net.ClientChannelHolder;
import com.ftkj.xxs.client.net.ClientMsgHandler;
import com.ftkj.xxs.net.Message;
import com.ftkj.xxs.net.stats.ClientMessageMetric;
import com.ftkj.xxs.proto.XxsCode;
import com.ftkj.xxs.proto.XxsModule;
import com.ftkj.xxs.proto.XxsRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author luch
 */
@Component
public class X3ClientMsgHandler extends ClientMsgHandler {
    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    @Override
    public void setMessageMetric(ClientMessageMetric messageMetric) {
        super.setMessageMetric(messageMetric);
    }

    @Autowired
    @Override
    public void setAsyncListenerFactory(AsyncListenerFactory asyncListenerFactory) {
        super.setAsyncListenerFactory(asyncListenerFactory);
    }

    @Autowired
    @Override
    public void setChannelHolder(ClientChannelHolder channelHolder) {
        super.setChannelHolder(channelHolder);
    }

    @Override
    public Message msgCopy(int id, int code, int ret) {
        return new ClientReqMessage(id, code);
    }

    @Override
    public Message msgCopy(int id, int code, int ret, byte[] data) {
        return new ClientReqMessage(id, code, data);
    }

    @Override
    public XxsCode gmCode() {
        return Holder.GM_CODE;
    }

    public static final class ClientCode extends Code implements XxsCode {
        public ClientCode(int code, String name) {
            super(code, name);
        }

        public ClientCode(Code code) {
            super(code.getCode(), code.getName());
        }

        @Override
        public XxsModule getModule() {
            return null;
        }

        @Override
        public XxsModule module() {
            return null;
        }
    }

    private static final class Holder {
        private static final ClientCode GM_CODE = new ClientCode(Code.convert(ServiceCode.GM));
        private static final Ret GmDisable = Ret.convert(ErrorCode.GmDisable.getCode());
    }

    @Override
    public boolean isGmEnable() {
        return clientConfig.isGm();
    }

    @Override
    public XxsRet gmDisableRet() {
        return Holder.GmDisable;
    }

    @Override
    public XxsRet clientErrorRet() {
        return Ret.convert(ErrorCode.GmClient.getCode());
    }
}
