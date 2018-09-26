package com.ftkj.conn;

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

public class JDBCUtil {

    private static final Logger logger = LoggerFactory.getLogger(JDBCUtil.class);

	protected static void close(ResultSet rs,Statement stmt,Connection con) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException ex) {
				logger.error("Could not close JDBC ResultSet", ex);
			} catch (Throwable ex) {
				logger.error("Unexpected exception on closing JDBC ResultSet", ex);
			}
		}
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
				logger.error("Could not close JDBC Statement", ex);
			} catch (Throwable ex) {
				logger.error("Unexpected exception on closing JDBC Statement", ex);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
				logger.error("Could not close JDBC Connection", ex);
			} catch (Throwable ex) {
				logger.error("Unexpected exception on closing JDBC Connection", ex);
			}
		}
	}
	
	/**
	 * Method set.
	 * @param conn Connection
	 * @param ps PreparedStatement
	 * @param objs Object[]
	 * @throws SQLException
	 */
	public static  void set(Connection conn, PreparedStatement ps, Object... objs)throws SQLException {
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
			throw new IllegalArgumentException("unsupport type:" + o.getClass());
		}
	}
	
}
