package com.ftkj.db.dao.common;

import java.util.List;

import org.joda.time.DateTime;

import com.ftkj.db.conn.dao.DBManager;
import com.ftkj.db.conn.dao.DataConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.db.domain.active.base.ActiveBasePO;
import com.ftkj.db.domain.active.base.SystemActivePO;
import com.ftkj.server.GameSource;

public class ActiveDAO extends DataConnectionDAO {

    public ActiveDAO() {
        runDel();
    }

    public static void runDel() {
        DBManager.putDataDelSql("delete from system_active where status = 2");
    }

    /**
     * 清空活动相关数据
     *
     * @param atvIds
     */
    public static void runClearActiveData(int atvId) {
        DBManager.putDataDelSql("delete from " + ActiveBasePO.getTableNameDB() + " where atv_id=" + atvId);
        DBManager.putDataDelSql("delete from t_u_active_data_share where atv_id=" + atvId);
    }
    
    public void deleteLimitChallenge(int atvId) {
      String sql = "delete from t_u_active_data_share where atv_id=" + atvId;
      execute(sql);
    }
    
    public void addLimitChallenge(ActiveBasePO po) {
      String sql = "INSERT INTO t_u_active_data_share VALUES (?, ?, ?,?,?, now(),now(),now(), ?, ?, ?,?,?,?,?, ?)";
      execute(sql,po.getAtvId(),po.getShardId(),po.getTeamId(),po.getTeamName(),po.getType(),po.getiData1(),po.getiData2(),po.getiData3(),po.getsData1(),po.getsData2(),po.getsData3(),po.getsData4(),po.getsData5());
    }
    
    public void updateLimitChallenge(ActiveBasePO po) {
      String sql = "UPDATE `t_u_active_data_share` SET i_data1=?,i_data2=?,i_data3=?,s_data1=?,s_data2=?,s_data3=?,s_data4=?,s_data5=?,update_time=now() where atv_id=? and team_id=?;";
      execute(sql,po.getiData1(),po.getiData2(),po.getiData3(),po.getsData1(),po.getsData2(),po.getsData3(),po.getsData4(),po.getsData5(),po.getAtvId(),po.getTeamId());
    }

    private RowHandler<SystemActivePO> SYSTEMACTIVEPO = new RowHandler<SystemActivePO>() {

        @Override
        public SystemActivePO handleRow(ResultSetRow row) throws Exception {
            SystemActivePO po = new SystemActivePO();
            po.setShardId(row.getInt("shard_id"));
            po.setAtvId(row.getInt("atv_id"));
            po.setName(row.getString("name"));
            po.setStartTime(new DateTime(row.getTimestamp("start_time")));
            po.setEndTime(new DateTime(row.getTimestamp("end_time")));
            po.setStatus(row.getInt("status"));
            po.setJsonConfig(row.getString("jsonConfig"));

            return po;
        }
    };
    
    private RowHandler<ActiveBasePO> ACTIVEBASEPO0 = new RowHandler<ActiveBasePO>() {

      @Override
      public ActiveBasePO handleRow(ResultSetRow row) throws Exception {

          ActiveBasePO po = new ActiveBasePO();
          po.setAtvId(row.getInt("atv_id"));
          po.setShardId(row.getInt("shard_id"));
          po.setTeamId(row.getLong("team_id"));
          po.setTeamName(row.getString("team_name"));
          po.setType(row.getInt("type"));
          po.setLastTime(new DateTime(row.getTimestamp("last_time")));
          po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
          po.setUpdateTime(new DateTime(row.getTimestamp("update_time")));
          po.setiData1(row.getInt("i_data1"));
          po.setiData2(row.getInt("i_data2"));
          po.setiData3(row.getInt("i_data3"));
          po.setsData1(row.getString("s_data1"));
          po.setsData2(row.getString("s_data2"));
          po.setsData3(row.getString("s_data3"));
          po.setsData4(row.getString("s_data4"));
          po.setsData5(row.getString("s_data5"));

          return po;
      }
  };

    private RowHandler<ActiveBasePO> ACTIVEBASEPO = new RowHandler<ActiveBasePO>() {

        @Override
        public ActiveBasePO handleRow(ResultSetRow row) throws Exception {

            ActiveBasePO po = new ActiveBasePO();
            po.setAtvId(row.getInt("atv_id"));
            po.setShardId(row.getInt("shard_id"));
            po.setTeamId(row.getLong("team_id"));
            po.setTeamName(row.getString("team_name"));
            po.setType(row.getInt("type"));
            po.setCreateDay(row.getString("create_day"));
            po.setLastTime(new DateTime(row.getTimestamp("last_time")));
            po.setCreateTime(new DateTime(row.getTimestamp("create_time")));
            po.setUpdateTime(new DateTime(row.getTimestamp("update_time")));
            po.setiData1(row.getInt("i_data1"));
            po.setiData2(row.getInt("i_data2"));
            po.setiData3(row.getInt("i_data3"));
            po.setiData4(row.getInt("i_data4"));
            po.setiData5(row.getInt("i_data5"));
            po.setsData1(row.getString("s_data1"));
            po.setsData2(row.getString("s_data2"));
            po.setsData3(row.getString("s_data3"));
            po.setsData4(row.getString("s_data4"));
            po.setsData5(row.getString("s_data5"));
            po.setsPropNum(row.getString("prop_num"));
            po.setFinishStatus(row.getString("finish_status"));
            po.setAwardStatus(row.getString("award_status"));

            return po;
        }
    };

    public List<SystemActivePO> getSystemActiveList(int shardId) {
        String sql = "select * from system_active where shard_id=?";
        return queryForList(sql, SYSTEMACTIVEPO, shardId);
    }

