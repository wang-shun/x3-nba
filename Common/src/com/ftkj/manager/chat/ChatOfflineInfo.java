
package com.ftkj.manager.chat;

import java.io.Serializable;

/**
 * 离线聊天信息
 *
 * @author jiangqin
 * @date 2018-05-21
 */
public class ChatOfflineInfo implements Serializable {
    private static final long serialVersionUID = 1363058837024221039L;
    /** 留言球队ID */
    private long teamId;
    /** 消息内容 */
    private String content;
    /** 消息创建时间 */
    private long createTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
}
