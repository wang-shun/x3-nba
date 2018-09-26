package com.ftkj.x3.client;

import com.ftkj.x3.client.net.X3ClientBootStrap;
import com.ftkj.x3.client.net.X3ClientChannelHolder;
import com.ftkj.x3.net.X3MessageMetric;
import com.ftkj.xxs.client.XxsClientStop;
import com.ftkj.xxs.net.stats.AlignFormat;
import com.ftkj.xxs.net.stats.MessageSnapshot;
import io.netty.channel.ChannelFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 客户端关闭
 *
 * @author luch
 */
@Component
public class ClientStop implements XxsClientStop {
    private static final Logger log = LoggerFactory.getLogger(ClientStop.class);
    @Autowired
    private X3ClientBootStrap clientBootStrap;
    @Autowired
    private X3MessageMetric x3MessageMetric;
    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    private X3ClientChannelHolder clientChannelHolder;

    @Override
    public void shutdown() {
        log.info("close client...");
        List<ChannelFuture> cfs = clientChannelHolder.channelGroup().closeAllClientChannel();
        for (ChannelFuture cf : cfs) {
            try {
                cf.await(Integer.MAX_VALUE);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        clientBootStrap.destroy();
        log.info("gracefulShutdownNow message metric");
        x3MessageMetric.gracefulShutdownNow(10);
        if (clientConfig.isLogMsgStats()) {
            MessageSnapshot ms = x3MessageMetric.getSnapshot();
            String stat = AlignFormat.newDefaultFormat().formatString(ms);
            log.info("msg stats\n{}", stat);
        }
    }
}
