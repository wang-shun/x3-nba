package com.ftkj.core;

import java.io.InputStream;

public class PlugResult {
	private String sql;
	private String name;
	private InputStream is;
	
	public PlugResult(String sql, String name, InputStream is) {
		super();
		this.sql = sql;
		this.name = name;
		this.is = is;
	}
	public InputStream getIs() {
		return is;
	}
	public void setIs(InputStream is) {
		this.is = is;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
