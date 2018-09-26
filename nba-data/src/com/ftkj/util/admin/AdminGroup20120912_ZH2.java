package com.ftkj.util.admin;

import com.ftkj.util.MD5Util;
import org.apache.mina.core.session.IoSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminGroup20120912_ZH2 {

	public Map<String,String> map = new HashMap<String,String>();

	public static void main(String[] args){
		AdminGroup20120912_ZH2 a = new AdminGroup20120912_ZH2() ;
		a.server();
		
		//a.send("231", "<gm/>servicecode=8110;key=pk_group_clear;pkId=124");

		//a.addMoneyFK(112102030712025L, 50000,"qq pay");//傲世丶恩佐
		//a.send("112", "<gm/>servicecode=8001;teamId=112102030712025;propIds=124:500,354:20,203:1000,207:1000,348:100,323:10,261:2000,291:2000,420:100,430:100,441:1000,442:1000,443:1000,444:1000,445:1000,446:1000,447:1000,448:1000,449:1000,20:20000,310:20000,311:1000;info=系统奖励"); //建议奖励

		
		//a.send("178","<gm/>servicecode=8110;key=gm_reload_props_player_card;teamId=178102039155950");

		//update game_group_buy_team set timex='2016-10-25',buy_num=500 where team_id = 102102030710723 and timex = '2016-10-25';
		//insert into game_group_buy_team(team_id,good_id,timex,shard_id,buy_num,prop_price,create_time)select team_id,good_id,'2016-12-15',shard_id,buy_num,prop_price,'2016-12-15 01:00:00' from game_group_buy_team where team_id = 102102030710723 and timex='2016-12-09';

		

		//a.addMoneyFKX(228102044806488L, 58613,"异常处理");//泯灭人性 186

		//a.addMoneyFK(220102037362687L, 20000,"qq pay");//泯灭人性 186
		//a.addMoneyFK(181102040199326L, -10000,"购买道具");//泯灭人性 186

		//a.send("155","<gm/>servicecode=8110;key=gmClearAllTeamSpetrain;");

		//a.send("107", "<gm/>servicecode=8110;key=max_team_id;max=176963");		
		//a.send("199", "<gm/>servicecode=8110;key=max_team_id;max=85000");		
		//a.send("199", "<gm/>servicecode=8110;key=maxTeam;size=1400");
		//a.send("199", "<gm/>servicecode=8110;key=runSuperPrice;size=0");

		//a.send("199", "<gm/>servicecode=8110;key=clear_team_off");
		//a.send("199", "<gm/>servicecode=8110;key=honor");
		//a.send("101", "<gm/>servicecode=8110;key=mission");

		//a.send("240", "<gm/>servicecode=8110;key=x_player_reload");		

		//a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=520:6260,310:780,261:450,205:36,352:2,519:560,124:230,291:600,286:100,311:50;info=系统奖励"); //建议奖励

		//a.addMoneyFK(125102032507846L,  -120000,"交易");//明月流风
		//a.addMoneyFK(125102034442073L,   120000,"交易");//天下无双  明月流风
		//a.addMoneyFK(125102034386752L,  60000,"交易");//曲靖小蜜蜂
		//a.addMoneyFK(125102034256913L,  25000,"交易");//曲靖猩猩
		//a.addMoneyFK(125102034327833L,  60000,"交易");//曲靖野牛
		//a.addMoneyFK(125102032973952L,   50000,"交易");//葫芦艸璀璨叔 124

		//a.addMoneyFK(240102044883855L, -30000,"交易");//幻一队
		//a.addMoneyFK(135102031820896L,  33000,"交易");//神乄梦幻
		
		
		//a.send("101", "<gm/>servicecode=8001;teamId=103102037474393;propIds=434:50,435:50,436:30,444:100;info=系统奖励");//系统补发20161016

		//a.send("227", "<gm/>servicecode=8001;teamId=227102044795850;propIds=284:500,285:500,286:500,261:500,291:500;info=系统奖励"); //建议奖励
		//a.send("227", "<gm/>servicecode=8001;teamId=227102044795850;propIds=114:500,128:888,129:500,130:200;info=系统奖励"); //建议奖励
		//a.send("227", "<gm/>servicecode=8001;teamId=227102044795850;propIds=435:100,436:100,437:100,444:100,445:100,446:100,114:100,129:100,20:10000;info=系统奖励"); //建议奖励


		//a.send("155","<gm/>servicecode=8007;type=9;tableId=4");

		//a.send("224","<gm/>servicecode=8110;key=league_arena");

		//a.send("107","<gm/>servicecode=8110;key=cup_reload");
		//a.send("107", "<gm/>servicecode=8110;key=cup_gm_test;teamId=0");

		/* 
		a.send("241","<gm/>servicecode=8110;key=gmReloadSystemActive");
		a.send("241","<gm/>servicecode=8110;key=gmReloadSystemActiveCfg");
		a.send("241","<gm/>servicecode=8110;key=reload_sys_active");
		/**/
		
		
		//a.send("101", "<gm/>servicecode=8110;key=play_offs_rate;");
		//a.send("216", "<gm/>servicecode=8110;key=nba_data_change");	

		
		//a.send("102", "<gm/>servicecode=8110;key=gm_gameElitePKExce;exeType=1");	

		//a.send("140", "<gm/>servicecode=8001;teamId=140102032636293;propIds=291:400,311:300,348:4,114:100,129:100;info=系统奖励");//系统补发20161016


		


		//a.send("103", "<gm/>servicecode=8110;key=gm_giftGamePKLadders;");

		//a.send("101", "<gm/>servicecode=8110;key=union_hall_change_time;pk_week=,5,6,;pk_hour=20;pk_gift_week=6;pk_gift_hour=21;");

		//a.addBdMoneyFK(216102044594312L, -75000,"积分");

		//a.addMoneyFKX(166102037946491L, 100,"异常");

		//a.addMoneyFK(125102032507846L, -1000,"购买道具");
		//a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=129:200;info=抽奖活动");


		//a.addMoneyFK(138102034512660L, -2,"购买道具");
		//a.send("138", "<gm/>servicecode=8001;teamId=138102034512660;propIds=291:700;info=购买道具");

		//a.addMoneyFK(139102034211287L, 1000, "qq pay");//"2017"

		//a.addMoneyJSF(143102034693699L, -43800,"异常处理");
		//a.addMoneyFK(209102040297097L, 20000,"qq pay");//jason
		//a.addMoneyFK(208102030744845L, 20000,"qq pay");//比哥yzj
		//a.addMoneyFK(207102030771193L, 20000,"qq pay");//一叶飘零
		//a.addMoneyFK(208102044204193L, 10000,"qq pay");//紫金丶Lakers
		//a.addMoneyFK(102102030710723L, 200000,"qq pay");//ben
		//a.addMoneyFK(153102034119989L,20000,"qq pay");//乄平凡人灬 151 *
		

		//a.send("140", "<gm/>servicecode=8001;teamId=140102034266683;propIds=199:7,348:7,420:10,429:10,432:10,261:100,291:100,520:1000,518:30,442:10;info=系统奖励");
		//a.send("155", "<gm/>servicecode=8001;teamId=156102030711001;propIds=199:1000;info=系统奖励");

		//a.addMoneyFK(134102033500660L, 20000,"qq pay");//KsAs丶惜朝 135 * 丨娛樂丶教授
		//a.addMoneyFK(107102033237969L,   20000,"qq pay");//Royal丶丸子 107
		//a.addMoneyFK(157102036881516L, 20000,"qq pay");//力83兮7盖4 157
		//a.addMoneyFK(125102032973952L, 20000,"qq pay");//葫芦艸璀璨叔 124
		
		//a.addMoneyFK(117102031738642L, 2000,"qq pay");//M丶Jordan 116
		//a.addMoneyFK(161102037416372L, 90000,"qq pay");//丿桃源丶世纪 160
		//a.addMoneyFK(134102031763725L, 20000,"qq pay");//請叫我蠻子 134		
		//a.addMoneyFK(159102037911272L, 10000,"qq pay");//泔水桶 157
		//a.addMoneyFK(160102032677811L, 20000,"qq pay");//刀光丶 160
		//a.addMoneyFK(169102038555411L, 20000,"qq pay");//oldboy 162		
		//a.addMoneyFK(104102030840425L, 20000,"qq pay");//约个啪啪啪乀 104 白子画乀		
		//a.addMoneyFK(155102032639869L, 10000,"qq pay");//無悔丶沫沫 155 V丶DiESeL
		//a.addMoneyFK(155102036450615L, 7500,"qq pay");//力83兮7盖4 155
		//a.addMoneyFK(125102032486417L, 3000,"qq pay");//力83曦7蓋4 124
		//a.addMoneyFK(157102036881516L, 20000,"qq pay");//力83兮7盖4 157
		//a.addMoneyFK(143102035213788L, 20000,"qq pay");//浅冬初雪 142
		//a.addMoneyFK(135102033605728L, 20000,"qq pay");//記住哥的臉 134
		//a.addMoneyFK(110102031265225L, 10000,"qq pay");//丿姜尚灬子牙 107
		//a.addMoneyFK(144102034914793L, 20000,"qq pay");//tianxiawudi 142
		//a.addMoneyFK(187102038063365L, 20000,"qq pay");//泯灭人性 186
		//a.addMoneyFK(209102043826768L, 20000,"qq pay");//梦之火箭队 209
		//a.addMoneyFK(210102035213788L, 3000,"qq pay");//倾国倾城乄哥 210
		//a.addMoneyFK(102102030902999L, 20000,"qq pay");//战斧乄絕殺 102
		//a.addMoneyFK(153102036220021L, 20000,"qq pay");//江南情歌 151		
		//a.addMoneyFK(211102034555452L, 5000,"qq pay");//力83兮7盖4 211
		//a.addMoneyFK(205102043975061L, 20000,"qq pay");//50丶 205
		//a.addMoneyFK(157102036877831L, 20000,"qq pay");//丿龍城灬璀璨 157
		//a.addMoneyFK(211102044360644L, 20000,"qq pay");//Royal丶卡徒 Royal丶巴豆 211
		//a.addMoneyFK(154102036517403L, 100,"qq pay");//再见乄梦之队
		/* 
		for(int i=1;i<=1;i++){
			//a.addMoneyFK(205102043975061L, 20000,"qq pay");//50丶 205
			//a.addMoneyFK(228102044422280L, 100,"qq pay");//小小浪
			//a.addMoneyFK(134102033475205L, 20000,"qq pay");//爷见那脸烦 130  Glory丶迷糊  Top丨67
			//a.addMoneyFK(153102034119989L, 100,"qq pay");//乄平凡人灬 151 *  灬鬼宿乀
			//a.addMoneyFK(105102031216741L, 1000,"qq pay");//佳琪  灬殘灬
			//a.addMoneyFK(239102044878039L, 300,"qq pay");//王者五杀 7天、一天3000卷  到30号 冲10次 
			//a.addMoneyFK(239102044878020L, 200,"qq pay");//Mr老黑 200*10一次 到3号
			//a.addMoneyFK(239102043802642L, 100,"qq pay");//闲云野鹤
			//a.addMoneyFK(186102041027812L, 10000,"qq pay");//猫儿咪惹火队 186
			//a.addMoneyFK(134102033500660L, 20000,"qq pay");//KsAs丶惜朝 135
			//a.addMoneyFK(191102032193915L, 20000,"qq pay");//一世丶孤王 191  丶無情ing  *
			//a.addMoneyFK(222102039026383L, 20000,"qq pay");//贝加尔湖畔  *  *  *			
			//a.addMoneyFK(216102044599665L, 20000,"qq pay");//丨麝香丶夫人 216   ***  Funny丨King-------------- 三剑客丨FK   卩s丶冷葉眸------封号
			//a.addMoneyFK(169102033181754L, 20000,"qq pay");//铜锣湾丶逍遥 162 兲安門丶三楼   *--------------YxMz丶絕版
			//a.addMoneyFK(213102037627241L, 5000,"qq pay");//情谊丶泡泡 213 *			
			//a.addMoneyFK(103102037474393L, 20000,"qq pay");//巔峰乄禪師 103 Mc丶碎碎唸  *
			//a.addMoneyFK(215102037362687L, 20000,"qq pay");//乱世灬七少   215 *
			//a.addMoneyFK(217102037362687L, 10000,"qq pay");// | 17丶	
			//////a.addMoneyFK(210102040123653L,20000,"qq pay");//删除记忆丶  210  丨倾城灬乔丹
			//a.addMoneyFK(211102044422011L,10000,"qq pay");//力83兮7葢4  211   * 
			//a.addMoneyFK(137102033823536L, 20000,"qq pay");//龍丶 136*	丶龍灬神  逸诗忧梦 $$
			//a.addMoneyFK(140102032636293L, 20000,"qq pay");//丶韦灬德 136  $$
			//a.addMoneyFK(215102044583060L,10000,"qq pay");//葫芦娃丶 215   *  情義丶一刀--------------Voc丶黑羿  貝勒爺丶
			//a.addMoneyFK(209102040123653L,20000,"qq pay");//狂砍十条街丶 209 *--------------一笑丶泯恩仇  2丶桐庐  厨鰰丨灬雙爺
			//a.addMoneyFK(216102044604155L, 5000,"qq pay");//依然饭特睡 216   *
			//a.addMoneyFK(216102037362687L, 15000,"qq pay");//Agoni丶神话 216   *
			//a.addMoneyFK(221102037362687L, 20000,"qq pay");//逆天乀小妖 221 *
			//a.addMoneyFK(222102037362687L,  20000,"qq pay");//小丸子灬 222 *
			//a.addMoneyFK(144102037362687L, 12000,"qq pay");//Royal丶小萱 *
			//a.addMoneyFK(224102037362687L, 20000,"qq pay");//暖色灬 *** Star灬莫離
			//a.addMoneyFK(211102037362687L, 24000,"qq pay");//Royal丶糖宝 211 丨Cute丶糖寳
			//a.addMoneyFK(223102037362687L, 20000,"qq pay");//柠檬灬 223 *
			//a.addMoneyFK(215102044583066L, 20000,"qq pay");//情義丶紫楓 215   -------------- !!!!!!!!!!!不再发   後會無期ooo

			//a.addMoneyFK(186102040974964L, 10000,"qq pay");//辉煌丶琦少 186			
			//a.addMoneyFK(107102033237969L, 23000,"qq pay");//Royal丶丸子 107
			//a.addMoneyFK(157102036881516L, 20000,"qq pay");//力83兮7盖4 157
			//a.addMoneyFK(213102043009814L, 3000,"qq pay");//老不死在哪里 213
			//a.addMoneyFK(213102042773717L, 3000,"qq pay");//灬戰鉮鉶兲乀 213
			//a.addMoneyFK(125102032507846L, 20000,"qq pay");//明月流风 124
			//a.addMoneyFK(187102038063365L, 20000,"qq pay");//泯灭人性 186
			//a.addMoneyFK(110102031265225L, 20000,"qq pay");//丿姜尚灬子牙 107
			//a.addMoneyFK(135102031820896L, 20000,"qq pay");//神乄梦幻 134  淼丶焱丨
			//a.addMoneyFK(146102035256086L, 10000,"qq pay");//傲丶你妹 145			
			//a.addMoneyFK(161102037416372L, 20000,"qq pay");//丿桃源丶世纪 160			
			//a.addMoneyFK(215102030890773L, 20000,"qq pay");//乱世灬三少   215
			//a.addMoneyFK(217102030890773L, 20000,"qq pay");//   217  32丶
			//a.addMoneyFK(155102032639869L, 20000,"qq pay");//無悔丶沫沫 155 V丶DiESeL
			//a.addMoneyFK(104102031167538L, 20000,"qq pay");//其实号没卖 104 生死看淡
			//a.addMoneyFK(104102030840425L, 20000,"qq pay");//约个啪啪啪乀 104 白子画乀  大老板丶
			//a.addMoneyFK(215102037419487L, 20000,"qq pay");//花溅泪   215
			//a.addMoneyFK(216102044593452L, 3000,"qq pay");//巴赫乀协奏曲  216 丨Forsaken 别捅咕我
			//a.addMoneyFK(216102044594312L, 20000,"qq pay");//大梦春秋   216 三剑客丨DK
			//a.addMoneyFK(216102038485125L, 20000,"qq pay");//Happy丨King   216
			//a.addMoneyFK(212102044463981L, 20000,"qq pay");//丶杰哥   212 
			//a.addMoneyFK(215102037050427L, 10000,"qq pay");//麻神丶   215  啦啦啦红太阳
			//a.addMoneyFK(208102041940603L, 10000,"qq pay");//丨star丶堕落   206  神话乄张大帅
			//a.addMoneyFK(130102031729156L, 20000,"qq pay");//开心消消乐   130
			//a.addMoneyFK(178102039155950L, 20000,"qq pay");//木木丶   173
			//a.addMoneyFK(104102031660782L, 5000,"qq pay");//请叫我大sa 104 乄背锅灬点  ZS丶城主
			//a.addMoneyFK(104102030898192L, 20000,"qq pay");//ZS丶神婷
			
			//a.addMoneyFK(180102040238014L, 20000,"qq pay");//Im丶草根部落  Ts乄寇莫放肆
			//a.addMoneyFK(174102039144766L, 20000,"qq pay");//復仇丿老湖
			//a.addMoneyFK(138102034512660L, 20000,"qq pay");//丶魔灬鬼
			//a.addMoneyFK(221102044683632L, 1000,"qq pay");//杰哥丶 国信 AL丶张大帅  神话乄斌哥
			//a.addMoneyFK(224102037943712L, 20000,"qq pay");//Star灬冷色  Star灬莫棄
			//a.addMoneyFK(156102030711001L, 20000,"qq pay");//賭丶丨發灬
			//a.addMoneyFK(157102031402729L, 5000,"qq pay");//賭丶丨中灬
			//a.addMoneyFK(155102036579770L, 20000,"qq pay");//賭丶丨南灬
			//a.addMoneyFK(157102034995328L, 20000,"qq pay");//Silent丶愛涙   賭丶丨愛神灬
			//a.addMoneyFK(227102036625075L, 1000,"qq pay");//God丶V  V爷爷治结巴   V丶老芭比
			//a.addMoneyFK(227102044795850L, 20000,"qq pay");//丨君临丶天下   对三‘
			//a.addMoneyFK(146102035165563L, 20000,"qq pay");//勥烎灬戦榊
			//a.addMoneyFK(145102034964998L, 20000,"qq pay");//銘鍆丨緃横
			//a.addMoneyFK(116102031547952L, 1000,"qq pay");//力83兮7盖4 116   乀團長丶L  单机游戏
			//a.addMoneyFK(116102031777898L, 1000,"qq pay");//红高粱模特队
			//a.addMoneyFK(232102044836710L, 20000,"qq pay");//王者超神
			//a.addMoneyFK(233102044841224L, 20000,"qq pay");//王者超神
			//a.addMoneyFK(234102044847056L, 200,"qq pay");//王者超神
			//a.addMoneyFK(235102044854184L, 20000,"qq pay");//蓝火加特林
			//a.addMoneyFK(113102031450314L, 10000,"qq pay");//此题无解
			//a.addMoneyFK(237102035296511L, 100,"qq pay");//再战江湖
			//a.addMoneyFK(234102044300939L, 20000,"qq pay");//丿Sky灬琥珀
			//a.addMoneyFK(240102044883855L, 20000,"qq pay");//狂砍七条街丶  0.5 大号
			try{
				Thread.sleep(4000);
			}catch(Exception e){}
		}
		/**/

		//a.addMoneyFK(163102037741752L, 20000,"qq pay");//So丶张大彪
		//a.addMoneyFK(231102031273752L, 100,"qq pay");//生筎夏花丶
		//a.addMoneyFK(228102044806488L, 20000,"qq pay");//神鸣殿
		//a.addMoneyFK(101102030511647L, 20000,"qq pay");//点烟抽寥寂
		//a.addMoneyFK(231102044682878L, 20000,"qq pay");//牛牛
		//a.addMoneyFK(227102044579253L, 10000,"qq pay");//God丶v  V爷爷来帮衬   老炮爷
		//a.addMoneyFK(227102033204163L, 20000,"qq pay");//V爷爷来捧场
		//a.addMoneyFK(127102032632885L, 10000,"qq pay");//桂林丨射神  一朵小红花
		//a.addMoneyFK(224102044743544L, 10000,"qq pay");//紫禁丶小艾
		//a.addMoneyFK(166102038323526L, 10000,"qq pay");//All丶冷血
		//a.addMoneyFK(198102034218005L, 20000,"qq pay");//灬玖爷乀
		
		//a.addMoneyFK(232102044836052L, 10000,"qq pay");//Royal丶筱冷
		//a.addMoneyFK(232102044833639L, 20000,"qq pay");//卩灬可乐丶
		//a.addMoneyFK(232102044693670L, 10000,"qq pay");//阿梓
		//a.addMoneyFK(232102034656575L, 20000,"qq pay");//791wow
		//a.addMoneyFK(232102033232987L, 10000,"qq pay");//篮球王朝
		//a.addMoneyFK(166102038018512L, 20000,"qq pay");//为爱戰天下丶  海与迟落梦丶
		//a.addMoneyFK(136102033760099L, 20000,"qq pay");//三國丶陌路
		//a.addMoneyFK(203102035819765L, 10000,"qq pay");//神话乄杜兰特
				
		//a.addMoneyFK(232102039192820L, 10000,"qq pay");//2丶桐庐  0.5 大号  波妹么么哒   陈丶冠希
		//a.addMoneyFK(232102033455127L, 20000,"qq pay");//请叫我传奇 0.5
		//a.addMoneyFK(232102044692173L, 20000,"qq pay");//巅峰 0.5
		//a.addMoneyFK(233102044759305L, 20000,"qq pay");//欧弟  0.5 大号
		//a.addMoneyFK(234102044841218L, 5000,"qq pay");//0.5 我最帅  大号  Wang丶  丿Sky灬王少 3区   苍狼丨牛爷  饿狼丨牛爷   Royal丶牛爷
		//a.addMoneyFK(235102036312018L, 20000,"qq pay");//勋章  0.5 大号
		//a.addMoneyFK(236102044862084L, 20000,"qq pay");//逝丶去  0.5 大号    冯丶Timor  米乄麒麟灬
		//a.addMoneyFK(235102044854184L, 20000,"qq pay");//蓝火加特林
		//a.addMoneyFK(227102044795850L, 20000,"qq pay");//丨君临丶天下   对三‘
		//a.addMoneyFK(235102031220499L, 10000,"qq pay");//07无名小卒
		
		//a.addMoneyFK(237102035296511L, 2100,"qq pay");//再战江湖
		//a.addMoneyFK(237102034656575L, 10000,"qq pay");//綄羙丨五杀
		//a.addMoneyFK(237102044868827L, 20000,"qq pay");//TCP丶F 0.5
		//a.addMoneyFK(101102031395278L, 5000,"qq pay");//名门乄一梦
		//a.addMoneyFK(222102030922592L, 3000,"qq pay");//RS丶文謙
		//a.addMoneyFK(238102034253298L, 10000,"qq pay");//Mr老黑
		//a.addMoneyFK(238102044833639L, 10000,"qq pay");//兄弟丶
		//a.addMoneyFK(238102044824664L, 20000,"qq pay");//钢炮
		//a.addMoneyFK(238102040771884L, 20000,"qq pay");//认真的雪
		//a.addMoneyFK(238102044874690L, 10000,"qq pay");//该如何回忆
		//a.addMoneyFK(238102033456573L, 20000,"qq pay");//天尊丨凡小尘 Vienna乀凡尘
		//a.addMoneyFK(104102030898192L, 10000,"qq pay");//半個夏天的風		
		//a.addMoneyFK(144102034856262L, 10000,"qq pay");//殺人不眨眼
		//a.addMoneyFK(163102030718057L, 10000,"qq pay");//All丶麦兜  凡人多烦事丶
		//a.addMoneyFK(103102041457301L, 10000,"qq pay");//Team丶华子
		//a.addMoneyFK(241102032637817L, 20000,"qq pay");//天之骄子
		//a.addMoneyFK(241102044878127L, 20000,"qq pay");//钢炮
		
		//a.addMoneyFK(190102041718907L, 10000,"qq pay");//灬財爷乀
		//a.addMoneyFK(237102044667472L, 20000,"qq pay");//天尊丨索命  Vienna乀乐毅
		
		//a.addMoneyFK(239102043802642L, 20000,"qq pay");//闲云野鹤
		//a.addMoneyFK(239102044878039L, 3000,"qq pay");//王者五杀 7天、一天3000卷  到30号 冲10次 
		//a.addMoneyFK(239102044878020L, 20000,"qq pay");//Mr老黑 2000一次 到3号
		//a.addMoneyFK(239102035029500L, 20000,"qq pay");//再战江湖
		//a.addMoneyFK(239102034856262L, 20000,"qq pay");//丶冫氵
		//a.addMoneyFK(239102044878145L, 20000,"qq pay");//事了拂衣去
		//a.addMoneyFK(239102044868340L, 20000,"qq pay");//50丶 0.5
		//a.addMoneyFK(239102044878040L, 20000,"qq pay");//珍奶不加珍珠  0.5 大号
		//a.addMoneyFK(240102044883855L, 20000,"qq pay");//狂砍七条街丶  0.5 大号
		//a.addMoneyFK(240102032947970L, 3000,"qq pay");//2wrdq1  0.5 大号
		//a.addMoneyFK(240102041632789L, 10000,"qq pay");//大魔王
		//a.addMoneyFK(240102042772778L, 20000,"qq pay");//封神丨黄飞虎
		//a.addMoneyFK(240102034856262L, 10000,"qq pay");//丶冫氵
		//a.addMoneyFK(240102044883909L, 20000,"qq pay");//狂砍六条街
		//a.addMoneyFK(240102044883852L, 20000,"qq pay");//Judy珠珠i
		//a.addMoneyFK(240102032833297L, 20000,"qq pay");//小旭旭
		//a.addMoneyFK(163102037686007L, 20000,"qq pay");//Hey丶girl
		//a.addMoneyFK(187102039256136L, 20000,"qq pay");//你皮丶任你皮
		//a.addMoneyFK(190102041826321L, 20000,"qq pay");//细胞君丶
		
		//a.addMoneyFK(241102044737356L, 20000,"qq pay");//TL01 0.5
		//a.addMoneyFK(241102044774584L, 1000,"qq pay");//TL02 0.5
		
		
		//a.send("240", "<gm/>servicecode=8001;teamId=240102044883855;propIds=431:500,432:500,433:500;info=系统奖励");		
		
		//a.send("241", "<gm/>servicecode=8001;teamId=241102044737356;propIds=124:100;info=系统奖励");		
		//a.send("241", "<gm/>servicecode=8001;teamId=241102044774584;propIds=149:1000;info=系统奖励");		
		
		//a.send("102", "<gm/>servicecode=8001;teamId=104102043199865;propIds=124:260,333:36,315:450,323:6,353:8,203:5000,294:5000,310:20000,313:1000,932:20;info=系统奖励");		
		
		//a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=261:500;info=系统奖励");		
		//a.send("239", "<gm/>servicecode=8001;teamId=239102044878039;propIds=124:50;info=系统奖励");	
		//a.send("238", "<gm/>servicecode=8001;teamId=238102040771884;propIds=124:1,348:1,146:30,305:2000;info=系统奖励");		
		
		//a.send("239", "<gm/>servicecode=8001;teamId=239102043802642;propIds=261:1000,291:1000,203:5000,124:200,348:5,932:20,520:5000;info=系统奖励");
		//a.send("107", "<gm/>servicecode=8001;teamId=107102033237969;propIds=463:2000,464:2000,465:2000,466:2000,467:2000,468:2000,469:2000,470:2000,471:2000,472:2000;info=系统奖励");
		//a.send("107", "<gm/>servicecode=8001;teamId=113102031450314;propIds=291:1500;info=系统奖励");
		
		//a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=261:800;info=系统奖励");
		//a.send("235", "<gm/>servicecode=8001;teamId=235102036312018;propIds=124:200,188:20,322:5;info=系统奖励");
		//a.send("234", "<gm/>servicecode=8001;teamId=234102044841218;propIds=310:5000,199:50;info=系统奖励");
		//a.send("236", "<gm/>servicecode=8001;teamId=236102044862084;propIds=203:3000,333:5,434:200,435:200,436:200;info=系统奖励");
		//a.send("222", "<gm/>servicecode=8001;teamId=222102037362687;propIds=436:3000;info=系统奖励");
		
		//a.send("237", "<gm/>servicecode=8001;teamId=237102035296511;propIds=149:10,348:2,305:200;info=系统奖励");
		//a.send("237", "<gm/>servicecode=8001;teamId=237102034656575;propIds=149:1000,348:2;info=系统奖励");
		//a.send("237", "<gm/>servicecode=8001;teamId=237102044868827;propIds=284:200,285:200,286:200,124:100;info=系统奖励");
		//a.send("239", "<gm/>servicecode=8001;teamId=239102044868340;propIds=348:2,124:100,149:500;info=系统奖励");
		//a.send("239", "<gm/>servicecode=8001;teamId=239102044878040;propIds=294:2000,29:150,124:100;info=系统奖励");
		
		//a.send("240", "<gm/>servicecode=8001;teamId=240102044883852;propIds=294:2000;info=系统奖励");
		
		//a.send("234", "<gm/>servicecode=8001;teamId=234102044300939;propIds=261:800,291:800,315:20;info=系统奖励");
		
		//a.addMoneyFK(233102044833639L, 10000,"qq pay");//灬笑看丶人生
		//a.addMoneyFK(236102041632789L, 10000,"qq pay");//单机游戏丶
		//a.addMoneyFK(236102044730646L, 10000,"qq pay");//钢炮哥
		
		//a.addMoneyFK(224102037362687L, 20000,"qq pay");//暖色灬 ***
		//a.addMoneyFK(224102037943712L, 10000,"qq pay");//冷色灬
		//a.addMoneyFK(224102031556807L, 20000,"qq pay");//可可可可乐  蓝色灬
		//a.addMoneyFK(222102044487284L, 20000,"qq pay");//Royal丶雨夜  护腕丶雨夜 秋雨丶夜未央  一騎絕塵雨夜  帥學覇丶雨夜
		//a.addMoneyFK(223102044744295L, 20000,"qq pay");//冷雨夜
		//a.addMoneyFK(104102032058076L, 20000,"qq pay");//雨果森林
		//a.addMoneyFK(223102034656575L, 10000,"qq pay");//某先生
		//a.addMoneyFK(210102035760674L, 10000,"qq pay");//厨神丨灬雙爺  高冷大帅比i
		//a.addMoneyFK(222102030922592L,   5000,"qq pay");//Someone 丿言西月灬 骄傲的少年  巴赫旧约  言丨西月 果汁分你一半  丶灯火阑珊处 1900  巴豆豆灬
		//a.addMoneyFK(129102032817918L, 20000,"qq pay");//一把骨头 124
		//a.addMoneyFK(104102031167538L, 20000,"qq pay");//其实号没卖 104 生死看淡
		//a.addMoneyFK(224102044683632L, 20000,"qq pay");//蹲厕24
		//a.addMoneyFK(224102044755023L, 20000,"qq pay");//戰乄白起
		//a.addMoneyFK(226102044782575L, 10000,"qq pay");//狼王乄加内特  Tiny   神丨姜太公
		//a.addMoneyFK(226102037416564L, 10000,"qq pay");//甜瓜乄安东尼
		//a.addMoneyFK(144102034831173L, 1500,"qq pay");//Dq丶龍影

		//a.addMoneyFK(225102044773202L, 20000,"qq pay");//锦衣夜行
		//a.addMoneyFK(225102032637817L, 20000,"qq pay");//今晚da老虎
		//a.addMoneyFK(225102033588737L, -3000,"异常");//记住哥的脸
		//a.addMoneyFK(225102030918000L, 20000,"qq pay");//丿聚義乄溱天
		//a.addMoneyFK(225102044773284L, 20000,"qq pay");//2丶桐庐
		//a.addMoneyFK(225102035740533L, 20000,"qq pay");//菩提哥
		//a.addMoneyFK(205102039448369L, 20000,"qq pay");//王家大少
		//a.addMoneyFK(227102030472489L, 20000,"qq pay");//挥霍的年迈
		//a.addMoneyFK(227102044795753L, 10000,"qq pay");//某先生  Rs丶钢炮
		//a.addMoneyFK(227102044795850L, 20000,"qq pay");//丨君临丶天下   对三‘
		//a.addMoneyFK(227102034656575L, 20000,"qq pay");//791wow
		//a.addMoneyFK(227102044692868L, 20000,"qq pay");//三三三
		//a.addMoneyFK(227102030918000L, 20000,"qq pay");//传奇丶逍遥子
		//a.addMoneyFK(227102030937704L, 20000,"qq pay");//传奇丶贝吉塔
		//a.addMoneyFK(105102031216741L, 10000,"qq pay");//佳琪  灬殘灬
		//a.addMoneyFK(222102044632078L, 10000,"qq pay");//星爷丶星爷 紫禁丶狂刀 Rs丶狂刀
		//a.addMoneyFK(228102033568289L, 20000,"qq pay");//燚燚燚燚
		//a.addMoneyFK(228102044422280L, 20000,"qq pay");//小小浪
		//a.addMoneyFK(228102044806498L, 20000,"qq pay");//一路溜吧	2	
		//a.addMoneyFK(228102030472489L, 20000,"qq pay");//我就是我 1
		//a.addMoneyFK(228102044692173L, 20000,"qq pay");//紫金1
		//a.addMoneyFK(228102033455127L, 20000,"qq pay");//请叫我传奇1		
		
		//a.addMoneyFK(229102034656575L, 20000,"qq pay");//791wow
		//a.addMoneyFK(229102044813141L, 20000,"qq pay");//只想当个村长
		//a.addMoneyFK(229102044812983L, 20000,"qq pay");//王者乄归来  DJX丨一言神
		//a.addMoneyFK(229102033455127L, 20000,"qq pay");//请叫我传奇 5折
		//a.addMoneyFK(229102030918000L, 10000,"qq pay");//梦魔丶逍遥侠
		
		//a.addMoneyFK(230102034253298L, 20000,"qq pay");//Mr老黑	
		//a.addMoneyFK(230102044806498L, 20000,"qq pay");//一路溜吧
		//a.addMoneyFK(228102042062108L, 10000,"qq pay");//李有様丶  V你媽了个啵
		//a.addMoneyFK(149102035728621L, 20000,"qq pay");//Ares丶低调
		
		//a.addMoneyFK(231102044829110L, 20000,"qq pay");//Royal丶筱冷
		//a.addMoneyFK(231102033455127L, 20000,"qq pay");//请叫我传奇 5折
		//a.addMoneyFK(231102044829107L, 20000,"qq pay");//Reaper丶 10W 送124 350
		//a.addMoneyFK(231102032677745L, 20000,"qq pay");//sakuzra
				
		//a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=203:3000;info=系统奖励");
		
		//a.addMoneyFK(214102044555404L, 20000,"qq pay");//丨灸舞丶至尊 214   *		
		//a.addMoneyFK(128102032722741L, 20000,"qq pay");//丿龍城灬飛雪 124
		//a.addMoneyFK(213102030922592L, 5000,"qq pay");//树先生 213
		//a.addMoneyFK(213102043009814L, 20000,"qq pay");//老不死在哪里 213		

		//a.addMoneyFK(213102042773717L, 20000,"qq pay");//灬戰鉮鉶兲乀 213  媈瑝乀瀟灑滒

		//a.addMoneyFK(213102035689100L, 10000,"qq pay");//香爱壹升 213
		//a.addMoneyFK(185102031837728L, 10000,"qq pay");//叁聚氰胺  179  *
		//a.addMoneyFK(102102030737943L, 20000,"qq pay");//三分乄絕殺  102 Top丶王子 AS一切随心
		//a.addMoneyFK(102102036310920L, 20000,"qq pay");//三秒乄絕殺  102
		//a.addMoneyFK(143102034710980L, 20000,"qq pay");//彼岸乄云海  142
		//a.addMoneyFK(214102040613703L, 20000,"qq pay");//乄風雲丨奋斗  214
		//a.addMoneyFK(146102035256086L, 10000,"qq pay");//傲丶你妹 145
		//a.addMoneyFK(134102033475205L, 20000,"qq pay");//爷见那脸烦 130  Glory丶迷糊
		//a.addMoneyFK(181102032290724L, 10000,"qq pay");//雷神丶 179 陳冠希丶
		//a.addMoneyFK(181102040244048L, 20000,"qq pay");//恒大丶 179
		//a.addMoneyFK(226102044783962L, 10000,"qq pay");//马泽法克丶
		//a.addMoneyFK(226102030472489L, 20000,"qq pay");//卡卡
		//a.addMoneyFK(226102033266144L, 20000,"qq pay");//随封乱舞
		//a.addMoneyFK(226102039192820L, 2000,"qq pay");//TL01
		//a.addMoneyFK(226102044783999L, 20000,"qq pay");//某先生
		//a.addMoneyFK(226102037416564L, 20000,"qq pay");//灌篮乄Melo

		//a.addMoneyFK(215102040006171L, 20000,"qq pay");//我爱寳姐  215		
		//a.addMoneyFK(215102030890773L, 10000,"qq pay");//乱世灬三少   215
		//a.addMoneyFK(212102044463981L, 20000,"qq pay");//隐丶杰哥   212
		//a.addMoneyFK(215102037419487L, 10000,"qq pay");//花溅泪   215
		//a.addMoneyFK(199102030908775L, 20000,"qq pay");//落星辰丶   199
		//a.addMoneyFK(217102030922592L, 20000,"qq pay");//13丶 217
		//a.addMoneyFK(216102031463435L, 5000,"qq pay");//肖邦乀协奏曲  216
		//a.addMoneyFK(152102036106575L, 20000,"qq pay");//砸钱or砸电脑  151  灬轸宿乀
		//a.addMoneyFK(216102044593452L, 20000,"qq pay");//宮商角徵羽  216  别捅咕我 别捅咕我
		//a.addMoneyFK(149102035680536L, 5500,"qq pay");//珈蓝丶可乐  147 爺有钱乄任性  丿征服灬天下
		//a.addMoneyFK(220102031595580L, 2100,"qq pay");//義灬先生  220
		//a.addMoneyFK(220102043270533L, 10000,"qq pay");//義灬至尊宝  220
		//a.addMoneyFK(220102044064770L, 20000,"qq pay");//一琳六八  220
		//a.addMoneyFK(101102031285646L, 14280,"qq pay");//NYC丶紅河道  101
		//a.addMoneyFK(217102044589664L, 20000,"qq pay");//黑凤梨 216
		//a.addMoneyFK(155102030986960L, 20000,"qq pay");//阿伦丶艾弗森 155
		//a.addMoneyFK(221102034656575L, 20000,"qq pay");//御海临风 212
		//a.addMoneyFK(221102044678089L, 12000,"qq pay");//逆天乀龙少 221
		//a.addMoneyFK(194102031947851L, 20000,"qq pay");//BreEze丶浮夸 186
		//a.addMoneyFK(154102036352532L, 20000,"qq pay");//灬神乀 154 灬星宿乀
		//a.addMoneyFK(222102044666471L, 20000,"qq pay");//柳风拂面
		//a.addMoneyFK(222102044678089L, 20000,"qq pay");//熱血乄姑娘


		//a.send("160", "<gm/>servicecode=8110;key=book_reload;teamId=160102032677811");
		//a.send("136", "<gm/>servicecode=8110;key=book_reload;teamId=140102032636293");
		//a.send("203", "<gm/>servicecode=8110;key=book_reload;teamId=205102043975061");s
		//a.send("110", "<gm/>servicecode=8110;key=book_reload;teamId=110102031265225");
		//a.send("104", "<gm/>servicecode=8110;key=book_reload;teamId=104102034180246");
		//a.send("103", "<gm/>servicecode=8110;key=book_reload;teamId=103102037474393");

		//a.send("102", "<gm/>servicecode=8110;key=cup_gm_test;teamId=0");

		//a.addMoneyFK(209102040297097L, 20000,"qq pay");//

		//a.addMoneyFK(104102034180246L, 200,"图鉴异常");//

		//86泯灭人性 187102038063365
		//猫儿咪惹火队 186102041027812
		//40君临天下 140102033296328
		//73盛世丨灬托尼173102038922687
		//215 
		//140102034266683 | 魔罗
		//141102034366235 如来佛
		//185102031837728 叁聚氰胺
		//190102031093848 陪呆子读书
		//a.send("142", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=143102035213788");
		//a.send("186", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=173102038922687");

		/* 
		List<String> list = a.initData();
		for(String team_id:list){
			a.send(team_id.substring(0,3), "<gm/>servicecode=8011;teamId="+team_id+";clearTrade=1");
		}
		/* */

		/*
		a.init();

		for(String str:teamList){
			a.send(str.substring(0,3),"<gm/>servicecode=8001;teamId="+str+";propIds=2:1800;info=充值补偿");
		}
		 */
		//a.send("160", "<gm/>servicecode=8001;teamId=161102037416372;propIds=261:108;info=购买道具");
		//a.addMoneyFK(161102037416372L, -26000,"购买道具");		

		//a.send("130", "<gm/>servicecode=8110;key=exit_more;roomId=7");

		//a.send("124", "<gm/>servicecode=8110;key=clear_rebequadd");
		//a.send("103", "<gm/>servicecode=8110;key=clear_rebequadd");
		//a.send("186", "<gm/>servicecode=8110;key=clear_rebequadd");
		//a.send("199", "<gm/>servicecode=8110;key=nick_name_reload;teamId=201102043341437");


		//a.send("151", "<gm/>servicecode=8110;key=clear_team_vip_all");

		//a.send("101", "<gm/>servicecode=8110;key=gm_reloadGamePKLadders");


		//a.send("test_177", "<gm/>servicecode=8110;key=nba_data_change");//nba_data_change

		//a.send("102", "<gm/>servicecode=8110;key=low_player_price;shardId=104");
		//a.send("206", "<gm/>servicecode=8110;key=low_player_price;shardId=210");	
		//a.send("212", "<gm/>servicecode=8110;key=low_player_price;shardId=213");	
		//a.send("206", "<gm/>servicecode=8110;key=low_player_price;shardId=209");	

		//a.send("136", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=137102035768270");
		//a.send("101", "<gm/>servicecode=8110;key=game_buy_goods_reload");
		//a.send("test_177", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=101102030406254");
		//a.send("142", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=0");
		//a.send("186", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=0");

		//String msg = "亲爱的玩家，第21届跨服赛已开启，并调整了相关奖励，敬请关注！";
		
		//String msg = "亲爱的玩家，上午出现的很多界面点不开的情况已经修复，需玩家重新进入游戏，请大家相互转告！";

		//String msg = "亲爱的玩家，（新秀卡）道具可提前1分钟进入，也可待房间开启后进入，且不算选秀次数，请知悉! ";
		
		//String msg = "亲爱的玩家，上赛季没有打过比赛的球员，由于历史数据参考错误，今日身价更新后将恢复正常，敬请谅解！！！ ";
		
		String msg = "亲爱的玩家，抢购活动将于11:00开启，敬请关注！ ";
		
		//String msg = "亲爱的玩家， 因机房设备老化，我们预计于明天切换到新的机房，目前正在做各种数据互通测试，如果一切顺利，将于凌晨开始维护；我们尽量24小时内解决所有问题，如有新的调整，我们会第一时间在游戏内通知，谢谢大家的配合！";
		
		//String msg = "今天是七夕，梦之队祝所有玩家心有所属，节日快乐！";

		//String msg = "亲爱的玩家，为了净化游戏环境，请不要在世界频道辱骂攻击他人，凡举报超过10次的，一经核实，将进行1-3天封号处理，请大家配合，谢谢！何必以身试法？ ";

		//String msg = "[官方公告]梦之队-新征程9区将于今日18：00火爆开启，敬请关注！ ";
		
		//String msg = "[温馨提示]最近盗号频发，请有代练的玩家切记不要将二级密码告诉代练，同时锁好底薪球员（其实最好是通过底薪收集吃掉，阵容中用潜力降过的底薪），以防被盗被骗！！！ ";
		
		//String msg = "[官方公告]亲爱的玩家，12月30日维护后，全服底薪球员开卡张数将进行一次~大~大~大~调整！！！请各位玩家相互转告，以免造成损失？！？！";

		//String msg = "亲爱的玩家，系统将于8:50进行临时维护，约30分钟，请做好下线准备！！！";

		//String msg = "亲爱的玩家，所谓的刷券千万别上当！！！小心被盗号！！！";
		
		//String msg = "[官方公告]亲爱的玩家，新一届跨服天梯赛已开启，敬请关注！";

		//String msg = "亲爱的玩家，新届全服天梯赛奖励的球员头像，变更成相应的头像选择卡，敬请关注！另外：所谓的刷券千万别上当！！！小心被盗号！！！";

		//String msg = "亲爱的玩家，荣誉之塔活动已经结束，请还没有领奖的玩家速度领奖！";

		//String msg = "亲爱的玩家：因数据异常，胜负竞猜，胜分竞猜 活动暂时关闭，昨天与今天的竞猜本金已全额退换，给您带来不便，我们非常抱歉。";

		//tring msg = "亲爱的玩家，欢迎使用X科比(属性做了较大调整，下次26号更新后恢复)，欢迎使用系统队徽，来来来，一起用科比！！！科科科比比比~~~";

		//String msg = "亲爱的玩家，从2016年4月14日16:00到4月15日16:00期间，全服底薪《科比》开卡张数从49张降低到39张，请各位玩家仔细留意并相互转告。感谢您的支持！祝您游戏愉快！";

		//String msg = "亲爱的玩家，自2016-05-01开始，官方将不再受理账号被盗申诉(包括球员被盗，帐号被盗)，请各位玩家自行保管好账号密码安全，以免出现不必要的损失。由此给您带来的不便，我们非常抱歉。感谢您的支持！";

		//String msg = "亲爱的玩家，如果今天到明天16:00之间，服务器挂掉了，后续重启后，詹姆斯的底薪还是会变成1845，所以请大家务必尽快处理！";

		//String msg = "各位亲爱的玩家：为了提升游戏环境，梦之队运营团队将于下次(维护时)清理等级低于31级的球队。请各位玩家朋友相互转告。同时，下次更新也一并更新今年的新秀，敬请期待！";

		//String msg = "亲爱的玩家，底薪球星卡活动中的“限量底薪”球员的开启张数已调整完毕。";

		//String msg = "2016欧洲杯有奖竞猜，百万实物奖励大放送！看球猜球赢奖品，赶紧来参与吧！<br/><br/><a href[:eq:]'http://zq.qq.com/cp/a20160602ozb/index.htm' target[:eq:]'_blank'>[查看详细]</a>";

		//String msg = "《NBA范特西-王者篇》新服湖人16区，火爆开启，来即送巅峰巨星，加群更有特级礼包等你来领取。Q群：555288853，一起组建您的梦幻球队吧！<br/><a href[:eq:]'http://www.ftxgame.com/wan/nbaftx/server_list_page' target[:eq:]'_blank'>[查看详细]</a>";

		//String msg = "[官方公告]亲爱的玩家：为了减缓服务器压力，梦之队运营团队将于下次维护时清理等级低于25级的球队。请各位玩家朋友相互转告。 (季后赛4，5 区只清理低于10级的球队)";

		//String msg = "亲爱的玩家，新一届跨服天梯赛将于今日12:00开启，本次所奖励的球员头像变更成永久的，敬请关注！ ";

		//String msg = "亲爱的玩家，第四届跨服精英争霸赛已开启，敬请关注！";

		//String msg = "新年佳节到，向您问个好，身体倍健康，心情特别好，好运天天交，口味顿顿妙。梦之队祝您：猴年好运挡不住，猴年财源滚滚来！";

		//String msg = "春节到，放鞭炮：一响鸿运照，二响忧愁抛，三响烦恼消，四响财运到，五响收入高，六响身体好，七响心情妙，八响平安罩，九响幸福绕，十响事业节节高！--梦之队敬上";
		//a.send("102", "<gm/>servicecode=8012;msg="+msg);	

		//String msg = "季后赛总决赛竞猜（勇士 vs 骑士）今天14：00结束，速度来参加吧，常规赛里他们交手两次，各取一胜。勇士要称王？";
		//a.send("136", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=139102034084245");
		//a.send("143", "<gm/>servicecode=8110;key=gm_addStadiumJsf;teamId=143102034770298;jsf=1000000000");
		//a.send("101", "<gm/>servicecode=8110;key=cross_reload");
		//a.send("101", "<gm/>servicecode=8110;key=guess_player_reload");
		//a.send("101", "<gm/>servicecode=8110;key=cup_reload");

		//long teamId = 101102041231722l;
		//a.send((""+teamId).substring(0, 3),"<gm/>servicecode=8001;teamId="+teamId+";propIds=900:100,901:100,902:100,903:100,904:100;info=系统补发");
		//props=1;rand=0;propsId=104:1;player=X|1|250|350;
		//props=1;rand=0;propsId=104:2,131:1;equId=114:1,214:1,414:1,514:1,614:1;player=X|1|250|350;
		//props=1;rand=0;propsId=138:2,132:4,128:3;player=X|1|250|350;
		//props=1;rand=0;propsId=138:2,308:3,129:4;player=X|1|250|350;
		//props=1;rand=0;propsId=119:1,305:3,304:5;equId=513:1,213:1;player=X|1|250|350;
		//props=1;rand=0;propsId=124:1,164:1,305:5;equId=215:1,515:1;player=X|1|250|350;
		//增加球券
		//a.addMoneyFK(144102034057977L, 50,0,0,0,"bug返还");

		//long team = Long.parseLong("205102043974662");
		//a.send((team+"").substring(0, 3), "<gm/>servicecode=8110;key=gm_team_sponsor_reload;tp=5;teamId="+team+";");

		//a.send("110", "<gm/>servicecode=8110;key=hof_room_clear;teamId=110102031261476;");  //名人堂
		//a.send("102", "<gm/>servicecode=8110;key=league");
		//a.send("199", "<gm/>servicecode=8110;key=pk_group_clear;pkId=125");
		//a.send("157", "<gm/>servicecode=8110;key=gmLeagueClear;teamId=157102036861139;");
		//a.send("112", "<gm/>servicecode=8110;key=gmReloadStadiumMap;teamId=112102031389837;");
		//a.send("160", "<gm/>servicecode=8110;key=exit_group;teamId=165102031178372;pkId=115");
		//a.send("101", "<gm/>servicecode=8110;key=game_buy_goods_reload");//

		//a.send("202", "<gm/>servicecode=8110;key=gmClearTeamSpetrainByTeamId;teamId=202102038964770;");
		//a.send("136", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=139102034084245");
		//a.send("101", "<gm/>servicecode=8110;key=cross_reload");
		//a.send("101", "<gm/>servicecode=8110;key=guess_player_reload");

		//a.send("191", "<gm/>servicecode=8110;key=clear_team_off");
		//a.send("191", "<gm/>servicecode=8110;key=honor");

		//a.send("199", "<gm/>servicecode=8110;key=clear_team_vip_all");

		//a.send("105", "<gm/>servicecode=8110;key=exit_group;teamId=105102030784357;pkId=123");
		//a.send("100", "<gm/>servicecode=8110;key=game_buy_goods_reload");
		//a.send("199", "<gm/>servicecode=8110;key=game_guess_match_gift");
		//a.send("test_177", "<gm/>servicecode=8110;key=pkSpeed;size=20;");
		//a.send("102", "<gm/>servicecode=8110;key=active_icon;activeId=100");
		//a.send("test_177", "<gm/>servicecode=8110;key=active_icon;activeId=125");
		//a.send("202", "<gm/>servicecode=8110;key=gmClearTeamSpetrainByTeamId;teamId=202102032731573");

		//a.send((""+teamId).substring(0, 3),"<gm/>servicecode=8001;teamId="+teamId+";propIds=124:1,164:1,305:5;equIds=215:1,515:1;info=系统补发");
		//a.send("105", "<gm/>servicecode=8110;key=exit_group;teamId=105102030784357;pkId=123");
		//a.send("101", "<gm/>servicecode=8110;key=game_buy_goods_reload");
		//a.send("116", "<gm/>servicecode=8110;key=game_guess_match_gift");
		//a.send("102", "<gm/>servicecode=8110;key=active_icon;activeId=100");
		//a.send("test_177", "<gm/>servicecode=8110;key=active_icon;activeId=125");

		//a.send("test_177", "<gm/>servicecode=8110;key=PKWINTEAMID;pk_win_teamid=101102030406283;pk_win_cap=500;pk_win_zs=1");

		int[] shard_id = {101,102,107,116,124,136,142,147,155,162,173,186,199,222,231,237,239,240};
		//int[] shard_id = {239};
		for(int sid:shard_id){
			//a.send(sid+"", "<gm/>servicecode=8110;key=league");
			//a.send(sid+"", "<gm/>servicecode=8110;key=clear_team_all");
			//a.send(sid+"", "<gm/>servicecode=8110;key=max_team_id;max=24000");		
			//a.send(sid+"", "<gm/>servicecode=8110;key=pk_power_step;size=20");
			/* 
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=119");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=125");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=127");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=126");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=131");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=123");
			a.send(sid+"", "<gm/>servicecode=8110;key=pk_group_clear;pkId=124");
			 */
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_gmGroupBuy;tp=1;");
			//a.send(sid+"", "<gm/>servicecode=8110;key=clear_rebequadd");
			//a.send(sid+"", "<gm/>servicecode=8110;key=player_swap_lock;flag=0");
			//a.send(sid+"", "<gm/>servicecode=8110;key=nba_data_change");
			//a.send(sid+"", "<game-data-change/>");
			//a.send(sid+"", "<gm/>servicecode=8012;msg="+msg);
			//a.send(sid+"", "<gm/>servicecode=8110;key=clear_rebequadd");
			//a.send(sid+"","<gm/>servicecode=8110;key=cup_reload");
			//a.send(sid+"", "<gm/>servicecode=8110;key=play_offs_team_reload;");
			//a.send(sid+"", "<gm/>servicecode=8110;key=cup_clear_size;pk_size=5;clear=1");
			//a.send(sid+"", "<gm/>servicecode=8110;key=cup_gm_test;teamId=0");
			//a.send(sid+"", "<gm/>servicecode=8110;key=game_guess_match_gift");		
			//a.send(sid+"", "<gm/>servicecode=8110;key=PKWINTEAMID;pk_win_teamid=0;pk_win_cap=4000;pk_win_zs=0");//胜-保护--力8	
			//a.send(sid+"", "<gm/>servicecode=8110;key=PKWINTEAMID;pk_win_teamid=0;pk_win_cap=0;pk_win_zs=0");//不保护
			//a.send(sid+"", "<gm/>servicecode=8110;key=PKGAMEDEBUG;size=0");
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_reloadGamePKLadders");
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_giftGamePKLadders;");
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_clear_props_player_card;");
						
			//a.send(sid+"","<gm/>servicecode=8110;key=gmReloadSystemActive");
			//a.send(sid+"","<gm/>servicecode=8110;key=gmReloadSystemActiveCfg");
			//a.send(sid+"","<gm/>servicecode=8110;key=reload_sys_active");
			
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_gameElitePKExce;exeType=1");	
			//a.send(sid+"","<gm/>servicecode=8110;key=gm_cross_exe_shardId;exeShardId=100");
		}		

		for(int sid=101;sid<=210;sid++){
			//a.send(sid+"", "<gm/>servicecode=8110;key=gm_reloadGamePKLadders");
		}

		//a.send("test_177", "<gm/>servicecode=8110;key=nba_data_change");//nba_data_change
		//a.send("101", "<gm/>servicecode=8001;teamId=101102030765260;propIds=151:1");//nba_data_change
		/* 
		a.send("140", "<gm/>servicecode=8110;key=book_reload;teamId=140102033296328;");
		a.send("140", "<gm/>servicecode=8110;key=book_add;teamId=140102033296328;bookId=403;exp=5140;flag=1");
		a.send("140", "<gm/>servicecode=8110;key=book_add;teamId=140102033296328;bookId=203;exp=5100;flag=1");		
		a.send("140", "<gm/>servicecode=8110;key=book_add;teamId=140102033296328;bookId=103;exp=5140;flag=1");
		a.send("140", "<gm/>servicecode=8110;key=book_add;teamId=140102033296328;bookId=603;exp=5120;flag=1");
		a.send("140", "<gm/>servicecode=8110;key=book_add;teamId=140102033296328;bookId=503;exp=5100;flag=1");
		/**/
		//a.send("140", "<gm/>servicecode=8110;key=book_reload;teamId=140102033296328");

		/**
		
		| props=1;rand=0;propsId=294:50,203:50|351:2,29:20|520:700,468:300;                                             | 
		| props=1;rand=0;propsId=294:200,203:200|352:2,29:50|520:1500,468:1500;                                         | 
		| props=1;rand=0;propsId=445:300,436:400|353:1,330:1|520:3000,468:3000;                                         | 
		| props=1;rand=0;propsId=261:20,291:10,205:8,279:1|261:20,519:160,205:10|261:20,520:500,205:8;                  | 
		| props=1;rand=0;propsId=261:35,291:15,205:10|261:35,519:200,205:12|261:35,520:800,205:10;                      | 
		| props=1;rand=0;propsId=261:50,291:20,352:1|261:50,519:240,314:15|261:50,520:1000,352:1;                       | 
		| props=1;rand=0;propsId=261:70,291:30,124:30|261:70,519:280,124:40|261:70,520:1400,124:30|261:70,353:1,314:35; | 

		
		+----------------------------------------------------------------------------+
		| props=1;rand=0;propsId=294:100|351:2,29:20|520:500,469:300;                |
		| props=1;rand=0;propsId=294:250|352:2,29:50|520:1000,469:500;               |
		| props=1;rand=0;propsId=294:450|353:1,330:1|520:1500,469:700;               |
		| props=1;rand=0;propsId=294:1080|435:300,444:160|520:2000,469:1500;         |
		| props=1;rand=0;propsId=203:600,936:50|436:420,445:240|464:500,469:2000;    |
		| props=1;rand=0;propsId=203:1000,936:100|437:500,446:240|464:1000,469:2500; |
		| props=1;rand=0;propsId=936:300|438:550,447:320|464:1500,469:3000;          |
		+----------------------------------------------------------------------------+
		
		| props=1;rand=0;propsId=294:100|351:2,29:20|520:500,468:300|311:10,310:150;                  |
		| props=1;rand=0;propsId=294:250|352:2,29:50|520:1000,468:500|311:40,310:250;                 |
		| props=1;rand=0;propsId=294:450|353:1,330:1|520:1500,468:700|311:100,310:350;                |
		| props=1;rand=0;propsId=294:1080|435:300,444:160|520:2000,468:1500|311:150,310:750;          |
		| props=1;rand=0;propsId=203:600,936:50|436:420,445:240|463:500,468:2000|311:200,310:1000;    |
		| props=1;rand=0;propsId=203:1000,936:100|437:500,446:240|463:1000,468:2500|311:250,310:1250; |
		| props=1;rand=0;propsId=936:300|438:550,447:320|463:1500,468:3000|311:300,310:1500;          |
		 */
		
		/* 
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=520:500,469:300;info=系统奖励"); //建议奖励		
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=520:1000,469:500;info=系统奖励"); //建议奖励		
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=353:1,330:1;info=系统奖励"); //建议奖励	
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=520:2000,469:1500;info=系统奖励"); //建议奖励		
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=464:500,469:2000;info=系统奖励"); //建议奖励		
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=464:1000,469:2500;info=系统奖励"); //建议奖励		
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=464:1500,469:3000;info=系统奖励"); //建议奖励
		a.send("166", "<gm/>servicecode=8001;teamId=166102038018512;propIds=261:500,291:500,310:2000,203:1000;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=351:2,29:20;info=系统奖励"); //建议奖励		
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=352:2,29:50;info=系统奖励"); //建议奖励		
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=353:1,330:1;info=系统奖励"); //建议奖励	
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=435:300,444:160;info=系统奖励"); //建议奖励		
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=436:420,445:240;info=系统奖励"); //建议奖励		
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=437:500,446:240;info=系统奖励"); //建议奖励		
		a.send("237", "<gm/>servicecode=8001;teamId=237102044667472;propIds=438:550,447:320;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=351:2,29:20;info=系统奖励"); //建议奖励		
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=352:2,29:50;info=系统奖励"); //建议奖励		
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=353:1,330:1;info=系统奖励"); //建议奖励	
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=520:2000,469:1500;info=系统奖励"); //建议奖励		
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=203:600,936:50;info=系统奖励"); //建议奖励		
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=203:1000,936:100;info=系统奖励"); //建议奖励		
		a.send("238", "<gm/>servicecode=8001;teamId=238102033456573;propIds=936:300;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=520:500,468:300;info=系统奖励"); //建议奖励		
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=520:1000,468:500;info=系统奖励"); //建议奖励		
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=353:1,330:1;info=系统奖励"); //建议奖励	
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=520:2000,468:700;info=系统奖励"); //建议奖励		
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=436:420,445:240;info=系统奖励"); //建议奖励		
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=203:1000,936:100;info=系统奖励"); //建议奖励		
		a.send("125", "<gm/>servicecode=8001;teamId=125102032507846;propIds=936:300,124:150;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=261:20,519:160,205:10;info=系统奖励"); //建议奖励		
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=261:35,519:200,205:12;info=系统奖励"); //建议奖励		
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=261:50,520:1000,352:1;info=系统奖励"); //建议奖励		
		a.send("235", "<gm/>servicecode=8001;teamId=235102044854184;propIds=261:70,519:280,124:40,149:2000;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=520:700,468:300;info=系统奖励"); //建议奖励		
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=520:1500,468:1500;info=系统奖励"); //建议奖励		
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=445:300,436:400;info=系统奖励"); //建议奖励	
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=261:70,291:30,124:30,261:3000;info=系统奖励"); //建议奖励
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=438:400,286:300;info=系统奖励"); //建议奖励
		a.send("135", "<gm/>servicecode=8001;teamId=135102031820896;propIds=305:1000;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=310:50,311:2,350:2;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=310:150,311:7,277:1;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=310:300,311:12,333:1,278:1;info=系统奖励"); //建议奖励	
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=261:20,520:500,205:8;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=261:35,520:800,205:10;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=261:50,520:1000,352:1;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102032677745;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=310:300,311:12,333:1,278:1;info=系统奖励"); //建议奖励	
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102031660782;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=261:20,519:160,205:10;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=261:35,520:800,205:10;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=261:50,520:1000,352:1;info=系统奖励"); //建议奖励		
		a.send("231", "<gm/>servicecode=8001;teamId=231102044682878;propIds=261:70,520:1400,124:30;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=310:300,311:12,333:1,278:1;info=系统奖励"); //建议奖励	
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("104", "<gm/>servicecode=8001;teamId=104102030898192;propIds=261:1070,291:30,124:30,348:5,315:3;info=系统奖励"); //建议奖励
		/**/

		/* 
		a.send("205", "<gm/>servicecode=8001;teamId=205102043975061;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("205", "<gm/>servicecode=8001;teamId=205102043975061;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("205", "<gm/>servicecode=8001;teamId=205102043975061;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("205", "<gm/>servicecode=8001;teamId=205102043975061;propIds=261:20,519:160,205:10;info=系统奖励"); //建议奖励		
		a.send("205", "<gm/>servicecode=8001;teamId=205102043975061;propIds=261:35,519:200,205:12;info=系统奖励"); //建议奖励		
		/**/
		
		/* 
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=310:300,311:12,333:1,278:1;info=系统奖励"); //建议奖励	
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("215", "<gm/>servicecode=8001;teamId=215102037050427;propIds=261:70,291:30,124:30;info=系统奖励"); //建议奖励
		/**/
		
		/* 
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=520:100,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=520:160,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=310:300,520:320,333:1;info=系统奖励"); //建议奖励	
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=261:20,520:500,205:8;info=系统奖励"); //建议奖励		
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=261:35,520:800,205:10;info=系统奖励"); //建议奖励		
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=261:50,520:1000,352:1;info=系统奖励"); //建议奖励		
		a.send("227", "<gm/>servicecode=8001;teamId=227102036625075;propIds=261:70,520:1400,124:30;info=系统奖励"); //建议奖励
		/**/

		

		/* 
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=261:20,519:160,205:10;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=261:35,519:200,205:12;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=261:50,519:240,314:15;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=156102030711001;propIds=261:70,519:280,124:40;info=系统奖励"); //建议奖励
		/**/

		/* 
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=261:20,519:160,205:10;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("156", "<gm/>servicecode=8001;teamId=157102034995328;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		/**/

		/* 
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("224", "<gm/>servicecode=8001;teamId=224102037943712;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		/**/

		
		
		/* 
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=519:50,146:2,189:1;info=系统奖励"); //建议奖励		
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=519:80,310:75,188:1;info=系统奖励"); //建议奖励		
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=310:300,519:120,333:1;info=系统奖励"); //建议奖励	
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		a.send("146", "<gm/>servicecode=8001;teamId=146102035165563;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		/**/

		/* */
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=441:80,453:100;info=系统奖励"); //建议奖励		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=442:80,453:300;info=系统奖励"); //建议奖励		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=445:200,436:300;info=系统奖励"); //建议奖励	
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=261:20,291:10,205:8,279:1;info=系统奖励"); //建议奖励		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=261:35,291:15,205:10;info=系统奖励"); //建议奖励		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=261:50,291:20,352:1;info=系统奖励"); //建议奖励		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=261:70,353:1,314:35;info=系统奖励"); //建议奖励
		
		//a.send("134", "<gm/>servicecode=8001;teamId=134102033475205;propIds=435:200,436:200,444:160,445:200;info=系统奖励"); //建议奖励
		/**/

		
		
		

	}

	private void addMoneyFK(long teamId, int addMoneyFK, String msg) {
		if(addMoneyFK<0)
			send(("" + teamId).substring(0, 3), "<gm/>servicecode=89105;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
		else
			send(("" + teamId).substring(0, 3), "<gm/>servicecode=89102;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
	}

	private void addMoneyFKX(long teamId, int addMoneyFK, String msg) {
		//if(addMoneyFK<0)
		send(("" + teamId).substring(0, 3), "<gm/>servicecode=89105;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
		//else
		//	send(("" + teamId).substring(0, 3), "<gm/>servicecode=89102;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
	}

	private void addMoneyJSF(long teamId, int jsf, String msg) {
		int addMoneyFK = 0;
		send(("" + teamId).substring(0, 3), "<gm/>servicecode=89105;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg+";jsf="+jsf);
		//send(("" + teamId).substring(0, 3), "<gm/>servicecode=89102;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
	}


	private void addMoneyFK_minus(long teamId, int addMoneyFK, String msg) {
		send(("" + teamId).substring(0, 3), "<gm/>servicecode=89105;teamId=" + teamId + ";moneyFK=" + addMoneyFK + ";key=" + getKey(teamId, addMoneyFK) + ";info=" + msg);
	}

	private void addMoneyFP(long teamId, int moneyFP, String msg) {
		send((""+teamId).substring(0, 3), "<gm/>servicecode=89105;teamId="+teamId+";moneyFK="+0+";moneyFP="+moneyFP+";key="+getKey(teamId, 0)+";info="+msg);
	}

	private void addMoneyRP(long teamId, int moneyRP, String msg) {
		send((""+teamId).substring(0, 3), "<gm/>servicecode=89105;teamId="+teamId+";moneyFK="+0+";moneyRP="+moneyRP+";key="+getKey(teamId, 0)+";info="+msg);
	}

	private void addBdMoneyFK(long teamId, int dbfk, String msg) {
		send((""+teamId).substring(0, 3), "<gm/>servicecode=89105;teamId="+teamId+";moneyFK="+0+";bdMoneyFK="+dbfk+";key="+getKey(teamId, 0)+";info="+msg);
	}

	private void send(String shardId,String send_str){
		int post = 8038;
		//if(shardId.equals("100")||shardId.equals("120"))
		//	post = 443;
		IoSession s  =Conn.getSession(map.get(shardId),post);
		if(map.containsKey(shardId) && !send_str.equals("")){
			s.write(send_str);
			System.out.println(shardId+"=="+send_str);
		}
		try{
			Thread.sleep(1000);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			s.closeNow();
		}
	}

	private String getKey(long teamId, int moneyFK) {
		//String KEY = "UlgSus9tAsCv4mxJpgOvIxktuCCBwnDcjl8txGNuoYibCOCU";
		String KEY = "UlgSus9tAsCv4mxJpgOvIxktuCCBwnDcjl8txGNuoLiaKoOl";
		return MD5Util.encodeMD5(teamId+""+KEY+""+moneyFK).toLowerCase();
	}

	public void server(){
		map.put("100","118.89.63.129"); 	 

		map.put("102","123.207.249.111");  
		map.put("104","123.207.249.111");  

		map.put("101","118.89.63.129"); 
		map.put("103","118.89.63.129"); 
		map.put("105","118.89.63.129");  
		map.put("106","118.89.63.129"); 

		map.put("107","123.207.100.51");  
		map.put("108","123.207.100.51");  
		map.put("109","123.207.100.51");  
		map.put("110","123.207.100.51");  
		map.put("111","123.207.100.51"); 
		map.put("112","123.207.100.51"); 
		map.put("113","123.207.100.51"); 
		map.put("114","123.207.100.51"); 
		map.put("115","123.207.100.51"); 

		map.put("116","118.89.64.198"); 
		map.put("117","118.89.64.198"); 
		map.put("118","118.89.64.198"); 
		map.put("119","118.89.64.198"); 
		map.put("120","118.89.64.198"); 
		map.put("121","118.89.64.198"); 
		map.put("122","118.89.64.198"); 
		map.put("123","118.89.64.198"); 

		map.put("124","118.89.64.119"); 
		map.put("125","118.89.64.119"); 
		map.put("126","118.89.64.119"); 
		map.put("127","118.89.64.119"); 
		map.put("128","118.89.64.119"); 
		map.put("129","118.89.64.119");
		map.put("130","118.89.64.119"); 
		map.put("131","118.89.64.119"); 
		map.put("132","118.89.64.119"); 
		map.put("133","118.89.64.119"); 
		map.put("134","118.89.64.119"); 
		map.put("135","118.89.64.119"); 

		map.put("136","139.199.68.107"); 
		map.put("137","139.199.68.107"); 
		map.put("138","139.199.68.107"); 
		map.put("139","139.199.68.107"); 
		map.put("140","139.199.68.107"); 
		map.put("141","139.199.68.107"); 

		map.put("142","123.207.118.183"); 
		map.put("143","123.207.118.183"); 
		map.put("144","123.207.118.183"); 
		map.put("145","123.207.118.183"); 
		map.put("146","123.207.118.183"); 


		map.put("147","118.89.24.232"); 
		map.put("148","118.89.24.232"); 
		map.put("149","118.89.24.232"); 
		map.put("150","118.89.24.232"); 
		map.put("151","118.89.24.232"); 
		map.put("152","118.89.24.232"); 
		map.put("153","118.89.24.232"); 
		map.put("154","118.89.24.232"); 


		map.put("155","118.89.47.21");  
		map.put("156","118.89.47.21");  
		map.put("157","118.89.47.21");  
		map.put("158","118.89.47.21");  
		map.put("159","118.89.47.21");  
		map.put("160","118.89.47.21");  
		map.put("161","118.89.47.21"); 
		map.put("164","118.89.47.21");  
		map.put("165","118.89.47.21");  
		map.put("162","118.89.47.21");  
		map.put("163","118.89.47.21");  	
		map.put("166","118.89.47.21");  
		map.put("167","118.89.47.21");  
		map.put("168","118.89.47.21");  
		map.put("169","118.89.47.21");  
		map.put("170","118.89.47.21");  
		map.put("171","118.89.47.21");   
		map.put("172","118.89.47.21");  

		map.put("173","211.159.188.244");  
		map.put("174","211.159.188.244");  
		map.put("175","211.159.188.244");  
		map.put("176","211.159.188.244");  
		map.put("177","211.159.188.244");  
		map.put("178","211.159.188.244");  
		map.put("179","211.159.188.244");  
		map.put("180","211.159.188.244");  
		map.put("181","211.159.188.244");  
		map.put("182","211.159.188.244");  
		map.put("183","211.159.188.244");  
		map.put("184","211.159.188.244");  
		map.put("185","211.159.188.244"); 


		map.put("186","123.207.88.55");  
		map.put("187","123.207.88.55");  
		map.put("188","123.207.88.55");  
		map.put("189","123.207.88.55");  
		map.put("190","123.207.88.55");  
		map.put("191","123.207.88.55"); 
		map.put("192","123.207.88.55"); 
		map.put("193","123.207.88.55"); 
		map.put("194","123.207.88.55"); 
		map.put("195","123.207.88.55"); 
		map.put("196","123.207.88.55"); 
		map.put("197","123.207.88.55"); 
		map.put("198","123.207.88.55");

		map.put("199","118.89.41.143"); 
		map.put("200","118.89.41.143"); 
		map.put("201","118.89.41.143"); 
		map.put("202","118.89.41.143"); 
		map.put("203","118.89.41.143"); 
		map.put("204","118.89.41.143"); 
		map.put("205","118.89.41.143"); 
		map.put("206","118.89.41.143"); 
		map.put("207","118.89.41.143"); 
		map.put("208","118.89.41.143"); 
		map.put("209","118.89.41.143"); 
		map.put("210","118.89.41.143"); 
		map.put("211","118.89.41.143");
		map.put("212","118.89.41.143"); 
		map.put("213","118.89.41.143"); 
		map.put("214","118.89.41.143"); 
		map.put("215","118.89.41.143");		 
		map.put("216","118.89.41.143");
		map.put("217","118.89.41.143");		 
		map.put("218","118.89.41.143");
		map.put("219","118.89.41.143");
		map.put("220","118.89.41.143");		 
		map.put("221","118.89.41.143");

		map.put("222","139.199.60.20");
		map.put("223","139.199.60.20");
		map.put("224","139.199.60.20");
		map.put("225","139.199.60.20");
		map.put("226","139.199.60.20"); 
		map.put("227","139.199.60.20");		
		map.put("228","139.199.60.20");				
		map.put("229","139.199.60.20");		
		map.put("230","139.199.60.20");
		
		map.put("231","139.199.83.196");		
		map.put("232","139.199.83.196");		
		map.put("233","139.199.83.196");
		map.put("234","139.199.83.196");		
		map.put("235","139.199.83.196");		
		map.put("236","139.199.83.196");
		
		map.put("237","118.89.22.183");		
		map.put("238","118.89.22.183");
		
		map.put("239","123.207.226.198");	
		map.put("240","123.207.245.62");
		
		map.put("241","139.199.11.94"); 
		
		map.put("test_177", "192.168.10.123");
		map.put("test_191", "192.168.10.191");
		map.put("test_183","192.168.10.183");
		map.put("test_201","192.168.10.124");
		map.put("test_tw", "192.168.10.178");
	}

	private List<String> initData(){
		List<String> d = new ArrayList<String>();			
		return d;
	}

	private void init(){	

	}
}
