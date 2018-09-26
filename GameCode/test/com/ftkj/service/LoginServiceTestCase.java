package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import org.junit.Test;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.PropsMt;
import com.ftkj.proto.mt.SystemActiveMt;
import com.ftkj.proto.mt.XTeamPlayer;

/**
 * @author Marc.Wang 2011-9-22 上午11:24:58
 * 功能：
 */
public class LoginServiceTestCase extends BaseServiceTestCase {
	//
	@Test
	public void testLogin() throws InterruptedException{
		sendData(ProtoType.checkTeamExists, 
				"accountId","102030405144","shardId","101","debug","0");
		Thread.sleep(3 * 1000);
		//
		//sendData(ProtoType.infoTeam);
		//Thread.sleep(3 * 1000);
		//
		sendData(ProtoType.getSystemActives);
		//Thread.sleep(3 * 1000);
		//sendData(ProtoType.chat_inform,"content","警方介绍大家看法","byTeamId","101102030401010");
		Thread.sleep(3 * 1000);
	}
	//
	@Override
	public Object transToObject(int type,InputStream is, boolean isZip) throws IOException {
		Object obj = "";
		if(type==ProtoType.infoTeam.getService()){
			obj = isZip?XTeamPlayer.TeamPlayerList.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						XTeamPlayer.TeamPlayerList.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.loadInfo.getService()){
			obj = isZip?PropsMt.LoadInfoList.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						PropsMt.LoadInfoList.parseFrom(new DataInputStream(is));
		}else if(type==ProtoType.getSystemActives.getService()){
			obj = isZip?SystemActiveMt.SysAtvListData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						SystemActiveMt.SysAtvListData.parseFrom(new DataInputStream(is));
		}
		return obj;
	}
}
