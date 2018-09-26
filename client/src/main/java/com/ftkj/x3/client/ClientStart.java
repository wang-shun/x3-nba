package com.ftkj.x3.client;

import com.ftkj.console.CM;
import com.ftkj.console.PlayerConsole;
import com.ftkj.manager.common.CacheManager;
import com.ftkj.tool.redis.JedisUtil;
import com.ftkj.tool.redis.Jredis;
import com.ftkj.util.IPUtil;
import com.ftkj.x3.client.console.ClientConsole;
import com.ftkj.x3.client.net.X3AsyncListenerFactory;
import com.ftkj.x3.client.net.X3ClientBootStrap;
import com.ftkj.x3.net.X3MessageMetric;
import com.ftkj.xxs.client.XxsClientStart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 客户端关闭
 *
 * @author luch
 */
@Component
public class ClientStart implements XxsClientStart {
    private static final Logger log = LoggerFactory.getLogger(ClientStart.class);
    @Autowired
    private ClientConfig clientConfig;
    @Autowired
    private X3ClientBootStrap clientBootStrap;
    @Autowired
    private X3AsyncListenerFactory asyncListenerFactory;
    @Autowired
    private ClientConsole clientConsole;
    @Autowired
    private X3MessageMetric x3MessageMetric;

    @Override
    public void init(GenericApplicationContext ctx) {
        clientBootStrap.init();
        asyncListenerFactory.init();
        CM.debugExcelPath = clientConfig.getExcelPath();
        log.info("client confg {}", clientConfig.configString());
        if (clientConfig.isCacheL2Enable()) {
            CM.setJedisUtil(new JedisUtil(initRedis()));
        }
        CM.init(false);
        CacheManager.initCacheFromClient();
        CacheManager.validateCache();
        clientConsole.init();
        log.info("client confg done. xplayer size {}", PlayerConsole.getCreateTeamXList().size());
        x3MessageMetric.init();
    }

    private Jredis initRedis() {
        Jredis j = new Jredis();
        List<InetSocketAddress> addrs = IPUtil.getAddresses(clientConfig.getCacheL2Address());
        InetSocketAddress addr = addrs.get(0);
        j.setHost(addr.getHostName());
        j.setPort(addr.getPort());
        j.setDatabase(0);
        if (clientConfig.getCacheL2Password() != null && !clientConfig.getCacheL2Password().isEmpty()) {
            j.setPassword(clientConfig.getCacheL2Password());
        }
        j.setConnectionCount(10);
        return j;
    }
}
