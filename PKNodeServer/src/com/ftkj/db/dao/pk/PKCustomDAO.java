package com.ftkj.db.dao.pk;

import com.ftkj.db.conn.dao.GameConnectionDAO;
import com.ftkj.db.conn.dao.ResultSetRow;
import com.ftkj.db.conn.dao.RowHandler;
import com.ftkj.enums.EActionType;
import com.ftkj.enums.EBattleRoomStatus;
import com.ftkj.enums.ECustomGuessType;
import com.ftkj.enums.ECustomPVPType;
import com.ftkj.enums.EPlayerPosition;
import com.ftkj.enums.battle.EBattleStep;
import com.ftkj.manager.custom.CustomPVPRoom;
import com.ftkj.manager.team.TeamNodeInfo;
import com.ftkj.util.JsonUtil;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import org.joda.time.DateTime;

import java.util.List;
import java.util.stream.Collectors;

public class PKCustomDAO extends GameConnectionDAO {

    private RowHandler<CustomPVPRoom> CUSTOMPVPROOM = new RowHandler<CustomPVPRoom>() {

        @Override
        public CustomPVPRoom handleRow(ResultSetRow row) throws Exception {
            CustomPVPRoom room = new CustomPVPRoom(row.getInt("room_id"));
            room.setAutoStart(false);
            room.setAwayMoney(row.getInt("away_money"));
            String awayTeam = row.getString("away_team");
            room.setAwayTeam("".equals(awayTeam) ? null : JsonUtil.toObj(awayTeam, TeamNodeInfo.class));
            room.setGuessType(ECustomGuessType.values()[row.getInt("guess_type")]);
            room.setHomeMoney(row.getInt("home_money"));
            String homeTeam = row.getString("home_team");
            room.setHomeTeam("".equals(homeTeam) ? null : JsonUtil.toObj(homeTeam, TeamNodeInfo.class));
            room.setLevelCondition(row.getInt("level_condition"));
            room.setNodes(Sets.newHashSet(row.getString("nodes").split("[,]")));
            room.setPkType(ECustomPVPType.values()[row.getInt("pk_type")]);
            room.setPositionCondition(EPlayerPosition.getEPlayerPosition(row.getInt("position_condition")));
            room.setPowerCondition(row.getInt("power_condition"));
            room.setRoomMoney(row.getInt("room_money"));
            room.setRoomScore(row.getInt("room_score"));
            room.setRoomStatus(EBattleRoomStatus.values()[row.getInt("room_status")]);
            room.setRoomTip(row.getString("room_tip"));
            room.setStepConditions(Sets.newHashSet(row.getString("step_condition").split("[,]")).stream()
                    .map(step -> EBattleStep.values()[Ints.tryParse(step)]).collect(Collectors.toSet()));
            room.setWinCondition(EActionType.convertByType(row.getInt("win_condition")));
            room.setStartTime(new DateTime(row.getTimestamp("start_time")));
            return room;
        }
    };

    public List<CustomPVPRoom> getCustomPVPRoomList() {
        String sql = "select * from t_u_custom_room";
        return queryForList(sql, CUSTOMPVPROOM);
    }

}
