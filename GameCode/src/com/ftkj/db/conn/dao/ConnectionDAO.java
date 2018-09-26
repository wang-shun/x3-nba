package com.ftkj.db.conn.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.ftkj.db.conn.ao.Transcation;

public abstract class ConnectionDAO{
	//
	private static ThreadLocal<Transcation> transactionHolder =
		new ThreadLocal<Transcation>();
	
	//
	protected abstract Connection getRealConnection() ;
	
	/**
	 * 得到线程对应的DB连接对象
	 * @return
	 * @throws DBException 
	 */
	public Connection getConnection() throws ConnectionException{
		Transcation t = transactionHolder.get();
		if(t==null){
			return getRealConnection();
		}
		ConnectionHolder holder = t.getConnection();
		if(holder==null){
			Connection connection = getRealConnection();
			try {
				connection.setAutoCommit(!t.isNeedTranscation());
			} catch (SQLException e) {
				throw new ConnectionException("set connection autoCommit error.",e);
			}
			ConnectionHolder ch = new ConnectionHolder(connection);
			t.setConnection(ch);
			return ch;
		}
		return holder;
	}
	/**
	 * 提交
	 * @throws DBException 
	 */
	public static void commit() throws ConnectionException {
		Transcation t = transactionHolder.get();
		if(t==null){
			throw new ConnectionException("not found transaction.");
		}
		if(t.getConnection()!=null){
			try {
				t.getConnection().getRealConnection().commit();
			} catch (SQLException e) {
				throw new ConnectionException("connection commit error.");
			}
		}
	}
	/**
	 * end current transaction
	 * @throws DBException 
	 */
	public static void endTransaction() throws ConnectionException {
		Transcation t=transactionHolder.get();
		transactionHolder.remove();
		if(t==null){
			throw new ConnectionException("not found transaction.");
		}
		try {
			if(t.getConnection()!=null){
				t.getConnection().getRealConnection().setAutoCommit(true);
				t.getConnection().getRealConnection().close();
			}
		} catch (SQLException e) {
			throw new ConnectionException("connection commit error.",e);
		}
	}
	/**
	 * 回滚事物
	 * @throws DBException 
	 */
	public static void rollback() throws ConnectionException {
		Transcation t=transactionHolder.get();
		if(t==null){
			throw new ConnectionException("not found transaction.");
		}
		try {
			if(t.getConnection()!=null){
				t.getConnection().getRealConnection().rollback();
			}
		} catch (SQLException e) {
			throw new ConnectionException("connection rollback error.",e);
		}
	}
	/**
	 * 开始事物
	 * @param needTranscation
	 */
	public static void startTransaction() {
		Transcation t = new Transcation();
		t.setNeedTranscation(true);
		transactionHolder.set(t);
	}
}
