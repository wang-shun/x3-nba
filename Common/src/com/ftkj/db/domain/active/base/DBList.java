package com.ftkj.db.domain.active.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动
 * 常用于多个奖励领取状态，多项数据要保存的情况
 * 数组类型的封装
 * 方便存储几天的数据类型，逗号字符串分割，想：1,1,1,1,1,1,1,1
 */
public class DBList implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(DBList.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private List<Integer> list;
    /**
     * 当前最大值下标，适用于不指定index加值的方式
     */
    @SuppressWarnings("unused")
    private int index = 0;

    /**
     * 不指定大小，不初始化0值，只能往后新增值
     */
    public DBList() {
        list = new ArrayList<Integer>();
    }

    public DBList(int size) {
        list = new ArrayList<Integer>();
        for (int i = 0; i < size; i++) {
            list.add(0);
        }
    }

    public DBList(String strSplit) {
        strSplit = strSplit == null || strSplit.toLowerCase().equals("null") ? "" : strSplit;
        converStr(strSplit.split(","));
    }

    public DBList(int[] values) {
        list = new ArrayList<Integer>();
        for (int i : values) {
            addValue(i);
        }
    }

    public DBList(String[] valSet) {
        converStr(valSet);
    }

    private void converStr(String[] split) {
        list = new ArrayList<>();
        for (String s : split) {
            if (!s.equals("")) {
                try {
                    list.add(Integer.parseInt(s));
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
                index++;
            }
        }
    }

    /**
     * 往list放入新值，长度+1
     * 往下标里放值，下标会往后加1
     *
     * @param value
     */
    public DBList addValue(int value) {
        this.list.add(value);
        this.index++;
        return this;
    }

    public void setValueAdd(int index, int value) {
        this.list.set(index, getValue(index) + value);
    }

    public void setValue(int index, int value) {
        this.list.set(index, value);
    }

    /**
     * 重置所有值为指定value
     * index也会充值
     *
     * @param value
     */
    public void setAllValue(int value) {
        this.index = 0;
        for (int i = 0; i < this.list.size(); i++) {
            this.list.set(i, value);
        }
    }

    public int getValue(int index) {
        return this.list.get(index);
    }

    public String getValueStr() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.list.size(); i++) {
            sb.append(this.getValue(i)).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 是否包含值
     *
     * @param value
     * @return
     */
    public boolean containsValue(int value) {
        return this.list.contains(value);
    }

    public List<Integer> getList() {
        return list;
    }

    public int getSize() {
        return this.list.size();
    }
	
    public int sum() {
    	return this.list.stream().mapToInt(i-> i).sum();
    }
    
    public int count() {
    	return (int) this.list.stream().count();
    }
    
    public int valueCount(int val) {
    	return (int) this.list.stream().filter(v-> v == val).count();
    }

	@Override
	public String toString() {
		return "DBList [list=" + list + ", index=" + index + "]";
	}

    public void setList(List<Integer> list) {
        this.list = list;
    }
}
