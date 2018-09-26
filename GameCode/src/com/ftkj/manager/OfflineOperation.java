package com.ftkj.manager;

/**
 * 离线操作
 *
 * @author tim.huang
 * 2015年12月30日
 */
public interface OfflineOperation {
    /**
     * 球队离线. 清除缓存中的数据, 额外可以做少量数据库操作.
     * <p>
     * 如果相关数据已经在 离线队列中, 以下情况不会触发此方法:
     * <ul>
     * <li>球队是NPC</li>
     * <li>球队在线</li>
     * </ul>
     */
    void offline(long teamId);

    /**
     * 清除球队数据, 在球队离线过程中, 如果其他玩家或者系统获取球队及球队模块信息, 相关信息会被加载到内存中,
     * 需要定时调度清除内存中的数据, 防止OOM.
     * <p>
     * 如果相关数据已经在 GC队列中, 以下情况不会触发此方法:
     * <ul>
     * <li>球队是NPC</li>
     * <li>球队在线</li>
     * <li>球队在离线队列中</li>
     * </ul>
     */
    void dataGC(long teamId);

    /**
     * 离线调用顺序. 按从小到大的顺序调用.
     */
    default int offlineOrder() {
        return Integer.MIN_VALUE;
    }
}
