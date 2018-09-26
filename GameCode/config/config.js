//importPackage(java.util);
//importPackage(com.ftkj.db.conn.dao);
importClass(com.ftkj.db.conn.dao);

$.setShardid(101);
$.setCharset("zh");
//$.addServer(new Server(ServerType.Server_nba_data,"127.0.0.1",2600));
//$.addServer(new Server(ServerType.Server_nba_game,"127.0.0.1",2500));
//$.addServer(new Server(ServerType.Server_nba_game,"127.0.0.1",2600));


//---redis--------------------------------------------
// j = new Jredis();
// j.setHost("192.168.10.181");
// j.setPort(6379);
// j.setDatabase(0);
// j.setPassword("xgame2016");
// j.setConnectionCount(150);

//$.addResource(ResourceType.Jredis,new JedisUtil(j));
/**

jdbc_nba_game= new Jdbc();
jdbc_nba_game.setUsername("root");
jdbc_nba_game.setPassword("123456");
jdbc_nba_game.setDriver("com.mysql.jdbc.Driver");
jdbc_nba_game.setUrl("jdbc:mysql://192.168.10.231:3306/x_game_101?useUnicode=true&characterEncoding=utf8");
jdbc_nba_game.setMinConnectionsPerPartition(20);
jdbc_nba_game.setMaxConnectionsPerPartition(50);
jdbc_nba_game.setAcquireIncrement(20);
jdbc_nba_game.setPartitionCount(5);
jdbc_nba_game.setIdleConnectionTestPeriodInSeconds(60*5);
jdbc_nba_game.setStatementsCacheSize(400);

$.addResource(ResourceType.DB_nba_game,new Database(jdbc_nba_game));

*/