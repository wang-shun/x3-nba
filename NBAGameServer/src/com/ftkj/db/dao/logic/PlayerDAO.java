package com.ftkj.db.dao.logic;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.PlayerAvgInfo;
import com.ftkj.db.domain.PlayerPO;
import com.ftkj.manager.player.PlayerGrade;
import com.ftkj.manager.player.PlayerMinPrice;
import com.ftkj.manager.player.PlayerTalent;

/**
 * @author tim.huang
 * 2017年3月8日
 */
public class PlayerDAO extends GameConnectionDAO {

    public PlayerDAO() {
        DBManager.putGameDelSql("delete from t_u_player where player_id < 0 or storage = 3");
        DBManager.putGameDelSql("delete from t_u_player_source where player_id < 0 ");
    }

    private RowHandler<PlayerPO> PLAYERPO = new RowHandler<PlayerPO>() {

        @Override
        public PlayerPO handleRow(ResultSetRow row) throws Exception {
            PlayerPO po = new PlayerPO();
            po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
            po.setId(row.getInt("pid"));
            po.setLineupPosition(row.getString("lineup_position"));
            po.setPlayerRid(row.getInt("player_id"));
            po.setPosition(row.getString("position"));
            po.setPrice(row.getInt("price"));
            po.setStorage(row.getInt("storage"));
            po.setTalentId(row.getInt("tid"));
            po.setBind(row.getBoolean("bind"));
            po.setTeamId(row.getLong("team_id"));
            return po;
        }
    };
    
    private RowHandler<PlayerMinPrice> PLAYERMINPRICE = new RowHandler<PlayerMinPrice>() {

        @Override
        public PlayerMinPrice handleRow(ResultSetRow row) throws Exception {
            PlayerMinPrice po = new PlayerMinPrice();            
            po.setPlayerId(row.getInt("player_id"));           
            po.setMinPrice(row.getInt("min(price)"));
           
            return po;
        }
    };

    private RowHandler<PlayerGrade> PLAYERGRADE = new RowHandler<PlayerGrade>() {

        @Override
        public PlayerGrade handleRow(ResultSetRow row) throws Exception {
            PlayerGrade playerGrade = new PlayerGrade();
            playerGrade.setExp(row.getInt("exp"));
            playerGrade.setGrade(row.getInt("grade"));
            playerGrade.setPlayerId(row.getInt("player_id"));
            playerGrade.setTeamId(row.getLong("team_id"));
            playerGrade.setStar(row.getInt("star"));
            playerGrade.setStarGrade(row.getInt("star_grade"));
            return playerGrade;
        }
    };

    private RowHandler<PlayerAvgInfo> PLAYERAVGPO = new RowHandler<PlayerAvgInfo>() {

        @Override
        public PlayerAvgInfo handleRow(ResultSetRow row) throws Exception {
            PlayerAvgInfo po = new PlayerAvgInfo();
            po.setAst(row.getInt("ast"));
            po.setBlk(row.getInt("blk"));
            po.setFga(row.getInt("fga"));
            po.setFgm(row.getInt("fgm"));
            po.setFta(row.getInt("fta"));
            po.setFtm(row.getInt("ftm"));
            po.setPa3(row.getInt("pa3"));
            po.setPf(row.getInt("pf"));
            po.setPlayerId(row.getInt("player_id"));
            po.setPm3(row.getInt("pm3"));
            po.setPts(row.getInt("pts"));
            po.setReb(row.getInt("reb"));
            po.setStl(row.getInt("stl"));
            po.setTeamId(row.getLong("team_id"));
            po.setTo(row.getInt("to"));
            po.setTotal(row.getInt("total"));
            return po;
        }
    };

    private RowHandler<PlayerTalent> PLAYERTALENT = new RowHandler<PlayerTalent>() {

        @Override
        public PlayerTalent handleRow(ResultSetRow row) throws Exception {
            PlayerTalent po = new PlayerTalent();
            po.setDf(row.getInt("df"));
            po.setFqmz(row.getInt("fqmz"));
            po.setGm(row.getInt("gm"));
            po.setId(row.getInt("id"));
            po.setLb(row.getInt("lb"));
            po.setPlayerId(row.getInt("player_id"));
            po.setQd(row.getInt("qd"));
            po.setSfmz(row.getInt("sfmz"));
            po.setTeamId(row.getLong("team_id"));
            po.setTlmz(row.getInt("tlmz"));
            po.setZg(row.getInt("zg"));
            return po;
        }
    };

    public List<PlayerGrade> getPlayerGradeList(long teamId) {
        String sql = "select * from t_u_player_grade where team_id = ?";
        return queryForList(sql, PLAYERGRADE, teamId);
    }

    public List<PlayerPO> getPlayerList(long teamId) {
        String sql = "select * from t_u_player where team_id = ?";
        return queryForList(sql, PLAYERPO, teamId);
    }

    public List<PlayerMinPrice> getPlayerMinPriceList() {
        String sql = "select player_id, min(price) from t_u_player where price > 0 group by player_id";
        return queryForList(sql, PLAYERMINPRICE);
    }

    public List<PlayerAvgInfo> getPlayerAvgList(long teamId) {
        String sql = "select * from t_u_player_source where team_id = ? and player_id > 0";
        return queryForList(sql, PLAYERAVGPO, teamId);
    }

    public List<PlayerTalent> getPlayerTalentList(long teamId) {
        String sql = "select * from t_u_player_talent where team_id = ? and player_id>0";
        return queryForList(sql, PLAYERTALENT, teamId);
    }
}
