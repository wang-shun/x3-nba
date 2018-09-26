package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.ChristmasMt;
import com.ftkj.proto.mt.SystemActiveMt;
import com.ftkj.proto.mt.TeamPlotMt;
/**
 * @author Marc.Wang 2011-12-15 上午11:03:58
 * 功能：
 */
public class ChristmasServiceTestCase extends BaseServiceTestCase{
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		//sendData(ProtoType.christmasView);
		//
//		sendData(ProtoType.dragonShowView);
		Thread.sleep(3 * 1000);
	}
	//
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
//		Object obj = "";
//		if(type==ProtoType.christmasView.getService()){
//			obj = isZip?ChristmasMt.ChristmasData.parseFrom(new 
//					InflaterInputStream(new DataInputStream(is))):
//						ChristmasMt.ChristmasData.parseFrom(new DataInputStream(is));
//		}else if(type==ProtoType.getSystemActives.getService()){
//			obj = isZip?SystemActiveMt.SysAtvListData.parseFrom(new 
//					InflaterInputStream(new DataInputStream(is))):
//						SystemActiveMt.SysAtvListData.parseFrom(new DataInputStream(is));
//		}else if(type==ProtoType.dragonShowView.getService()){
//			obj = isZip?TeamPlotMt.TeamDragonData.parseFrom(new 
//					InflaterInputStream(new DataInputStream(is))):
//						TeamPlotMt.TeamDragonData.parseFrom(new DataInputStream(is));
//		}
//		return obj;
		return null;
	}

}
