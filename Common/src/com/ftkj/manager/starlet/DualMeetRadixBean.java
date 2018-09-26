package com.ftkj.manager.starlet;

import com.ftkj.cfg.base.ExcelBean;
import com.ftkj.util.excel.RowData;

/**
 * 新秀对抗赛基础数据
 * @author qin.jiang
 */
public class DualMeetRadixBean extends ExcelBean{
    
    /** 挑战次数*/
    private int num;   
    /** 得分*/
    private float pts;     
    /** 篮板*/
    private float reb;    
    /** 助攻*/
    private float ast;     
    /** 抢断*/
    private float stl;    
    /** 盖帽*/
    private float blk; 
    /** 失误*/
    private float to; 
    /** 犯规*/
    private float pf;   
    
    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }


    public float getPts() {
        return pts;
    }

    public void setPts(float pts) {
        this.pts = pts;
    }

    public float getReb() {
        return reb;
    }

    public void setReb(float reb) {
        this.reb = reb;
    }

    public float getAst() {
        return ast;
    }

    public void setAst(float ast) {
        this.ast = ast;
    }

    public float getStl() {
        return stl;
    }

    public void setStl(float stl) {
        this.stl = stl;
    }

    public float getBlk() {
        return blk;
    }

    public void setBlk(float blk) {
        this.blk = blk;
    }

    public float getTo() {
        return to;
    }

    public void setTo(float to) {
        this.to = to;
    }

    public float getPf() {
        return pf;
    }

    public void setPf(float pf) {
        this.pf = pf;
    }

    @Override
    public void initExec(RowData row) {
        // TODO Auto-generated method stub
        
    }
}