    public ActiveBasePO getActiveShareData(int shardId, int atvId) {
        String sql = "select * from t_u_active_data_share where shard_id=? and atv_id = ?";
        return queryForObject(sql, ACTIVEBASEPO, shardId, atvId);
    }
    
    public ActiveBasePO getLimitChanllenge(long teamId,int atv_id) {
      String sql = "select * from t_u_active_data_share where team_id=? and atv_id = ?";
      return queryForObject(sql, ACTIVEBASEPO0, teamId, atv_id);
    }
    
//    public List<ActiveBasePO> queryLimitChanllenge(int title,int startIndex,int limit,int atv_id) {
//      //得分 i_data1
//      //篮板 i_data2
//      //助攻 i_data3
//      //抢断 s_data1
//      //盖帽 s_data2
//      List<ActiveBasePO> list = new ArrayList<>();
//      String condition = null;
//      if(title==0) {//得分+篮板
//        condition = "i_data1+i_data2";
//      }else if(title==1) {//得分+助攻
//        condition = "i_data1+i_data3";
//      }else if(title==2) {//得分+篮板+助攻
//        condition = "i_data1+i_data2+i_data3";
//      }else if(title==3) {//得分+篮板+助攻+抢断+盖帽
//        condition = "i_data1+i_data2+i_data3+s_data1+s_data2";
//      }else if(title==4) {//得分
//        condition = "i_data1";
//      }else if(title==5) {//助攻
//        condition = "i_data3";
//      }else if(title==6) {//篮板
//        condition = "i_data2";
//      }
//      if(condition == null) {
//        return list;
//      }
//      StringBuffer sql = new StringBuffer();
//      sql.append("select a.*,").append(condition).append(" b ").append("from t_u_active_data_share a where atv_id=").append(atv_id)
//      .append(" order by b desc,update_time asc");
//      if(limit > 0) {
//        sql.append(" limit ").append(startIndex).append(",").append(limit);
//      }
//      return queryForList(sql.toString(), ACTIVEBASEPO0);
//  }
    
    public List<ActiveBasePO> queryLimitChanllenge(int title,int startIndex,int limit,int atv_id) {
      //得分 i_data1
      //篮板 i_data2
      //助攻 i_data3
      //抢断 s_data1
      //盖帽 s_data2
      String condition = "i_data1+i_data2+i_data3+s_data1+s_data2";
      StringBuffer sql = new StringBuffer();
      sql.append("select a.*,").append(condition).append(" b ").append("from t_u_active_data_share a where atv_id=").append(atv_id)
      .append(" order by b desc,update_time asc");
      if(limit > 0) {
        sql.append(" limit ").append(startIndex).append(",").append(limit);
      }
      return queryForList(sql.toString(), ACTIVEBASEPO0);
  }

    public ActiveBasePO getActiveBase(int shardId, int atvId, long teamId) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and team_id = ? and atv_id = ?";
        return queryForObject(sql, ACTIVEBASEPO, shardId, teamId, atvId);
    }

    public ActiveBasePO getActiveBase(int shardId, long teamId, int atvId, DateTime createDate) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and team_id = ? and atv_id = ? and date(create_time)=date(?)";
        return queryForObject(sql, ACTIVEBASEPO, shardId, teamId, atvId, createDate);
    }

    /**
     * 某时间段内的所有记录，15天前的记录
     *
     * @param shardId
     * @param teamId
     * @param atvId
     * @param createDate
     * @return
     */
    public List<ActiveBasePO> getActiveBaseListBeforeDay(int shardId, int atvId, long teamId, int beforeDay) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and team_id = ? and atv_id = ? and date(create_time) >= DATE_SUB(CURDATE(), INTERVAL ? DAY) ";
        return queryForList(sql, ACTIVEBASEPO, shardId, teamId, atvId, beforeDay);
    }

    public List<ActiveBasePO> queryActiveDataOrderByLastTime(int shardId, int atvId, int size) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and atv_id = ? and last_time is not null order by last_time asc limit ?";
        return queryForList(sql, ACTIVEBASEPO, shardId, atvId, size);
    }

    /**
     * 活动排名
     *
     * @param atvId
     * @param size
     * @return
     */
    public List<ActiveBasePO> queryActiveDataOrderByRank(int shardId, int atvId, int size) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and atv_id = ? and last_time is not null order by i_data2 desc,last_time asc limit ?";
        return queryForList(sql, ACTIVEBASEPO, shardId, atvId, size);
    }

    public List<ActiveBasePO> queryActiveDataByValue(int shardId, int atvId, int value) {
        String sql = "select * from " + ActiveBasePO.getTableNameDB() + " where shard_id=? and atv_id = ? and i_data1 = ? ";
        return queryForList(sql, ACTIVEBASEPO, shardId, atvId, value);
    }

    /**
     * 如果该分区表不存在，则创建，拷贝基础表t_u_active_datacopynew
     *
     * @return
     */
    public boolean createActiveDataTable() {
        return execute("CREATE TABLE IF NOT EXISTS t_u_active_data_" + GameSource.shardId + " LIKE t_u_active_data_copynew");
    }

    /**
     * 清空活动相关数据
     *
     * @param atvIds
     */
    public void clearActiveData(int atvId) {
        execute("delete from " + ActiveBasePO.getTableNameDB() + " where atv_id=?", atvId);
        execute("delete from t_u_active_data_share where atv_id=?", atvId);
    }

    /**
     * 清空活动相关数据
     *
     * @param atvIds
     */
    public void clearActiveData(long teamId, int atvId) {
        execute("delete from " + ActiveBasePO.getTableNameDB() + " where team_id=? and atv_id=?", teamId, atvId);
    }

}
