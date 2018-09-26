package com.ftkj.test;

import com.ftkj.db.conn.dao.AsynchronousBatchDB;
import com.ftkj.db.conn.dao.BatchDataHelper;
import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResourceType;
import com.ftkj.db.dao.logic.LeagueDAO;
import com.ftkj.db.domain.LeaguePO;
import com.ftkj.server.instance.InstanceFactory;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;

public class GameTest {


    public static void main(String[] args){
        testBatch();
    }

    private static void testBatch(){

        String jsPath = "E:/work/NBA/server/NBAGameServer/config/config.dev.js";
        InstanceFactory.get().runInitScript(jsPath);


        DBManager dbManager = new DBManager();
        LeaguePO leaguePO = new LeaguePO();
        leaguePO.setLeagueId(2);
        DateTime dateTime = new DateTime();
        leaguePO.setLeagueName("start\n );{}\\-@\t11\\ \t'aaaaa\n aaa \"\t\" bbb \n\\\"end");
        //leaguePO.setLeagueName("ss start\n );\r{}\\-@\t11\\ \t'aaa\"aa\nend");
        //String logo;
        leaguePO.setLeagueTip("000");
        //leaguePO.setLeagueNotice("aabbb ;; \\\'  \\\" ........ SELECT   \\\t  end");
        leaguePO.setCreateTime(dateTime);

        LeaguePO leaguePO1 = new LeaguePO();
        leaguePO1.setLeagueId(3);
        DateTime dateTime1 = new DateTime();
        leaguePO1.setLeagueName("aaat\n );{}\\-@\t11\\ \t'aa\"\t\" bbb \n\\\"endaaa");
        leaguePO1.setLeagueTip("000");
        leaguePO1.setCreateTime(dateTime1);

        DBManager.putAsynchronousGameDB(leaguePO);

        BatchDataHelper.init();

        List<AsynchronousBatchDB> list = Lists.newArrayList(leaguePO,leaguePO1);
        GameConnectionDAO game = new GameConnectionDAO();
        game.database = InstanceFactory.get().getDataBaseByKey(ResourceType.DB_game.getResName());

        game.batchUpdate(list,leaguePO.getTableName(),leaguePO.getRowNames(),true,true);

        System.out.println();



        //statement = conn.prepareStatement(StringUtil.formatString("LOAD DATA LOCAL INFILE '{}.csv' REPLACE INTO TABLE {} " +
        //   "FIELDS TERMINATED BY '\t' ENCLOSED BY ''  ESCAPED BY '\\'  LINES TERMINATED BY '\n' STARTING BY '' ({})", tableName, tableName, rowNames));

        //DBManager.run(false);

        LeagueDAO leagueDAO = new LeagueDAO();
        List<LeaguePO> leagueDAOS = leagueDAO.getAllLeague();
        System.out.println();
    }



}
