package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.Default;
import com.ftkj.proto.mt.HOFMt;

public class HOFServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030484800","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.hofCreateRoom);
		Thread.sleep(3 * 1000);
		
		//sendData(ProtoType.hofIntoRoom,"roomId","1");
		//Thread.sleep(3 * 1000);
		//
		//sendData(ProtoType.hofKickUser,"kickTeamId","101102030405074");
		//Thread.sleep(3 * 1000);
		//
		//sendData(ProtoType.hofShowView);
		Thread.sleep(3000 * 1000);
	}
	//
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.hofShowView.getService()){
			obj = isZip?HOFMt.HOFData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						HOFMt.HOFData.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.hofCreateRoom.getService()){
			obj = isZip?Default.DefaultData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						Default.DefaultData.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.hofKickUser.getService()){
			obj = isZip?Default.DefaultData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						Default.DefaultData.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.topicHofRoom.getService()){
			obj = isZip?HOFMt.MyHOFRoomData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						HOFMt.MyHOFRoomData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}
