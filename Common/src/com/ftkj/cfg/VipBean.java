package com.ftkj.cfg;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.enums.EBuffType;
import com.ftkj.manager.prop.PropSimple;
import com.ftkj.util.excel.RowData;

import com.google.common.collect.Maps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VipBean extends ExcelBean {

    /**
     * 等级
     */
    private int level;
    /**
     * 累计充值金额
     */
    private int money;
    /**
     * buff. map[buffid, map[v|p, int]]
     */
    private Map<Integer, Map<String, Integer>> buffMap = Maps.newHashMap();
    // 礼包原价
    private int originalPrice;
    // 现价
    private int salePrice;
    private List<PropSimple> giftList;
    private List<PropSimple> freeGiftList;

    @Override
    public void initExec(RowData row) {
        // 礼包
        String moneyCfg = row.get("giftMoney");
        String[] priceStr = (moneyCfg == null ? "" : moneyCfg).split(":");
        this.originalPrice = Integer.valueOf(priceStr[0]);
        this.salePrice = Integer.valueOf(priceStr[1]);
        this.giftList = PropSimple.getPropBeanByStringNotConfig(row.get("giftBag"));
        this.freeGiftList = PropSimple.getPropBeanByStringNotConfig(row.get("freeGift"));
        // buff配置
        for (EBuffType buf : Arrays.stream(EBuffType.values()).filter(b -> b.isVip()).collect(Collectors.toList())) {
            String colName = "b" + buf.getId();
            Map<String, Integer> paramMap = Maps.newHashMap();
            if (!row.checkName(colName)) {
                continue;
            }
            String[] paArr = row.get(colName).toString().split(";");
            paramMap.put("v", Integer.valueOf(paArr[0]));
            if (paArr.length > 1) {
                String[] paArr2 = paArr[1].split(",");
                for (int i = 0; i < paArr2.length; i++) {
                    paramMap.put("p" + (i + 1), Integer.valueOf(paArr2[i]));
                }
            }
            buffMap.put(buf.getId(), paramMap);
        }
    }

    

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public Map<Integer, Map<String, Integer>> getBuffMap() {
        return buffMap;
    }

    public int getBuffValue(EBuffType buff) {
        Map<String, Integer> attrs = buffMap.get(buff.getId());
        return attrs != null ? attrs.getOrDefault("v", 0) : 0;
    }

    public Map<String, Integer> getBuffType(EBuffType buf) {
        return buffMap.get(buf.getId());
    }

    public int getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }

    public int getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(int salePrice) {
        this.salePrice = salePrice;
    }

    public List<PropSimple> getGiftList() {
        return giftList;
    }

    public void setGiftList(List<PropSimple> giftList) {
        this.giftList = giftList;
    }

    public List<PropSimple> getFreeGift() {
        return freeGiftList;
    }

    public void setFreeGift(List<PropSimple> freeGift) {
        this.freeGiftList = freeGift;
    }

}
