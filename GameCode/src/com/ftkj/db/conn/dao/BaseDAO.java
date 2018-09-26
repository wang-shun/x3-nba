package com.ftkj.db.conn.dao;

import com.ftkj.db.conn.annotation.Resource;
import com.ftkj.server.instance.InstanceFactory;
import com.ftkj.util.StringUtil;

import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public abstract class BaseDAO extends ConnectionDAO {
    private static final Logger log = LoggerFactory.getLogger(BaseDAO.class);

    public BaseDAO() {
        initField();
    }

    private void initField() {
        //获取当前类的父类
        Class<?> cla = this.getClass().getSuperclass();
        //获取类声明的各种字段
        Field[] fields = cla.getDeclaredFields();
        Resource resource = null;
        for (Field field : fields) {
            // 获取注解
            resource = field.getAnnotation(Resource.class);
            if (resource == null) { continue; }
            //
            try {
                field.setAccessible(true);
                field.set(this, InstanceFactory.get().getDataBaseByKey(resource.value().getResName()));
            } catch (Exception e) {
                log.error("init DataBase error:" + e);
            }
        }
    }

    //
    protected static final RowHandler<Integer> INT_ROW_HANDLER = new RowHandler<Integer>() {
        @Override
        public Integer handleRow(ResultSetRow row) throws Exception {
            return row.getInt(1);
        }
    };
    protected static final RowHandler<Long> LONG_ROW_HANDLER = new RowHandler<Long>() {
        @Override
        public Long handleRow(ResultSetRow row) throws Exception {
            return row.getLong(1);
        }
    };
    //
    protected static final RowHandler<Double> DOUBLE_ROW_HANDLER = new RowHandler<Double>() {
        @Override
        public Double handleRow(ResultSetRow row) throws Exception {
            return row.getDouble(1);
        }
    };
    //
    protected static final RowHandler<Float> FLOAT_ROW_HANDLER = new RowHandler<Float>() {
        @Override
        public Float handleRow(ResultSetRow row) throws Exception {
            return row.getFloat(1);
        }
    };
    //
    protected static final RowHandler<BigDecimal> BIGDECIMAL_ROW_HANDLER = new RowHandler<BigDecimal>() {
        @Override
        public BigDecimal handleRow(ResultSetRow row) throws Exception {
            return row.getBigDecimal(1);
        }
    };
    //
    protected static final RowHandler<String> STRING_ROW_HANDLER = new RowHandler<String>() {
        @Override
        public String handleRow(ResultSetRow row) throws Exception {
            return row.getString(1);
        }
    };
    //
    protected static final RowHandler<Byte> BYTE_ROW_HANDLER = new RowHandler<Byte>() {
        @Override
        public Byte handleRow(ResultSetRow row) throws Exception {
            return row.getByte(1);
        }
    };
    //
    protected static final RowHandler<Short> SHORT_ROW_HANDLER = new RowHandler<Short>() {
        @Override
        public Short handleRow(ResultSetRow row) throws Exception {
            return row.getShort(1);
        }
    };
    //
    protected static final RowHandler<Boolean> BOOLEAN_ROW_HANDLER = new RowHandler<Boolean>() {
        @Override
        public Boolean handleRow(ResultSetRow row) throws Exception {
            return row.getBoolean(1);
        }
    };
    //
    protected static final RowHandler<Date> DATE_ROW_HANDLER = new RowHandler<Date>() {
        @Override
        public Date handleRow(ResultSetRow row) throws Exception {
            return row.getDate(1);
        }
    };
    protected static final RowHandler<Blob> BLOB_ROW_HANDLER = new RowHandler<Blob>() {
        @Override
        public Blob handleRow(ResultSetRow row) throws Exception {
            return row.getBlob(1);
        }
    };

    //

    /**
     * Method queryForBoolean.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Boolean
     */
    protected final Boolean queryForBoolean(String sql, Object... parameters) {
        return queryForObject(sql, BOOLEAN_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForString.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return String
     */
    protected final String queryForString(String sql, Object... parameters) {
        return queryForObject(sql, STRING_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForDouble.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Double
     */
    protected final Double queryForDouble(String sql, Object... parameters) {
        return queryForObject(sql, DOUBLE_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForFloat.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Float
     */
    protected final Float queryForFloat(String sql, Object... parameters) {
        return queryForObject(sql, FLOAT_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForInteger.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Integer
     */
    protected final Integer queryForInteger(String sql, Object... parameters) {
        return queryForObject(sql, INT_ROW_HANDLER, parameters);
    }

    protected final Long queryForLong(String sql, Object... parameters) {
        return queryForObject(sql, LONG_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForShort.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Short
     */
    protected final Short queryForShort(String sql, Object... parameters) {
        return queryForObject(sql, SHORT_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForBigDecimal.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return BigDecimal
     */
    protected final BigDecimal queryForBigDecimal(String sql, Object... parameters) {
        return queryForObject(sql, BIGDECIMAL_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForByte.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Byte
     */
    protected final Byte queryForByte(String sql, Object... parameters) {
        return queryForObject(sql, BYTE_ROW_HANDLER, parameters);
    }

    /**
     * Method queryForDate.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return Date
     */
    protected final Date queryForDate(String sql, Object... parameters) {
        return queryForObject(sql, DATE_ROW_HANDLER, parameters);
    }

    protected final Blob queryForBlob(String sql, Object... parameters) {
        return queryForObject(sql, BLOB_ROW_HANDLER, parameters);
    }

    // --------------------------------------------------------------------------

    /**
     * @param sql        String
     * @param rowHandler ResultSetRowHandler<T>
     * @param parameters Object[]
     * @return T
     */
    protected final <T> T queryForObject(String sql, RowHandler<T> rowHandler, Object... parameters) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            rs = ps.executeQuery();
            if (rs.next()) {
                ResultSetRow row = new ResultSetRow(rs);
                return rowHandler.handleRow(row);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(rs, ps, conn);
        }
    }

    @SuppressWarnings("unchecked")
    protected final <T> T queryForBlobObject(String sql, Object... parameters) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ObjectInputStream ois = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            rs = ps.executeQuery();
            T ses = null;
            if (rs.next()) {
                InputStream is = rs.getBlob(1).getBinaryStream();
                ois = new ObjectInputStream(is);
                Object x = ois.readObject();
                ses = (T) x;
                //				ses = (T)rs.getObject(1);
                return ses;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DataStoreException(e);
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                    ois = null;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            JDBCUtil.close(rs, ps, conn);
        }
    }

    //

    /**
     * Method queryForList.
     *
     * @param sql        String
     * @param rowHandler ResultSetRowHandler<T>
     * @param parameters Object[]
     * @return List<T>
     */
    protected final <T> List<T> queryForList(String sql, RowHandler<T> rowHandler, Object... parameters) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            rs = ps.executeQuery();
            List<T> result = new ArrayList<T>();
            while (rs.next()) {
                ResultSetRow row = new ResultSetRow(rs);
                result.add(rowHandler.handleRow(row));
            }
            return result;
        } catch (Exception e) {
            log.error("Error Sql : {}", sql);
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(rs, ps, conn);
        }
    }

    protected final Map<String, String> queryForMap(String sql, Object... parameters) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            rs = ps.executeQuery();
            Map<String, String> result = Maps.newHashMap();
            while (rs.next()) {
                result.put(rs.getString(1), rs.getString(2));
            }
            return result;
        } catch (Exception e) {
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(rs, ps, conn);
        }
    }
    //--------------------------------------------------------------------------

    /**
     * Method execute.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return boolean
     */
    protected final boolean execute(String sql, Object... parameters) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            return ps.execute();
        } catch (Exception e) {
            log.error("Error Sql : {}", sql);
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(null, ps, conn);
        }
    }
    //

    /**
     * Method executeUpdate.
     *
     * @param sql        String
     * @param parameters Object[]
     * @return int
     */
    protected final int executeUpdate(String sql, Object... parameters) {
        //log(sql+Arrays.toString(parameters));
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            return ps.executeUpdate();
        } catch (Exception e) {
            log.error("Error Sql : {}", sql);
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(null, ps, conn);
        }

    }

    protected final int executeKey(String sql, Object... parameters) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int autoIncKeyFromApi = -1;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            JDBCUtil.set(conn, ps, parameters);
            ps.execute();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1);
            }
            return autoIncKeyFromApi;
        } catch (Exception e) {
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(rs, ps, conn);
        }
    }
    //

    /**
     * Method executeBatch.
     *
     * @param sql           String
     * @param parameterList List<Object[]>
     * @return int[]
     */
    protected final int[] executeBatch(String sql, List<Object[]> parameterList) {
        //log(sql);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement(sql);
            for (Object[] parameters : parameterList) {
                JDBCUtil.set(conn, ps, parameters);
                ps.addBatch();
            }
            return ps.executeBatch();
        } catch (Exception e) {
            log.error("Error Sql : {}", sql);
            throw new DataStoreException(e);
        } finally {
            JDBCUtil.close(null, ps, conn);
        }
    }

    protected final void loadData(InputStream is, String tableName, String rowNames, boolean loggerDebugEnabled, boolean loggerInfoEnabled) {
        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement(StringUtil.formatString("LOAD DATA LOCAL INFILE '{}.csv' REPLACE INTO TABLE {} ({})", tableName, tableName, rowNames));
            if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
                com.mysql.jdbc.PreparedStatement mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class);
                mysqlStatement.setLocalInfileInputStream(is);
                int result = mysqlStatement.executeUpdate();
                if (loggerDebugEnabled) {
                    log.debug("批量更新数据表[{}]，更新数据行[{}].", tableName, result);
                }
                if (loggerInfoEnabled) {
                    log.debug("批量更新数据表[{}]，更新数据行[{}].", tableName, result);
                }
            }
            //			statement.execute();
        } catch (ConnectionException e) {
            log.error(e.getMessage(), e);
        } catch (SQLException e) {
            log.error("SQL异常 -> table={}, rows={}", tableName, rowNames);
            log.error(e.getMessage(), e);
        } finally {
            JDBCUtil.close(null, statement, conn);
        }
    }

    //    public int getTableIndex(long uid){
    //    	return (int)(uid%10);
    //    }

    //private void log(String sql){
    //	logger.error("=="+sql);
    //}

    private String getBatchSql(List<AsynchronousBatchDB> dbObjects) {
        AsynchronousBatchDB dataDB = dbObjects.get(0);

        String preSql = BatchDataHelper.findBatchDataPreSql(dataDB);
        String parameterSql = BatchDataHelper.findBatchDataParameterSql(dataDB);
        String afterSql = BatchDataHelper.findBatchDataAfterSql(dataDB);

        StringBuilder builder = new StringBuilder();
        builder.append(preSql);
        boolean flag = false;
        for (AsynchronousBatchDB dbObject : dbObjects) {
            if (flag) {
                builder.append(",");
            }
            builder.append(parameterSql);
            flag = true;
        }
        builder.append(afterSql);
        String sql = builder.toString();
        return sql;
    }

    public final void batchUpdate(List<AsynchronousBatchDB> dbObjects, String tableName, String rowNames,
                                  boolean loggerDebugEnabled, boolean loggerInfoEnabled) {
        if (dbObjects == null || dbObjects.isEmpty()) {
            return;
        }
        String sql = getBatchSql(dbObjects);

        PreparedStatement statement = null;
        Connection conn = null;
        try {
            conn = getConnection();
            statement = conn.prepareStatement(sql);
            if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
                com.mysql.jdbc.PreparedStatement mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class);

                int index = 1;
                for (AsynchronousBatchDB dbObject : dbObjects) {
                    List<Object> parameterList = dbObject.getRowParameterList();
                    if (parameterList == null || parameterList.isEmpty()) {
                        continue;
                    }
                    JDBCUtil.addParameter(index, statement, parameterList);
                    index += parameterList.size();
                }

                int result = mysqlStatement.executeUpdate();
                if (loggerDebugEnabled) {
                    log.debug("批量更新数据表[{}]，更新数据行[{}].", tableName, result);
                }
                if (loggerInfoEnabled) {
                    log.debug("批量更新数据表[{}]，更新数据行[{}].", tableName, result);
                }
            }
            //			statement.execute();
        } catch (ConnectionException e) {
            log.error(e.getMessage(), e);
        } catch (SQLException e) {
            log.error("SQL异常 -> table={}, rows={}", tableName, rowNames);
            log.error(e.getMessage(), e);
        } finally {
            JDBCUtil.close(null, statement, conn);
        }
    }

}

