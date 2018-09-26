package com.ftkj.server.instance;

/**
 * 实例操作
 *
 * @author tim.huang
 * 2015年12月11日
 */
public interface InstanceOperation {

    /**
     * 实例化之后执行的方法
     */
    void instanceAfter();

    /** 初始化时处理策划配置 */
    default void initConfig() {
    }

    /** 初始化顺序, 按自然顺序排序 */
    int getOrder();

}
