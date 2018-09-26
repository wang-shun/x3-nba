package com.ftkj.db.conn.dao;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class JDBCUtil {
    private static final Logger log = LoggerFactory.getLogger(JDBCUtil.class);

    protected static void close(ResultSet rs, Statement stmt, Connection con) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ex) {
                log.error("Could not close JDBC ResultSet", ex);
            } catch (Throwable ex) {
                log.error("Unexpected exception on closing JDBC ResultSet", ex);
            }
        }
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException ex) {
                log.error("Could not close JDBC Statement", ex);
            } catch (Throwable ex) {
                log.error("Unexpected exception on closing JDBC Statement", ex);
            }
        }
        if (con != null) {
            try {
                con.close();
            } catch (SQLException ex) {
                log.error("Could not close JDBC Connection", ex);
            } catch (Throwable ex) {
                log.error("Unexpected exception on closing JDBC Connection", ex);
            }
        }
    }

    /**
     * Method set.
     *
     * @param conn Connection
     * @param ps   PreparedStatement
     * @param objs Object[]
     * @throws SQLException
     */
    public static void set(Connection conn, PreparedStatement ps, Object... objs) throws SQLException {
        if (objs == null || objs.length == 0) {
            return;
        }
        int i = 1;

        for (Object o : objs) {
            if (o == null) {
                ps.setString(i++, null);
                continue;
            }
            if (o instanceof String) {
                ps.setString(i++, ((String) o));
                continue;
            }
            if (o instanceof Date) {
                Date date = (Date) o;
                ps.setTimestamp(i++, new Timestamp(date.getTime()));
                continue;
            }
            if (o instanceof Integer) {
                ps.setInt(i++, ((Integer) o));
                continue;
            }
            if (o instanceof Double) {
                ps.setDouble(i++, ((Double) o));
                continue;
            }
            if (o instanceof Float) {
                ps.setFloat(i++, ((Float) o));
                continue;
            }
            if (o instanceof BigDecimal) {
                ps.setBigDecimal(i++, ((BigDecimal) o));
                continue;
            }
            if (o instanceof Long) {
                ps.setLong(i++, ((Long) o));
                continue;
            }
            if (o instanceof Byte) {
                ps.setByte(i++, ((Byte) o));
                continue;
            }
            if (o instanceof byte[]) {
                ps.setBytes(i++, ((byte[]) o));
                continue;
            }
            if (o instanceof Boolean) {
                ps.setBoolean(i++, ((Boolean) o));
                continue;
            }
            if (o instanceof Set) {
                ps.setObject(i++, o);
                continue;
            }
            throw new IllegalArgumentException("unsupport type:" + o.getClass());
        }
    }

    public static void setParameterType(int index, PreparedStatement ps, Object object) throws SQLException {
        if (object == null) {
            ps.setString(index, null);
            return;
        }
        if (object instanceof String) {
            ps.setString(index, ((String) object));
            return;
        }
        if (object instanceof Date) {
            Date date = (Date) object;
            ps.setTimestamp(index, new Timestamp(date.getTime()));
            return;
        }
        if (object instanceof DateTime) {
            DateTime date = (DateTime) object;
            ps.setTimestamp(index, new Timestamp(date.getMillis()));
            return;
        }
        if (object instanceof Integer) {
            ps.setInt(index, ((Integer) object));
            return;
        }
        if (object instanceof Double) {
            ps.setDouble(index, ((Double) object));
            return;
        }
        if (object instanceof Float) {
            ps.setFloat(index, ((Float) object));
            return;
        }
        if (object instanceof BigDecimal) {
            ps.setBigDecimal(index, ((BigDecimal) object));
            return;
        }
        if (object instanceof Long) {
            ps.setLong(index, ((Long) object));
            return;
        }
        if (object instanceof Byte) {
            ps.setByte(index, ((Byte) object));
            return;
        }
        if (object instanceof byte[]) {
            ps.setBytes(index, ((byte[]) object));
            return;
        }
        if (object instanceof Boolean) {
            ps.setBoolean(index, ((Boolean) object));
            return;
        }
        ps.setObject(index, object);
        //        throw new IllegalArgumentException("unsupport type:" + object.getClass());
    }

    public static void addParameter(int startIndex, PreparedStatement ps, List<Object> objects) throws SQLException {
        if (objects == null || objects.isEmpty()) {
            return;
        }
        int index = startIndex;
        for (Object object : objects) {
            setParameterType(index, ps, object);
            index++;
        }
    }

}
