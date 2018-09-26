package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;

/**
 * @author tim.huang
 * 2017年10月26日
 * 教练道具
 */
public class PropCoachBean extends PropBean {
    private static final long serialVersionUID = 1L;

    private int coachId;

    public PropCoachBean(PropBeanVO prop) {
        super(prop);
    }

    public int getCoachId() {
        return coachId;
    }

    public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "coach": {
                this.setCoachId(Integer.parseInt(valStr));
                break;
            }
            default:
                super.setAttr(nameStr, valStr);
        }
    }

}
