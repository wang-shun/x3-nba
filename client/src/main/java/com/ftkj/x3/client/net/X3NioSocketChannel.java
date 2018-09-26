package com.ftkj.x3.client.net;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

/**
 * 服务器端使用的channel, 带有登录信息.
 *
 * @author luch
 */
public class X3NioSocketChannel extends NioSocketChannel implements X3Channel {
    /** 分区id */
    private int partitionId = 0;
    private String partitionIdStr;
    /** channel 关联的帐号id */
    private long accountId = 0;
    private String accountIdStr;
    /** channel 关联的玩家id */
    private long userId = 0;
    private String userIdStr;
    /** Cache for the string representation of this channel */
    private boolean strValActive;
    private String strVal;

    public X3NioSocketChannel() {
        super();
    }

    public X3NioSocketChannel(Channel parent, SocketChannel socket) {
        super(parent, socket);
    }

    @Override
    public String getPartitionId() {
        return partitionIdStr;
    }

    /** 玩家是否已经登录 */
    public boolean isUserLogined() {
        return userId > 0;
    }

    /** 帐号是否已经登录 */
    public boolean isAccountLogined() {
        return accountId > 0;
    }

    public String getAccountId() {
        return accountIdStr;
    }

    public String getUserId() {
        return userIdStr;
    }

    @Override
    public void setAccountId(String accountId) {
        this.accountIdStr = accountId;
        this.accountId = Long.valueOf(accountId);
        strVal = null;
    }

    @Override
    public void setUserId(String teamId) {
        this.userIdStr = teamId;
        this.userId = Long.valueOf(teamId);
        strVal = null;
    }

    /**
     * Returns the {@link String} representation of this channel.  The returned
     * string contains the {@linkplain #hashCode() ID}, {@linkplain #localAddress() local address},
     * and {@linkplain #remoteAddress() remote address} of this channel for
     * easier identification.
     * <p>
     */
    @Override
    public String toString() {
        boolean active = isActive();
        if (strValActive == active && strVal != null) {
            return strVal;
        }

        SocketAddress remoteAddr = remoteAddress();
        SocketAddress localAddr = localAddress();
        if (remoteAddr != null) {
            StringBuilder buf = new StringBuilder(96 + 50)
                    .append("[id: 0x")
                    .append(id().asShortText())
                    .append(", L:")
                    .append(localAddr)
                    .append(active ? " - " : " ! ")
                    .append("R:")
                    .append(remoteAddr)
                    .append(", aid ")
                    .append(accountId)
                    .append(" uid ")
                    .append(userId)
                    .append(']');
            strVal = buf.toString();
        } else if (localAddr != null) {
            StringBuilder buf = new StringBuilder(64 + 50)
                    .append("[id: 0x")
                    .append(id().asShortText())
                    .append(", L:")
                    .append(localAddr)
                    .append(", aid ")
                    .append(accountId)
                    .append(" uid ")
                    .append(userId)
                    .append(']');
            strVal = buf.toString();
        } else {
            StringBuilder buf = new StringBuilder(16)
                    .append("[id: 0x")
                    .append(id().asShortText())
                    .append(']');
            strVal = buf.toString();
        }

        strValActive = active;
        return strVal;
    }
}
