package com.main.test;

import java.util.Random;

public class SimpleTest {

	public static void main(String[] args) {
//		SimpleTest t = new SimpleTest();
//		EventBusManager.register(EEventType.充值, t);
//		EventBusManager.post(EEventType.充值, 100);
		// 随机种子测试
		 Random random = new Random(1000);
		 for(int i=0; i<10; i++) {
			 System.err.println(random.nextInt(100));
		 }
	}
	
//	@Subscribe
//	public void haha(RechargeParam p) {
//		System.err.println(p.fk+"   " + p.type);
//	}
//	
//	@Subscribe
//	public void haha2(int p) {
//		System.err.println(p);
//	}
}
