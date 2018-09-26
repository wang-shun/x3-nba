package com.ftkj.db.conn.dao;



public interface RowHandler<T> {
	/**
	 * Method handleRow.
	 * @param row ResultSetRow
	 * @return T
	 * @throws Exception
	 */
	T handleRow(ResultSetRow row)throws Exception;
}
