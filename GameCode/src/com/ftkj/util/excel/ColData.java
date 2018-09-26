package com.ftkj.util.excel;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.ftkj.util.DateTimeUtil;

public class ColData implements Serializable {
    private static final long serialVersionUID = 1110319915246816198L;
    private String type;
    private String value;

    public ColData(String type, String value) {
        super();
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    @SuppressWarnings("unchecked")
    public <V> V getValue() {
        if (this.type.equals(int.class.getSimpleName())) {
            return (V) new Integer(Integer.parseInt(value));
        }
        if (this.type.equals(float.class.getSimpleName())) {
            return (V) Float.valueOf(value);
        }
        if (this.type.equals(DateTime.class.getSimpleName().toLowerCase())) {
        	//System.err.println(value);
        	return (V) DateTimeUtil.parseToLdtDateTime(value);
        }
        // 默认字符串
        return (V) value;
    }

    public String getValueStr() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                "\"type\":\"" + type + "\"" +
                ", \"value\":\"" + value + "\"" +
                '}';
    }

}
