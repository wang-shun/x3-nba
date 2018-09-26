package com.ftkj;

import com.esotericsoftware.minlog.Log;
import com.ftkj.server.GameSource;
import com.ftkj.server.socket.GameSocketServer;
import com.ftkj.server.socket.SocketServerConfig;
import com.ftkj.util.IPUtil;

import java.io.File;

/**
 * 服务器启动入口
 *
 * @author tim.huang
 * 2015年12月11日
 */
public class GamePKnodeStartup {

    public static void main(String[] args) throws Throwable {
        //		System.out.println(System.getProperty("java.class.path"));
        Log.set(Log.LEVEL_TRACE);
        final int port;
        final String ip;
        final String jsPath;
        final int net;
        //开启调试模式
        //		GameSource.updateDebug(false);
        if (args.length >= 3) {
            ip = args[0];
            port = Integer.parseInt(args[1]);
            jsPath = args[2];
        } else {
            ip = IPUtil.getLocalIp();
            port = 8068;
            jsPath = System.getProperty("user.dir") + File.separatorChar + "config" + File.separatorChar + "config.js";
        }
        if (args.length >= 4) {
            net = Integer.parseInt(args[3]);
        } else {
            net = 1;
        }
        SocketServerConfig config = new SocketServerConfig() {

            @Override
            public String getServicePackgePath() {
                return "";
            }

            @Override
            public int getPort() {
                return port;
            }

            @Override
            public String getPath() {
                return GamePKnodeStartup.class.getResource("/").getPath();
            }

            @Override
            public String getIP() {
                return ip;
            }

            @Override
            public String getManagerPackgePath() {
                return "com.ftkj.manager.pk";
            }

            @Override
            public String getCommonPackgePath() {
                return "com.ftkj";
            }

            @Override
            public String getActiveManagerPackgePath() {
                return "com.ftkj.manager.active";
            }

            @Override
            public String getAOPackgePath() {
                return "com.ftkj.db.ao.pk";
            }

            @Override
            public String getDAOPackgePath() {
                return "com.ftkj.db.dao.pk";
            }

            @Override
            public String getJobPackgePath() {
                return "com.ftkj.job";
            }

            @Override
            public String getJSScriptPath() {
                return jsPath;
            }

            @Override
            public ClassLoader getClassLoader() {
                return GamePKnodeStartup.class.getClassLoader();
            }

        };
        GameSource.net = net == 1;
        GameSocketServer g = new GameSocketServer(config);
        if (GameSource.pool.equals("route")) {
            g.startRoute();
        } else {
            g.startNode();
        }
    }

}
