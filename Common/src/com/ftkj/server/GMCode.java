package com.ftkj.server;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import com.ftkj.enums.TeamDayNumType;

public class GMCode {

    public static final int GMManager_loginManager = 100000001;
    public static final int GMManager_reloadNBAData = 110001001;
    public static final int GMManager_reloadNBAPKData = 110001002;
    public static final int GMManager_tipAllGame = 110001003;
    public static final int GMManager_sendEMail = 110001004;
    public static final int GMManager_lockTeam = 110001005;
    public static final int GMManager_chatController = 110001006;
    public static final int GMManager_resetMatch = 110001007;
    /**发送竞猜活动奖励邮件*/
    public static final int GMManager_sendGameGuessEMail = 110001008;
    /**运行后台新增了竞猜比赛的数据,通知游戏服加载新的竞猜比赛数据*/
    public static final int GMManager_updateGameGuessData = 110001009;
    /** 运行后台,客服回复了玩家提问调用*/
    public static final int GMManager_customerResp = 110001010;
    // 活动相关
    public static final int GMManager_syncAllSystemActive = 200000001;
    public static final int GMManager_updateActiveTimeBatch = 200000002;
    public static final int GMManager_queryActiveData = 200000003;
    public static final int GMManager_clearActiveData = 200000004;
    public static final int GMManager_clearTeamActiveData = 200000005;
    public static final int GMManager_pushTeamActiveData = 200000006;
    public static final int GMManager_createActiveDataTable = 200000007;
    public static final int GMManager_updateActiveConfigBatch = 200000008;
    //
    public static final int GMManager_recharge = 710001001;

    public static final int GMManager_closeServer = 810001111;
    public static final int GMManager_resetServerData = 810001112;

    public static enum GmCommand {

        test("test", "测试内部方法，参数【test】"),
        add_player("add_player", "添加球员,参数【球员基础ID】"),
        del_La_All("del_la_all", "清理所有联盟球馆赛数据，参数【del_la_all】"),
        reLea_ScoreRanks("reLea_ScoreRanks", "刷新周贡献排行榜，参数【reLea_ScoreRanks 】"),
        Train_trainUpLevel("trainUpLevel", "测试内部方法，参数【trainUpLevel 训练馆唯一id, 增加的经验值】"),
        Create_Task("create_task", "测试内部方法，参数【create_task 基础id】"),
        Finsh_Task("finsh_task", "测试内部方法，参数【finsh_task 基础id】"),
        Reload_NbaData("reload_nbadata", "重置球员数据，参数【reload_nbadata】"),
        Team_Level("team_level", "修改球队等级 num:int 最终等级"),
        Team_Lev_Up("lev_up", "球队升级 num:int 要增加的等级"),
        Team_Currency("currency", "修改球队货币 type:str num:int 货币类型 货币数量(正加负减)"),
        Team_Reset_Daily("t_reset_daily", "重置球队每日状态"),
        Team_Num_Set("t_num_reset", "int int 设置球队已使用次数(Team.xlsx->num)  次数类型 当前次数"),
        Team_Day_Num_Set("t_day_num", "int int 设置球队每日次数 次数类型 当前次数"),

        Pay("pay", "充值回调  payType:充值类型 num:int 充值金额，参数【pay 0 10】"),
        Prop_Add("prop_add", "添加物品 pid:int num:int 物品ID 物品数量(正加,只提供添加物品)， 参数【prop_add 1148 10】"),
        Props_Add("props", "添加物品 pid:int num:int+ 道具id 物品数量+"),

        Match_Force_End_All("match_end_all", "比赛.强制结束当前所有比赛 homeWin:int 主场是否强制胜利(0正常比分,1是)"),
        Match_Force_End("match_end", "比赛.强制结束比赛 id:int homeWin:int 比赛id 主场是否强制胜利(0正常比分,1是)"),

        MainMatch_Reset_Sys_Rank("mmatch_sys_rank_clear", "主线赛程.清空所有关卡的排行榜"),
        MainMatch_Reset_Team_CS_Target("mmatch_t_target_reset", "主线赛程.重置球队当前锦标赛对手"),
        MainMatch_Enable_Lev("mmatch_enable_lev", "主线赛程.开启关卡(开启时会连带开启赛区)  id:int star:int 关卡配置id 默认开启星级(可选)"),
        MainMatch_Enable_Lev_Full_Star("mmatch_enable_lev_max_star", "主线赛程.开启关卡到最大星级(开启时会连带开启赛区)  id:int+ 关卡配置id列表"),
        MainMatch_Lev_Star("mmatch_lev_star", "主线赛程.设置关卡星级 id:int star:int 关卡配置id 星级"),
        MainMatch_Lev_Match_Num("mmatch_lev_mc", "主线赛程.设置关卡总比赛次数 id:int num:int 关卡配置id 已经比赛次数"),
        MainMatch_Reset_Div_Awards("mmatch_div_reset_awards", "主线赛程.重置赛区奖励领取状态 id:int 赛区配置id"),

        RMatch_Refresh_Rank("rmatch_up_rank", "天梯赛.刷新段位排行榜"),
        RMatch_Join_Pool("rmatch_join", "天梯赛.加入比赛 tid:long 球队id(为0或空则加自己)"),
        RMatch_Rating("rmatch_rating", "天梯赛.修改评分(段位自动跟随评分变化) num:int 评分变化(正加负减)"),
        RMatch_Season_Award("rmatch_season_award", "天梯赛.强制发放赛季奖励"),

