package com.ftkj.manager.skill;

import com.google.common.primitives.Ints;

public class SkillTree{
	private int step;
	
	private int s1;
	private int s2;
	private int s3;
	private int s4;
	
	public SkillTree(int step, int s1, int s2, int s3, int s4) {
		super();
		this.step = step;
		this.s1 = s1;
		this.s2 = s2;
		this.s3 = s3;
		this.s4 = s4;
	}
	
	public String getSql(){
		return this.s1+","+this.s2+","+this.s3+","+this.s4;
	}
	
	public int getMaxSkillLevel(){
		return Ints.max(s1,s2,s3,s4);
	}
	
	public int superSkillPower(){
		int max = Ints.max(s1,s2,s3,s4);
		if(step == 4){
			switch(max){
				case 1:{
					return 12000;
				}
				case 2:{
					return 14000;
				}
				case 3:{
					return 16000;
				}
				case 4:{
					return 18000;
				}
				case 5:{
					return 20000;
				}
				default:{
					return 10000;
				}
			}
		}else if(step == 6){
			switch(max){
				case 1:{
					return 22000;
				}
				case 2:{
					return 24000;
				}
				case 3:{
					return 26000;
				}
				case 4:{
					return 28000;
				}
				case 5:{
					return 30000;
				}
				default:{
					return 10000;
				}
			}
		}
		return 10000;
	}

	public int getSkillLevel(int index){
		switch(index){
			case 0:{
				return s1;
			}
			case 1:{
				return s2;
			}
			case 2:{
				return s3;
			}
			case 3:{
				return s4;
			}
		}	
		return 0;
	}
	
	public int getAllLevel(){
		return s1+s2+s3+s4;
	}
	
	public void updateLevel(int index,int level){
		switch(index){
			case 0:{
				s1=level;
				return;
			}
			case 1:{
				s2=level;
				return;
			}
			case 2:{
				s3=level;
				return;
			}
			case 3:{
				s4=level;
				return;
			}
		}	
	}
	
	public int getStep() {
		return step;
	}
	public int getS1() {
		return s1;
	}
	public int getS2() {
		return s2;
	}
	public int getS3() {
		return s3;
	}
	public int getS4() {
		return s4;
	}
	
}
