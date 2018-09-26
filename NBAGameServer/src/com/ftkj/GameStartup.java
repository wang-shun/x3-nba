package com.ftkj;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ftkj.manager.User;
import com.ftkj.manager.logic.log.GameOlineLogManager;
import com.ftkj.server.GameSource;
import com.ftkj.server.socket.GameSocketServer;
import com.ftkj.server.socket.SocketServerConfig;
import com.ftkj.util.IPUtil;

/**
 * 服务器启动入口
 *
 * @author tim.huang
 * 2015年12月11日
 */
public class GameStartup {
    private static final Logger log = LoggerFactory.getLogger(GameStartup.class);

    public static void main(String[] args) throws Throwable {
        //		System.out.println(System.getProperty("java.class.path"));
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
            port = 8038;
//            port = 7038;
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
                return GameStartup.class.getResource("/").getPath();
            }

            @Override
            public String getIP() {
                return ip;
            }

            @Override
            public String getManagerPackgePath() {
                return "com.ftkj.manager.logic";
            }

            @Override
            public String getActiveManagerPackgePath() {
                return "com.ftkj.manager.active";
            }

            @Override
            public String getCommonPackgePath() {
                return "com.ftkj";
            }

            @Override
            public String getAOPackgePath() {
                return "com.ftkj.db.ao";
            }

            @Override
            public String getDAOPackgePath() {
                return "com.ftkj.db.dao";
            }

            @Override
            public String getJobPackgePath() {
                return "com.ftkj.job.logic";
            }

            @Override
            public String getJSScriptPath() {
                return jsPath;
            }

            @Override
            public ClassLoader getClassLoader() {
                return GameStartup.class.getClassLoader();
            }

            @Override
            public String getPoolName() {
                return "logic";
            }
        };
        GameSource.net = net == 1;
        try {
            GameSocketServer g = new GameSocketServer(config);
            g.startNode(user -> GameOlineLogManager.Log(user.getTeamId(),
                    user.getLoginTime(),
                    "".equals(user.getVal(User.Level_Key)) ? 0 : Integer.parseInt(user.getVal(User.Level_Key))));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            System.exit(-1);
            throw e;
        }
        
    }

}
