package com.ftkj.action;

import java.util.Random;

import com.ftkj.conn.TranscationManager;
import com.ftkj.invoker.ResourceCache;

public class BaseAction {
	//
	protected TranscationManager transcationManager;
	
	public BaseAction(){
		transcationManager = new TranscationManager();
		ResourceCache.get().init(this);
	}
	
	//产生随机数
	static Random random = new Random() ;
	public int getRand(int max){		
		int rand = random.nextInt( max ) ;
		return rand;
	}
	
	public static void main(String[] args){
		BaseAction t = new BaseAction();
		for(int i=1;i<100;i++){
			System.out.print("\t"+t.getRand(8));
			if(i%10==0)
				System.out.println("");
		}
	}
	
}