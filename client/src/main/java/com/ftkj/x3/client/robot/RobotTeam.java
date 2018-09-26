package com.ftkj.x3.client.robot;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 机器人状态
 *
 * @author luch
 */
public class RobotTeam {
    //统计信息
    private AtomicLong loginCount = new AtomicLong();
    private AtomicLong logoutCount = new AtomicLong();
    /** 进行到那个阶段 */
    private Stage stage;
    //

    /** 操作阶段. 按模块划分 */
    public enum Stage {
        Prop,
        Player
    }
}
