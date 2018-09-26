package com.ftkj.db.conn.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    //	DataSource db;
    HikariDataSource ds;

    public Database(Jdbc j) {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(j.getUrl());
            config.setUsername(j.getUsername());
            config.setPassword(j.getPassword());

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", Math.min(Math.max(j.getStatementsCacheSize(), 250), 500));
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            //useServerPrepStmts:
            // Newer versions of MySQL support server-side prepared statements, this can provide a substantial performance boost. Set this property to true.
            config.addDataSourceProperty("useServerPrepStmts", "true");

            ds = new HikariDataSource(config);
            //            jdbcUrl=jdbc:mysql://localhost:3306/simpsons
            //            user=test
            //            password=test
            //            dataSource.cachePrepStmts=true
            //            dataSource.prepStmtCacheSize=250
            //            dataSource.prepStmtCacheSqlLimit=2048
            //            dataSource.useServerPrepStmts=true
            //            dataSource.useLocalSessionState=true
            //            dataSource.rewriteBatchedStatements=true
            //            dataSource.cacheResultSetMetadata=true
            //            dataSource.cacheServerConfiguration=true
            //            dataSource.elideSetAutoCommits=true
            //            dataSource.maintainTimeStats=false
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Connection getConnection() throws ConnectionException {
        try {
            Connection connection = ds.getConnection();
            return connection;
        } catch (SQLException e) {
            throw new ConnectionException(e.toString());
        }
    }

    public static void main(String[] args) {
        //sql 注入测试
        Jdbc cfg = new Jdbc();
        cfg.setUsername("root");
        cfg.setPassword("zgame2017");
        cfg.setDriver("com.mysql.jdbc.Driver");
        cfg.setUrl("jdbc:mysql://192.168.10.181:3306/nba_101?useUnicode=true&characterEncoding=utf8&sql_mode=NO_BACKSLASH_ESCAPES");
        //        cfg.setMinConnectionsPerPartition(10);
        //        cfg.setMaxConnectionsPerPartition(30);
        //        cfg.setAcquireIncrement(10);
        //        cfg.setPartitionCount(3);
        //        cfg.setIdleConnectionTestPeriodInSeconds(60 * 5);
        //        cfg.setStatementsCacheSize(400);
        try {
            Class.forName(cfg.getDriver()).newInstance();
            Connection conn = DriverManager.getConnection(cfg.getUrl(), cfg.getUsername(), cfg.getPassword());
            String sql = "select * from t_u_team where name like ? limit ?, ?";  // 含有参数
            PreparedStatement st = conn.prepareStatement(sql);
            st.setEscapeProcessing(true);
            //            st.setString(1, "儿童"); // 参数赋值
            //            st.setString(1, "儿童%"); // 参数赋值
            st.setString(1, "'\b'%儿童%;--" + "%"); // 参数赋值
            //                        st.setString(1, "张三' or '1=1"); // 参数赋值
            //            st.setString(2, "张三1' or '1=1"); // 参数赋值
            //            st.setString(3, "\'张三2' or '1=1"); // 参数赋值
            st.setInt(2, 1); // 参数赋值
            st.setInt(3, 2); // 参数赋值
            System.out.println(st.getClass());
            System.out.println(st.toString()); //com.mysql.jdbc.JDBC4PreparedStatement@d704f0: select * from goods where min_name = '儿童'
            st.executeQuery();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
