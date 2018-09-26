package com.ftkj.conn;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;


public class ResultSetRow {
	private ResultSet resultSet;
	/**
	 * Constructor for ResultSetRow.
	 * @param resultSet ResultSet
	 */
	protected ResultSetRow(ResultSet resultSet){
		this.resultSet=resultSet;
	}
	/**
	 * @param columnIndex
	 * @return Array
	 * @throws SQLException * @see java.sql.ResultSet#getArray(int) */
	public Array getArray(int columnIndex) throws SQLException {
		return resultSet.getArray(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Array
	 * @throws SQLException * @see java.sql.ResultSet#getArray(java.lang.String) */
	public Array getArray(String columnLabel) throws SQLException {
		return resultSet.getArray(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return InputStream
	 * @throws SQLException * @see java.sql.ResultSet#getAsciiStream(int) */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return resultSet.getAsciiStream(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return InputStream
	 * @throws SQLException * @see java.sql.ResultSet#getAsciiStream(java.lang.String) */
	public InputStream getAsciiStream(String columnLabel) throws SQLException {
		return resultSet.getAsciiStream(columnLabel);
	}	
	/**
	 * @param columnIndex
	 * @return BigDecimal
	 * @throws SQLException * @see java.sql.ResultSet#getBigDecimal(int) */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return resultSet.getBigDecimal(columnIndex);
	}	
	/**
	 * @param columnLabel
	 * @return BigDecimal
	 * @throws SQLException * @see java.sql.ResultSet#getBigDecimal(java.lang.String) */
	public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
		return resultSet.getBigDecimal(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return InputStream
	 * @throws SQLException * @see java.sql.ResultSet#getBinaryStream(int) */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return resultSet.getBinaryStream(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return InputStream
	 * @throws SQLException * @see java.sql.ResultSet#getBinaryStream(java.lang.String) */
	public InputStream getBinaryStream(String columnLabel) throws SQLException {
		return resultSet.getBinaryStream(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return Blob
	 * @throws SQLException * @see java.sql.ResultSet#getBlob(int) */
	public Blob getBlob(int columnIndex) throws SQLException {
		return resultSet.getBlob(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Blob
	 * @throws SQLException * @see java.sql.ResultSet#getBlob(java.lang.String) */
	public Blob getBlob(String columnLabel) throws SQLException {
		return resultSet.getBlob(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return boolean
	 * @throws SQLException * @see java.sql.ResultSet#getBoolean(int) */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return resultSet.getBoolean(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return boolean
	 * @throws SQLException * @see java.sql.ResultSet#getBoolean(java.lang.String) */
	public boolean getBoolean(String columnLabel) throws SQLException {
		return resultSet.getBoolean(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return byte
	 * @throws SQLException * @see java.sql.ResultSet#getByte(int) */
	public byte getByte(int columnIndex) throws SQLException {
		return resultSet.getByte(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return byte
	 * @throws SQLException * @see java.sql.ResultSet#getByte(java.lang.String) */
	public byte getByte(String columnLabel) throws SQLException {
		return resultSet.getByte(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return byte[]
	 * @throws SQLException * @see java.sql.ResultSet#getBytes(int) */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return resultSet.getBytes(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return byte[]
	 * @throws SQLException * @see java.sql.ResultSet#getBytes(java.lang.String) */
	public byte[] getBytes(String columnLabel) throws SQLException {
		return resultSet.getBytes(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return Reader
	 * @throws SQLException * @see java.sql.ResultSet#getCharacterStream(int) */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return resultSet.getCharacterStream(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Reader
	 * @throws SQLException * @see java.sql.ResultSet#getCharacterStream(java.lang.String) */
	public Reader getCharacterStream(String columnLabel) throws SQLException {
		return resultSet.getCharacterStream(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return Clob
	 * @throws SQLException * @see java.sql.ResultSet#getClob(int) */
	public Clob getClob(int columnIndex) throws SQLException {
		return resultSet.getClob(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Clob
	 * @throws SQLException * @see java.sql.ResultSet#getClob(java.lang.String) */
	public Clob getClob(String columnLabel) throws SQLException {
		return resultSet.getClob(columnLabel);
	}
	/**
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getConcurrency() */
	public int getConcurrency() throws SQLException {
		return resultSet.getConcurrency();
	}
	/**
	 * @return String
	 * @throws SQLException * @see java.sql.ResultSet#getCursorName() */
	public String getCursorName() throws SQLException {
		return resultSet.getCursorName();
	}
	/**
	 * @param columnIndex
	 * @param cal
	 * @return Date
	 * @throws SQLException * @see java.sql.ResultSet#getDate(int, java.util.Calendar) */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return resultSet.getDate(columnIndex, cal);
	}
	/**
	 * @param columnIndex
	 * @return Date
	 * @throws SQLException * @see java.sql.ResultSet#getDate(int) */
	public Date getDate(int columnIndex) throws SQLException {
		return resultSet.getDate(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @param cal
	 * @return Date
	 * @throws SQLException * @see java.sql.ResultSet#getDate(java.lang.String, java.util.Calendar) */
	public Date getDate(String columnLabel, Calendar cal) throws SQLException {
		return resultSet.getDate(columnLabel, cal);
	}
	/**
	 * @param columnLabel
	 * @return Date
	 * @throws SQLException * @see java.sql.ResultSet#getDate(java.lang.String) */
	public Date getDate(String columnLabel) throws SQLException {
		return resultSet.getDate(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return double
	 * @throws SQLException * @see java.sql.ResultSet#getDouble(int) */
	public double getDouble(int columnIndex) throws SQLException {
		return resultSet.getDouble(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return double
	 * @throws SQLException * @see java.sql.ResultSet#getDouble(java.lang.String) */
	public double getDouble(String columnLabel) throws SQLException {
		return resultSet.getDouble(columnLabel);
	}
	/**
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getFetchDirection() */
	public int getFetchDirection() throws SQLException {
		return resultSet.getFetchDirection();
	}
	/**
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getFetchSize() */
	public int getFetchSize() throws SQLException {
		return resultSet.getFetchSize();
	}
	/**
	 * @param columnIndex
	 * @return float
	 * @throws SQLException * @see java.sql.ResultSet#getFloat(int) */
	public float getFloat(int columnIndex) throws SQLException {
		return resultSet.getFloat(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return float
	 * @throws SQLException * @see java.sql.ResultSet#getFloat(java.lang.String) */
	public float getFloat(String columnLabel) throws SQLException {
		return resultSet.getFloat(columnLabel);
	}
	/**
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getHoldability() */
	public int getHoldability() throws SQLException {
		return resultSet.getHoldability();
	}
	/**
	 * @param columnIndex
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getInt(int) */
	public int getInt(int columnIndex) throws SQLException {
		return resultSet.getInt(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getInt(java.lang.String) */
	public int getInt(String columnLabel) throws SQLException {
		return resultSet.getInt(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return long
	 * @throws SQLException * @see java.sql.ResultSet#getLong(int) */
	public long getLong(int columnIndex) throws SQLException {
		return resultSet.getLong(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return long
	 * @throws SQLException * @see java.sql.ResultSet#getLong(java.lang.String) */
	public long getLong(String columnLabel) throws SQLException {
		return resultSet.getLong(columnLabel);
	}
	/**
	 * @return ResultSetMetaData
	 * @throws SQLException * @see java.sql.ResultSet#getMetaData() */
	public ResultSetMetaData getMetaData() throws SQLException {
		return resultSet.getMetaData();
	}
	/**
	 * @param columnIndex
	 * @return Reader
	 * @throws SQLException * @see java.sql.ResultSet#getNCharacterStream(int) */
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return resultSet.getNCharacterStream(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Reader
	 * @throws SQLException * @see java.sql.ResultSet#getNCharacterStream(java.lang.String) */
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return resultSet.getNCharacterStream(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return NClob
	 * @throws SQLException * @see java.sql.ResultSet#getNClob(int) */
	public NClob getNClob(int columnIndex) throws SQLException {
		return resultSet.getNClob(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return NClob
	 * @throws SQLException * @see java.sql.ResultSet#getNClob(java.lang.String) */
	public NClob getNClob(String columnLabel) throws SQLException {
		return resultSet.getNClob(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return String
	 * @throws SQLException * @see java.sql.ResultSet#getNString(int) */
	public String getNString(int columnIndex) throws SQLException {
		return resultSet.getNString(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return String
	 * @throws SQLException * @see java.sql.ResultSet#getNString(java.lang.String) */
	public String getNString(String columnLabel) throws SQLException {
		return resultSet.getNString(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return Object
	 * @throws SQLException * @see java.sql.ResultSet#getObject(int) */
	public Object getObject(int columnIndex) throws SQLException {
		return resultSet.getObject(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @param map
	 * @return Object
	 * @throws SQLException * @see java.sql.ResultSet#getObject(java.lang.String, java.util.Map) */
	public Object getObject(String columnLabel, Map<String, Class<?>> map)
	throws SQLException {
		return resultSet.getObject(columnLabel, map);
	}
	/**
	 * @param columnLabel
	 * @return Object
	 * @throws SQLException * @see java.sql.ResultSet#getObject(java.lang.String) */
	public Object getObject(String columnLabel) throws SQLException {
		return resultSet.getObject(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return Ref
	 * @throws SQLException * @see java.sql.ResultSet#getRef(int) */
	public Ref getRef(int columnIndex) throws SQLException {
		return resultSet.getRef(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return Ref
	 * @throws SQLException * @see java.sql.ResultSet#getRef(java.lang.String) */
	public Ref getRef(String columnLabel) throws SQLException {
		return resultSet.getRef(columnLabel);
	}
	/**
	 * @return int
	 * @throws SQLException * @see java.sql.ResultSet#getRow() */
	public int getRow() throws SQLException {
		return resultSet.getRow();
	}
	/**
	 * @param columnIndex
	 * @return RowId
	 * @throws SQLException * @see java.sql.ResultSet#getRowId(int) */
	public RowId getRowId(int columnIndex) throws SQLException {
		return resultSet.getRowId(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return RowId
	 * @throws SQLException * @see java.sql.ResultSet#getRowId(java.lang.String) */
	public RowId getRowId(String columnLabel) throws SQLException {
		return resultSet.getRowId(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return short
	 * @throws SQLException * @see java.sql.ResultSet#getShort(int) */
	public short getShort(int columnIndex) throws SQLException {
		return resultSet.getShort(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return short
	 * @throws SQLException * @see java.sql.ResultSet#getShort(java.lang.String) */
	public short getShort(String columnLabel) throws SQLException {
		return resultSet.getShort(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @return SQLXML
	 * @throws SQLException * @see java.sql.ResultSet#getSQLXML(int) */
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return resultSet.getSQLXML(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return SQLXML
	 * @throws SQLException * @see java.sql.ResultSet#getSQLXML(java.lang.String) */
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return resultSet.getSQLXML(columnLabel);
	}
	/**
	 * @return Statement
	 * @throws SQLException * @see java.sql.ResultSet#getStatement() */
	public Statement getStatement() throws SQLException {
		return resultSet.getStatement();
	}
	/**
	 * @param columnIndex
	 * @return String
	 * @throws SQLException * @see java.sql.ResultSet#getString(int) */
	public String getString(int columnIndex) throws SQLException {
		return resultSet.getString(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @return String
	 * @throws SQLException * @see java.sql.ResultSet#getString(java.lang.String) */
	public String getString(String columnLabel) throws SQLException {
		return resultSet.getString(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @param cal
	 * @return Time
	 * @throws SQLException * @see java.sql.ResultSet#getTime(int, java.util.Calendar) */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return resultSet.getTime(columnIndex, cal);
	}
	/**
	 * @param columnIndex
	 * @return Time
	 * @throws SQLException * @see java.sql.ResultSet#getTime(int) */
	public Time getTime(int columnIndex) throws SQLException {
		return resultSet.getTime(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @param cal
	 * @return Time
	 * @throws SQLException * @see java.sql.ResultSet#getTime(java.lang.String, java.util.Calendar) */
	public Time getTime(String columnLabel, Calendar cal) throws SQLException {
		return resultSet.getTime(columnLabel, cal);
	}
	/**
	 * @param columnLabel
	 * @return Time
	 * @throws SQLException * @see java.sql.ResultSet#getTime(java.lang.String) */
	public Time getTime(String columnLabel) throws SQLException {
		return resultSet.getTime(columnLabel);
	}
	/**
	 * @param columnIndex
	 * @param cal
	 * @return Timestamp
	 * @throws SQLException * @see java.sql.ResultSet#getTimestamp(int, java.util.Calendar) */
	public Timestamp getTimestamp(int columnIndex, Calendar cal)
	throws SQLException {
		return resultSet.getTimestamp(columnIndex, cal);
	}
	/**
	 * @param columnIndex
	 * @return Timestamp
	 * @throws SQLException * @see java.sql.ResultSet#getTimestamp(int) */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return resultSet.getTimestamp(columnIndex);
	}
	/**
	 * @param columnLabel
	 * @param cal
	 * @return Timestamp
	 * @throws SQLException * @see java.sql.ResultSet#getTimestamp(java.lang.String, java.util.Calendar) */
	public Timestamp getTimestamp(String columnLabel, Calendar cal)
	throws SQLException {
		return resultSet.getTimestamp(columnLabel, cal);
	}
	/**
	 * @param columnLabel
	 * @return Timestamp
	 * @throws SQLException * @see java.sql.ResultSet#getTimestamp(java.lang.String) */
	public Timestamp getTimestamp(String columnLabel) throws SQLException {
		return resultSet.getTimestamp(columnLabel);
	}	

}

