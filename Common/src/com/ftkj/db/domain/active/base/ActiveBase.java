package com.ftkj.db.domain.active.base;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * 活动数据基类
 * 实现类只需实现iData1~3和sData1~5的get和set方法即可，按需选择
 * 继承类如果是内部类，必须是静态内部
 *
 * @author Jay
 * @time:2017年9月6日 上午11:25:18
 */
public class ActiveBase implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(ActiveBase.class);
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ActiveBasePO po;
    //
    @ActiveDataField(fieldName = "sFinishStatus")
    private DBList finishStatus;
    @ActiveDataField(fieldName = "sAwardStatus")
    private DBList awardStatus;
    @ActiveDataField(fieldName = "sPropNum", size = 10)
    private DBList propNum;

    /**
     * 是否只保存在redis的数据
     */
    private boolean isRedisData;

    public ActiveBase(ActiveBasePO po) {
        super();
        this.po = po;
        initField();
    }

    /**
     * 根据注解初始化
     */
    @SuppressWarnings("rawtypes")
    private void initField() {
        List<Field> fieldList = Lists.newArrayList();
        Class tempClass = this.getClass();
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        for (Field field : fieldList) {
            ActiveDataField anno = field.getAnnotation(ActiveDataField.class);
            if (anno == null) {
                continue;
            }
            if (field.getType() == DBList.class) {
                Field targetField;
                try {
                    targetField = this.po.getClass().getDeclaredField(anno.fieldName());
                    field.setAccessible(true);
                    targetField.setAccessible(true);
                    Object object = targetField.get(this.po);
                    DBList dbList;
                    if (object != null && !object.equals("") && !object.toString().toLowerCase().equals("null")) {
                        dbList = new DBList(object.toString());
                    } else if (anno.size() == 0) {
                        dbList = new DBList("");
                    } else {
                        dbList = new DBList(anno.size());
                    }
                    // 改了长度后自动补0,还没测试
                    if (anno.size() > 0 && dbList.getSize() < anno.size()) {
                        for (int i = dbList.getSize(); i < anno.size(); i++) {
                            dbList.addValue(0);
                        }
                    }
                    field.set(this, dbList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    public void saveBefore() {
        List<Field> fieldList = Lists.newArrayList();
        Class tempClass = this.getClass();
        while (tempClass != null) {//当父类为null的时候说明到达了最上层的父类(Object类).
            fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
            tempClass = tempClass.getSuperclass(); //得到父类,然后赋给自己
        }
        for (Field field : fieldList) {
            ActiveDataField anno = field.getAnnotation(ActiveDataField.class);
            if (anno == null) {
                continue;
            }
            if (field.getType() == DBList.class) {
                Field targetField;
                try {
                    targetField = this.po.getClass().getDeclaredField(anno.fieldName());
                    field.setAccessible(true);
                    targetField.setAccessible(true);
                    Object object = field.get(this);
                    targetField.set(this.po, object == null ? "" : ((DBList) object).getValueStr());
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 保存
     */
    public void save() {
        this.po.setUpdateTime(DateTime.now());
        saveBefore();
        this.po.save();
    }

    /**
     * 删除
     */
    public void del() {
        this.po.setType(999);
        this.po.del();
        this.save();
    }

    public int getAtvId() {
        return this.po.getAtvId();
    }

    public int getShardId() {
        return this.po.getShardId();
    }

    public long getTeamId() {
        return this.po.getTeamId();
    }

    public String getTeamName() {
        return this.po.getTeamName();
    }

    public int getType() {
        return this.po.getType();
    }

    public void setType(int type) {
        this.po.setType(type);
    }

    public DateTime getLastTime() {
        return this.po.getLastTime();
    }

    public void setLastTime(DateTime lastTime) {
        this.po.setLastTime(lastTime);
    }

    public DateTime getCreateTime() {
        return this.po.getCreateTime();
    }

    public void setCreateTime(DateTime time) {
        this.po.setCreateTime(time);
    }

    public DateTime getUpdateTime() {
        return this.po.getUpdateTime();
    }

    public int getiData1() {
        return this.po.getiData1();
    }

    public void setiData1(int iData1) {
        this.po.setiData1(iData1);
    }

    public int getiData2() {
        return this.po.getiData2();
    }

    public void setiData2(int iData2) {
        this.po.setiData2(iData2);
    }

    public int getiData3() {
        return this.po.getiData3();
    }

    public void setiData3(int iData3) {
        this.po.setiData3(iData3);
    }

    public String getsData1() {
        return this.po.getsData1();
    }

    public void setsData1(String sData1) {
        this.po.setsData1(sData1);
    }

    public String getsData2() {
        return this.po.getsData2();
    }

    public void setsData2(String sData2) {
        this.po.setsData2(sData2);
    }

    public String getsData3() {
        return this.po.getsData3();
    }

    public void setsData3(String sData3) {
        this.po.setsData3(sData3);
    }

    public String getsData4() {
        return this.po.getsData4();
    }

    public void setsData4(String sData4) {
        this.po.setsData4(sData4);
    }

    public String getsData5() {
        return this.po.getsData5();
    }

    public void setsData5(String sData5) {
        this.po.setsData5(sData5);
    }

    public DBList getFinishStatus() {
        return finishStatus;
    }

    public DBList getAwardStatus() {
        return awardStatus;
    }

    public ActiveBasePO getPo() {
        return po;
    }

    public boolean isRedisData() {
        return isRedisData;
    }

    public void setRedisData(boolean isRedisData) {
        this.isRedisData = isRedisData;
    }

    public DBList getPropNum() {
        return propNum;
    }

    public void setPropNum(DBList propNum) {
        this.propNum = propNum;
    }

}
