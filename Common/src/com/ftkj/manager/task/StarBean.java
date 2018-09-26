package com.ftkj.manager.task;

/**
 * 星级系数基础表
 *
 * @author qin.jiang
 */
public class StarBean {
    //星数
    private int star;
    //等级系数(计算任务经验的系数)
    private int num;
    //星级系数
    private float ratio;

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public int getStar() {
        return star;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }
}
