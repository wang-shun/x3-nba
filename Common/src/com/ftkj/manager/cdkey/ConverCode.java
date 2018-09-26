package com.ftkj.manager.cdkey;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.console.CDKeyConsole;
import com.ftkj.enums.ErrorCode;
import com.ftkj.util.cdkey.ConverCodeUtil;


/**
 * @author tim.huang
 * 2016年12月20日
 * 3+2+16+3
 */
public class ConverCode {
	private ConverCodePO po;
	private String code;
	private String key;
	private List<ConverCodePO> codes;
	private ConverCodeBean bean;
	
	public static String Error = "error";
	
	public ConverCode(long teamId,String source){
		if(source.length()<15) {
			po = new ConverCodePO();
			po.setId(Error);
			return;
		}
		po = new ConverCodePO();
		po.setId(source.substring(0,3));
		po.setPlat(source.substring(3,5));
		this.key = source.substring(5,12);
		this.code = source.substring(12,15);
		po.setCode(source);
		po.setTeamId(teamId);
		po.setCreateTime(new Date());
		this.bean = CDKeyConsole.getConverCodeBean(po.getId(), po.getPlat());
	}
	
	public int check(){
		if(!this.getPo().getCode().equals(ConverCodeUtil.instantCode(this.getPo().getId(), 
						this.getPo().getPlat(), this.code)))
		 {
			return ErrorCode.Ret_Success;//激活码无效
		}
		
		if(this.codes.size()>=this.bean.getMaxCount())
		 {
			return ErrorCode.Ret_Success;//是否已经超出该类型激活码的最高领取上限
		}
		if(!this.bean.isStart())
		 {
			return ErrorCode.Ret_Success;//还没到激活时间
		}
		if(checkDayCount())
		 {
			return ErrorCode.Ret_Success;//是否超过当天可领取上限
		}
		if(checkExist())
		 {
			return ErrorCode.Ret_Success;//判断是否已经使用过这个key
		}
		return 0;
	}
	
	private boolean checkExist(){
		return this.codes.stream().filter(co->co.getCode().equals(this.po.getCode()))
							.findFirst().isPresent();
	}
	
	private boolean checkDayCount(){
		DateTime now = DateTime.now();
		int year = now.getYear();
		int day = now.getDayOfYear();
		return this.bean.getDayCount()>0 &&
				(int)this.codes.stream().map(co->new DateTime(co.getCreateTime()))
			.filter(dt->dt.getDayOfYear()==day && dt.getYear()==year).count() >=this.bean.getDayCount();
	}
	
	public ConverCodeBean getBean() {
		return bean;
	}
	public void setCodes(List<ConverCodePO> codes) {
		this.codes = codes;
	}

	public List<ConverCodePO> getCodes() {
		return codes;
	}
	public String getCode() {
		return code;
	}
	public String getKey() {
		return key;
	}
	public ConverCodePO getPo() {
		return po;
	}
	
}
