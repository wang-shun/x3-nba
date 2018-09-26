package com.ftkj.x3.net;

import com.codahale.metrics.MetricRegistry;
import com.ftkj.xxs.net.stats.ClientMessageMetric;
import com.ftkj.xxs.net.stats.ClientMessageMetric.ClientCodeStat;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * 客户端消息统计信息.
 *
 * @author luch
 */
public abstract class X3MessageMetric extends ClientMessageMetric<ClientCodeStat> {
    public X3MessageMetric(MetricRegistry registry, ExecutorService executorService) {
        super(registry, executorService);
    }

    public X3MessageMetric(MetricRegistry registry, ExecutorService executorService, String globalPrefix, int unknownCode, int allCode) {
        super(registry, executorService, globalPrefix, unknownCode, allCode);
    }

    //    /** 统计一个发送消息状态 */
    //    public void addSendMsgStat(Code code, int transmittedLength) {
    //        super.addSendMsgStat(code.getCode(), transmittedLength);
    //    }
    //
    //    /** 统计一个接收到的消息的状态 */
    //    public void addReceivedMsgStat(Code code, int receiveLength, int time) {
    //        super.addReceivedMsgStat(code.getCode(), receiveLength, time);
    //    }

    @Override
    protected abstract Iterator<ClientCodeStat> initAllCodeStats();

    @Override
    protected ClientCodeStat newCodeStat(int code, String name) {
        return new ClientCodeStat(code, name);
    }

}
