package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.TacticsMt;
/**
 * @author Marc.Wang 2012-4-11 下午04:04:15
 * 功能：
 */
public class TeamServiceTestCase extends BaseServiceTestCase {
	//
	public void testShowView() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.myTeamCoachList);
		Thread.sleep(3 * 1000);
	}
	//
	@Override
	Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.myTeamCoachList.getService()){
			obj = isZip?TacticsMt.MyTeamCoachData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						TacticsMt.MyTeamCoachData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}

}
