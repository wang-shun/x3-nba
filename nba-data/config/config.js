load("nashorn:mozilla_compat.js");
importPackage(com.ftkj.invoker);
importPackage(com.ftkj.conn);
importPackage(com.ftkj.jredis);
importPackage(java.util);

//---jdbc-account-------------------------------------------
$.setCharset("zh");


//---jdbc-data-------------------------------------------

jdbc_nba_data= new Jdbc();
jdbc_nba_data.setUsername("root");
jdbc_nba_data.setPassword("zgame2017");
jdbc_nba_data.setDriver("com.mysql.jdbc.Driver");
jdbc_nba_data.setUrl("jdbc:mysql://192.168.10.181:3306/nba_data?useUnicode=true&characterEncoding=utf8");
jdbc_nba_data.setMaxPoolSize(30);
jdbc_nba_data.setMinPoolSize(10);
jdbc_nba_data.setCheckoutTimeout(1000*5);
jdbc_nba_data.setIdleConnectionTestPeriod(60*10);
jdbc_nba_data.setMaxIdleTime(60*3);
jdbc_nba_data.setMaxStatements(100);

$.addResource(ResourceType.DB_nba_data,new Database(jdbc_nba_data));


