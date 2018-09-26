package com.ftkj.manager.prop;

import java.io.Serializable;

/**
 * 道具配置.
 */
public class PropSimpleBean extends PropSimple implements Serializable {
    private static final long serialVersionUID = 5076904188961266963L;
    //不要去掉 final, 修改时创建新的 PropSimple
    private final int propId;
    private final int num;
    private final int hour;
    private final String config;
    //不要去掉 final, 修改时创建新的 PropSimple

    public PropSimpleBean(int propId, int num, int hour, String config) {
        this.propId = propId;
        this.num = num;
        this.hour = hour;
        if ("".equals(config)) {
            config = "0";
        }
        this.config = config;
    }

    @Override
	public void setConfig(String newconfig) {

        throw new UnsupportedOperationException(
                String.format("你的代码可能正在修改策划配置的道具. propId %s cfg %s newcfg %s", propId, config, newconfig));
    }

    @Override
	public String getConfig() {
        return config;
    }

    @Override
	public int getHour() {
        return hour;
    }

    @Override
	public int getPropId() {
        return propId;
    }

    @Override
	public int getNum() {
        return num;
    }

    @Override
    public void addNum(int addNum) {
        throw new UnsupportedOperationException(
                String.format("你的代码可能正在修改策划配置的道具数量. propId %s num %s addNum %s", propId, num, addNum));
    }

    @Override
    public String toString() {
        return "{" +
                "\"propid\":" + propId +
                ", \"num\":" + num +
                '}';
    }
}
