package com.ftkj.server.socket;

/**
 * 服务器配置
 *
 * @author tim.huang
 * 2015年12月1日
 */
public interface SocketServerConfig {

    /**
     * 获得Service层路径
     *
     * @return
     */
    String getServicePackgePath();

    String getManagerPackgePath();

    String getCommonPackgePath();

    String getActiveManagerPackgePath();

    String getAOPackgePath();

    String getDAOPackgePath();

    String getJobPackgePath();

    String getJSScriptPath();

    String getIP();

    int getPort();

    String getPath();

    ClassLoader getClassLoader();

    /** {@com.ftkj.server.socket.GameServerManager} pool name */
    default String getPoolName() {
        return "x3";
    }
}