        AllStar_Restart("allstar_restart", "全明星. 设置难度(难度变化后血量变满) id:int lev:int 配置id 难度"),
        AllStar_Hp("allstar_hp", "全明星. 设置血量 id:int hp:int 配置id 当前血量(>0)"),
        AllStar_Award("allstar_award", "全明星. 发放排名奖励 id:int 配置id"),
        AllStar_Team_Hp("allstar_t_hp", "全明星. 修改球队伤害 id:int hp:int 配置id 伤害(正加负减)"),

        Arena_Reset_Cd("arena_reset_cd", "竞技场. 重置比赛cd"),
        Arena_Award("arena_award", "竞技场. 发放每日排名奖励"),

        Activity_Set("activity", "运营活动. 设置"),

        Rank_Team("rank_team", "排行榜. 更新本球队战力排行榜战力"),
        
        Draft_Join("draft_join", "int roomLevel, long teamId 加入选秀，第一个参数是房间类型，第二个参数是球队id."),
        
        Clear_Card("clear_card", "int type, int playerId 清空球星卡收集数据"),
        
        Add_Player_Low("add_player_low", "int playerId 添加底薪到仓库."),
        
        add_cks_npc("add_cks_npc", ""),
        honor("honor", ""),
        qtl("qtl", ""),

        //
        ;
        /** gm 命令 */
        private final String command;
        /** 是否需要球队在线 */
        private final boolean needTeamLogin;
        /** gm命令说明 */
        private final String tip;

        /**
         * gm命令
         *
         * @param command gm命令
         * @param tip     gm命令说明
         */
        GmCommand(String command, String tip) {
            this(command, true, tip);
        }

        /**
         * gm命令
         *
         * @param command       gm命令
         * @param needTeamLogin 是否需要球队在线
         * @param tip           gm命令说明
         */
        GmCommand(String command, boolean needTeamLogin, String tip) {
            this.command = command;
            this.needTeamLogin = needTeamLogin;
            this.tip = tip;
        }

        public String getTip() {
            return tip;
        }

        public boolean isNeedTeamLogin() {
            return needTeamLogin;
        }

        public String getCommand() {
            return command;
        }

        private static final Map<String, GmCommand> cache = new HashMap<>();

        static {
            for (GmCommand e : values()) {
                String cmd = e.getCommand().toLowerCase();
                if (cache.containsKey(cmd)) {
                    throw new Error("gm 命令 " + cmd + " 已经存在");
                }
                cache.put(cmd, e);
            }
        }

        public static GmCommand convertTo(String command) {
            command = command.toLowerCase();
            return cache.get(command);
        }

    }

    //    public static void main(String[] args) {
    //        StringBuilder str = new StringBuilder();
    //        for (GmCommand e : GmCommand.values()) {
    //            str.append(e.command).append(" ").append(e.tip).append("\n");
    //        }
    //        System.out.println(str);
    //    }

    /**
     * 输出格式化的gm命令, 便于查看
     *
     * @author luch
     */
    public static final class GmCommandPrinter {

        private EnumMap<GmCommand, BiConsumer<GmCommand, StringBuilder>> generater = new EnumMap<>(GmCommand.class);

        private void init() {
            generater.put(GmCommand.Team_Currency, (gmc, sb) -> sb.append("\t货币类型:\n" +
                    "\t\tmoney 球卷\n\t\tbdmoney 绑定球券\n\t\tgold 经费"));
            generater.put(GmCommand.Team_Reset_Daily, (gmc, sb) -> sb.append("\t重置如下功能:\n" +
                    "\t\t天梯赛每日奖励领取状态"));
            generater.put(GmCommand.Team_Day_Num_Set, (gmc, sb) -> {
                sb.append("\t次数类型:\n");
                sb.append("\t\t").append(TeamDayNumType.Arena_Match_Free_Num.getType()).append("  竞技场. 每日已使用免费比赛次数");
            });
            String matchForceEndComment = "\t主场强制胜利时如果输的话比分改为比客场大1分. 强制胜利时主线赛程星数改为3星";
            generater.put(GmCommand.Match_Force_End_All, (gmc, sb) -> sb.append(matchForceEndComment));
            generater.put(GmCommand.Match_Force_End, (gmc, sb) -> sb.append(matchForceEndComment));
        }

        public static void main(String[] args) {
            GmCommandPrinter pgc = new GmCommandPrinter();
            pgc.init();
            System.out.println(pgc.gmCommandToString());
        }

        private String gmCommandToString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("gm命令列表. 格式:\n");
            sb.append("gm命令 说明 参数列表(名称:类型) 参数列表(名称)说明\n\n");
            for (GmCommand gmc : GmCommand.values()) {
                sb.append(gmc.getCommand()).append(" ").append(gmc.getTip()).append("\n");
                BiConsumer<GmCommand, StringBuilder> func = generater.get(gmc);
                if (func != null) {
                    func.accept(gmc, sb);
                    sb.append("\n");
                }
            }

            sb.append("\nPrinter by : ").append(getClass().getCanonicalName()).append("\n");
            return sb.toString();
        }
    }

}
