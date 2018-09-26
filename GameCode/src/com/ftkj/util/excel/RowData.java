package com.ftkj.util.excel;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class RowData implements Serializable {
    private static final long serialVersionUID = -685468182144305807L;
    private LinkedHashMap<String, ColData> rows;

    public RowData() {
        rows = new LinkedHashMap<>();
    }

    public void addColData(String name, ColData colData) {
        rows.put(name, colData);
    }

    /**
     * 取某类读取的类型
     *
     * @param name
     * @return
     */
    public String getColSimpleType(String name) {
        return rows.get(name).getType();
    }

    public <V> V get(String name) {
        return rows.get(name).getValue();
    }

    /**
     * map类型专用，返回value列的值
     *
     * @return
     */
    public <V> V getValue() {
        return rows.get("value").getValue();
    }

    public boolean checkName(String name) {
        return rows.containsKey(name);
    }

    public ColData getColData(String name) {
        return rows.get(name);
    }

    @Override
    public String toString() {
        return String.valueOf(rows);
    }

    public LinkedHashMap<String, ColData> getRows() {
        return rows;
    }

    /**
     * 转换成map类型，每个值的类型重新取type
     *
     * @return
     */
    public RowData converDataToMap() {
        this.rows.get("value").setType(this.rows.get("type").getValueStr());
        return this;
    }
}
