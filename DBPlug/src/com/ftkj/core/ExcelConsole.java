package com.ftkj.core;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ftkj.core.db.DBConsole;
import com.ftkj.core.project.BaseExcel;
import com.ftkj.core.util.StringUtil;
import com.mysql.jdbc.Statement;

/**
 * @author tim.huang 2016年1月8日 Excel控制
 */
public class ExcelConsole {
	
	private DBConsole dbConsole;
	private IExcel excel;
	public ExcelConsole(BaseExcel excel){
		try {
			dbConsole = new DBConsole(FileConsole.getDBSource());
			this.excel = excel;
		} catch (Exception e) {
			System.exit(0);
		}
	}
	
	public int run(File file) throws Exception{
		PlugResult result = excel.ExcelToDB(file);
		Connection conn = null;
		com.mysql.jdbc.PreparedStatement mysqlStatement = null;
		int num = 0; 
		try {
			conn = dbConsole.getConnection();
			conn.setAutoCommit(false);
			delTable(result.getName(),conn);
			PreparedStatement statement = conn.prepareStatement(result.getSql());
			if (statement.isWrapperFor(com.mysql.jdbc.Statement.class)) {
				mysqlStatement = statement.unwrap(com.mysql.jdbc.PreparedStatement.class); 
				mysqlStatement.setLocalInfileInputStream(result.getIs());
				num = mysqlStatement.executeUpdate(); 
			}
			conn.commit();
		} catch (Exception e) {
			System.err.println(e);
			num = 0;
			conn.rollback();
		}finally{
			close(mysqlStatement, conn);
		}
		return num;
	}
	
	private void delTable(String tableName,Connection conn)throws Exception{
		PreparedStatement statement = null;
		String sql = StringUtil.formatString("delete from {}", tableName);
//		String sql = StringUtil.formatString("drop table if exists {}", tableName);
		statement = conn.prepareStatement(sql);
		statement.execute();
	}
	
	protected static void close(Statement stmt,Connection con) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
			} catch (Throwable ex) {
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
			} catch (Throwable ex) {
			}
		}
	}
	
	protected static void closeDel(PreparedStatement stmt,Connection con) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException ex) {
			} catch (Throwable ex) {
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException ex) {
			} catch (Throwable ex) {
			}
		}
	}

	

}
