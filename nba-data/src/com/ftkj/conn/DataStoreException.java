package com.ftkj.conn;

public class DataStoreException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8459701673673361296L;
	/**
	 * Constructor for DataStoreException.
	 * @param msg String
	 */
	public DataStoreException(String msg) {
		super(msg);
	}
	/**
	 * Constructor for DataStoreException.
	 * @param e Throwable
	 */
	public DataStoreException(Throwable e) {
		super(e);
	}
	/**
	 * Constructor for DataStoreException.
	 * @param msg String
	 * @param e Throwable
	 */
	public DataStoreException(String msg,Throwable e) {
		super(msg, e);
	}
}