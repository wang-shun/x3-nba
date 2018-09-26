package com.ftkj.manager.prop.bean;

import com.ftkj.db.domain.bean.PropBeanVO;
import com.ftkj.enums.EPlayerGrade;

/**
 * @author tim.huang
 * 2017年3月22日
 */
public class PropPlayerGradeBean extends PropBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//大于等于最小级别
	private EPlayerGrade minGrade;
	//小于等于最大级别
	private EPlayerGrade maxGrade;
	// 新秀年份_轮数_顺位
	private String draft;
	
    public PropPlayerGradeBean(PropBeanVO prop) {
        super(prop);
    }

    public EPlayerGrade getMinGrade() {
        return minGrade;
    }

    public EPlayerGrade getMaxGrade() {
        return maxGrade;
    }

    @Override
    protected void setAttr(String nameStr, String valStr) {
        switch (nameStr) {
            case "minGrade": {
                this.minGrade = EPlayerGrade.convertByName(valStr);
                break;
            }
            case "maxGrade": {
                this.maxGrade = EPlayerGrade.convertByName(valStr);
                break;
            }
            case "draft":{
				this.draft =  valStr;
				break;
			}
            default:
                super.setAttr(nameStr, valStr);
        }
    }

	public String getDraft() {
		return draft;
	}

	@Override
	public String toString() {
		return "PropPlayerGradeBean [minGrade=" + minGrade + ", maxGrade=" + maxGrade + ", draft=" + draft + "]";
	}
	

}
