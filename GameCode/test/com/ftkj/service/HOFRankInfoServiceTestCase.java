package com.ftkj.service;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

import org.junit.Test;

import com.ftkj.proto.ProtoType;
import com.ftkj.proto.mt.AnswerMt;
/**
 * @author Marc.Wang 2011-6-1 上午11:43:44
 * 功能：名人堂ServiceTestCase
 */
public class HOFRankInfoServiceTestCase extends BaseServiceTestCase {
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
		//sendData(ProtoType.getSystemActives);
		//Thread.sleep(3 * 1000);
		//sendData(ProtoType.chat_inform,"content","警方介绍大家看法","byTeamId","101102030401010");
		sendData(ProtoType.hofRankTmInfo,"id","3");
		Thread.sleep(3 * 1000);
	}
	
	@Override
	public Object transToObject(int type, InputStream is, boolean isZip)
			throws IOException {
		Object obj = "";
		if(type==ProtoType.hofRankTmInfo.getService()){
			obj = isZip?AnswerMt.HOFRankData.parseFrom(new 
					InflaterInputStream(new DataInputStream(is))):
						AnswerMt.HOFRankData.parseFrom(new DataInputStream(is));
		}
//		if(type==ProtoType.hofRankLgInfo.getService()){
//			obj = isZip?AnswerMt.HOFRankLeagueData.parseFrom(new 
//					InflaterInputStream(new DataInputStream(is))):
//						AnswerMt.HOFRankLeagueData.parseFrom(new DataInputStream(is));
//		}
		return obj;
	}

}
