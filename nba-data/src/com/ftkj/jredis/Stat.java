package com.ftkj.jredis;

import java.util.concurrent.atomic.AtomicLong;

public class Stat {
	int service;
	int s5;
	int s10;
	int s20;
	int s30;
	int s40;
	int s50;
	int s60;
	int s70;
	int s80;
	int s90;
	int s100;
	int s100x;	
	public int size=0;
	AtomicLong mo  = new AtomicLong(0);//请求总数
	AtomicLong mt = new AtomicLong(0);//响应总数
	
	public Stat(int service){
		this.service = service;
	}
	
	public void mo(){
		mo.incrementAndGet();
	}

	public void setTime(long time){
		//size++;
		mt.incrementAndGet();
		time = System.currentTimeMillis() - time;
		if(time<=5)s5++;
		else if(time>5  && time<=10)s10++;
		else if(time>10 && time<=20)s20++;
		else if(time>20 && time<=30)s30++;
		else if(time>30 && time<=40)s40++;
		else if(time>40 && time<=50)s50++;
		else if(time>50 && time<=60)s60++;
		else if(time>60 && time<=70)s70++;
		else if(time>70 && time<=80)s80++;
		else if(time>80 && time<=90)s90++;
		else if(time>90 && time<=100)s100++;
		else s100x++;	
	}

	@Override
	public String toString() {
		return "RedStat[mo="+mo.get()+",mt="+mt.get()+",s5="+s5+",s10="+s10+",s20="+s20+",s30="+s30+",s40="+s40+",s50="+s50+",s60="+s60+",s70="+s70+",s80="+s80+",s90="+s90+",s100="+s100+",s100x="+s100x+"]";
	}
	
	
	



}
