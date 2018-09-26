package com.ftkj.conn;

import com.ftkj.invoker.Jdbc;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
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
            config.addDataSourceProperty("prepStmtCacheSize", Math.min(Math.max(j.getMaxStatements(), 250), 500));
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


}
