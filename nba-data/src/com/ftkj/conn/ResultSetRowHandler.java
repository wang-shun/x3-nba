package com.ftkj.conn;



public interface ResultSetRowHandler<T> {
	/**
	 * Method handleRow.
	 * @param row ResultSetRow
	 * @return T
	 * @throws Exception
	 */
	T handleRow(ResultSetRow row)throws Exception;
}
