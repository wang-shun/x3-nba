package com.ftkj.enums;

import com.ftkj.util.BitUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 比赛行为类型.
 * <p>
 * 参考
 * <a href="https://bbs.hupu.com/1902529.html">篮球术语</a>
 * <a href="http://www.espn.com/nba/player/stats/_/id/3202/kevin-durant">赛季统计信息</a>
 * <a href="http://www.espn.com/nba/boxscore?gameId=400975646">赛程统计信息</a>
 *
 * @author tim.huang
 * @author luch
 * 2017年2月28日
 */
public enum EActionType {
    //====================================================================================
    //球员行为 - 基础行为 1 - 99
    //====================================================================================
    /** 上场时间 min */
    min(1, "min", "上场时间", Attr.Player_Act),
    /** 投篮命中 FGM Field goals made */
    fgm(2, "fgm", "投篮命中", Attr.Player_Act),
    /** 尝试投篮 FGA Field goals attempted. Field Goals Made-Attempted FGM-A. */
    fga(3, "fga", "尝试投篮", Attr.Player_Act, Attr.AI_Tigger),
    /** 投篮命中率 FG% Field Goal Percentage */
    fgp(4, "fg%", "投篮命中率", Attr.Player_Act),
    /** 3分命中 3PM 3-Point Field Goals Made */
    _3pm(5, "3pm", "3分命中次数", Attr.Player_Act),
    /** 3分出手 3PA 3-Point Field Goals Made-Attempted 3PM-A */
    _3pa(6, "3pa", "3分出手次数", Attr.Player_Act),
    /** 3分命中率 3P% 3-Point Field Goal Percentage */
    _3pp(7, "3p%", "3分命中率", Attr.Player_Act),
    /** 罚球命中 FTM Free Throws Made */
    ftm(8, "ftm", "罚球命中", Attr.Player_Act),
    /** 罚球次数 FTM Free Throws Attempted. Free Throws Made-Attempted FTM-A */
    fta(9, "fta", "罚球次数", Attr.Player_Act),
    /** 罚球命中率 FT% Free Throw Percentage */
    ftp(10, "ft%", "罚球命中率", Attr.Player_Act),
    /** 进攻篮板 OR Offensive Rebounds */
    oreb(11, "oreb", "进攻篮板", Attr.Player_Act),
    /** 防守篮板 DR Defensive Rebounds */
    dreb(12, "dreb", "防守篮板", Attr.Player_Act),
    /** 篮板 REB Rebounds */
    reb(13, "reb", "篮板", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 助攻数 AST Assists */
    ast(14, "ast", "助攻数", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 盖帽 BLK Blocks */
    blk(15, "blk", "盖帽", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 抢断 STL Steals */
    stl(16, "stl", "抢断", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 犯规 PF Personal Fouls */
    pf(17, "pf", "犯规", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 失误 TO Turnovers */
    to(18, "to", "失误", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    /** 得分 PTS Points */
    pts(19, "pts", "得分", Attr.Player_Act, Attr.Custom, Attr.AI_Tigger),
    //====================================================================================
    //球员行为 -- 基础扩展行为 100 - 199
    //====================================================================================
    /** 2分命中 */
    _2pm(101, "2pm", "2分命中"),
    /** 2分投不中 */
    _2p_missed(102, "2p_miss", "2分投不中", Attr.Custom),
    /** 3分 3-Point */
    _3p(103, "3p", "3分能力和行为", Attr.Custom, Attr.AI_Tigger),
    /** 罚球行为 FT Free Throw */
    ft_act(104, "ft", "罚球", Attr.Custom, Attr.AI_Tigger),
    /** 补篮 */
    tip_in(105, "tip_in", "补篮", Attr.Custom, Attr.AI_Tigger),
    /** 球员绝杀(至胜入球) Clutch shot */
    clutch_shot(106, "clutch_shot", "球员_绝杀(至胜入球)", Attr.AI_Tigger),

    /** 打铁(投篮不进). brick 球打在篮筐或篮板上被崩出来 */
    brick(107, "brick", "打铁", Attr.Player_Act),
    /** 被犯规 */
    fouled(108, "fouled", "被犯规", Attr.Custom, Attr.AI_Tigger),
    /** 犯满离场 disqualification 被罚下场(缩写：DQ.)、犯满离场、毕业 */
    disqualification(109, "dq", "犯满离场", Attr.AI_Tigger),
    /** 单个球员连续得分 */
    pr_run(110, "pr_run", "球员连续得分", Attr.Player_Act, Attr.AI_Tigger),
    /** 连续失误 */
    continuity_to(111, "continuity_to", "连续失误", Attr.AI_Tigger),
    //====================================================================================
    //球员行为 -- 扩展行为 200 - 299
    //====================================================================================
    /** 体力恢复 */
    update_power(201, "up_power", "体力恢复", Attr.Player_Act, Attr.AI_Tigger),
    /** 体力 */
    power(202, "power", "体力能力"),
    /** 体力系数 powerRate */
    power_rate(203, "power_rate", "体力系数"),
    /** 释放球员技能 */
    skill(204, "skill", "球员技能", Attr.AI_Tigger),
    /** 使用道具体力恢复 */
    prop_update_power(205, "prop_up_power", "使用道具体力恢复", Attr.Player_Act, Attr.AI_Tigger),
    //====================================================================================
    //球队行为 - 300 - 399
    //====================================================================================
    /** 球队. 换人(上场、下场) */
    substitute(301, "substitute", "换人(上场、下场)", Attr.Team_Act, Attr.Custom, Attr.AI_Tigger),
    /** 球队. 使用道具 */
    use_item(302, "use_item", "使用道具", Attr.AI_Tigger),
    /** 球队. 更换战术 */
    change_tactics(303, "change_tactics", "更换战术", Attr.Custom, Attr.Team_Act, Attr.AI_Tigger),
    /** 球队. 获得对方战术 */
    tactics_look(304, "tactics_look", "获得对方战术", Attr.AI_Tigger),
    /** 球队. 落后分差. 主线赛程使用 */
    Behind_Score_Gap(305, "behind_score_gap", "球队落后分差"),
    /** 球队. 绝杀(至胜入球) Clutch shot */
    Team_Clutch_Shot(306, "t_clutch_shot", "球队_绝杀(至胜入球)", Attr.Team_Act),
    /** 球队. 分数交替次数 */
    Team_Score_Rotation_Num(307, "t_score_rotation", "球队_分数交替", Attr.Team_Act),
    /** 球队. 分差 */
    Team_Score_Gap(308, "t_score_gap", "球队_分差", Attr.Team_Act),
    //====================================================================================
    //球员球队公共行为 400 - 499
    //====================================================================================
    /** null */
    NULL(401, "null", "null"),
    /** 进攻cap */
    ocap(402, "acap", "进攻cap"),
    /** 防守cap */
    dcap(403, "dcap", "防守cap"),
    /** 技能能量或效率 */
    skill_power(404, "skill_power", "技能能量或效率"),
    /** 移除特效 */
    remove_effect(405, "remove_effect", "移除特效", Attr.AI_Tigger),

    //====================================================================================
    //比赛行为 500 - 599
    //====================================================================================
    /** 比赛. 分差 */
    Battle_Score_Gap(501, "score_gap", "分差", Attr.Match_Act),
    /** 比赛. 分数交替次数 */
    Battle_Score_Rotation_Num(502, "b_score_rotation", "分数交替"),
    /** 比赛. 进球转换球权 change possession */
    Change_Possession(503, "possession", "转换球权", Attr.Custom, Attr.AI_Tigger),
    /** 比赛. 小节回合数 */
    Step_Round(504, "step_round", "小节回合数", Attr.Match_Act),
    /** 比赛. 总回合数 */
    Round(505, "round", "总回合数"),
    /** 比赛. 小节暂停 */
    Step_Pause(506, "step_pause", "小节暂停", Attr.AI_Tigger),
    /** 比赛. 绝杀(至胜入球) Clutch shot */
    Battle_Clutch_Shot(507, "b_clutch_shot", "比赛_绝杀(至胜入球)"),
    /** 比赛. 暂停 */
    Pause(508, "pause", "比赛_暂停", Attr.Custom),
    /** 使用教练技能 */
    Coach_Skill(509, "coach_skill", "使用教练技能", Attr.Custom),
    //
    ;
    /** 类型id. 客户端服务器策划共用 */
    private final int type;
    /** 策划配置名称 */
    private final String configName;
    /** 注释 */
    private final String comment;
    /** 行为属性 */
    private final long attr;

    EActionType(int type, String configName, String comment) {
        this(type, configName, comment, Attr.NONE);
    }

    EActionType(int type, String configName, String comment, int... attrs) {
        this.type = type;
        this.configName = configName.toLowerCase();
        this.comment = comment;
        long attr = 0;
        for (int a : attrs) {
            attr |= a;
        }
        this.attr = attr;
    }

    static final class Attr {
        static final int NONE = 0;
        //        static final int _1 = 1;
        /** 比赛行为类型. 全局行为 */
        static final int Match_Act = 1 << 1;
        /** 比赛行为类型. 球队行为 */
        static final int Team_Act = 1 << 2;
        /** 比赛行为类型. 球员行为 */
        static final int Player_Act = 1 << 3;
        /** AI. 可以触发 AI 的行为 */
        static final int AI_Tigger = 1 << 4;
        /** 是否可以自定义 */
        static final int Custom = 1 << 5;

    }

    public int getType() {
        return type;
    }

    public String getConfigName() {
        return configName;
    }

    public String getComment() {
        return comment;
    }

    /** 比赛行为类型. true 不支持 */
    public boolean isDisableAct() {
        return ((Attr.Match_Act | Attr.Team_Act | Attr.Player_Act) & attr) == 0;
    }

    /** 比赛行为类型. 全局行为. true 是 */
    public boolean isMatchAct() {
        return BitUtil.hasBit(attr, Attr.Match_Act);
    }

    /** 比赛行为类型. 球队行为. true 是 */
    public boolean isTeamAct() {
        return BitUtil.hasBit(attr, Attr.Team_Act);
    }

    /** 比赛行为类型. 球员行为. true 是 */
    public boolean isPlayerAct() {
        return BitUtil.hasBit(attr, Attr.Player_Act);
    }

    @Override
    public String toString() {
        return configName;
    }

    public static final Map<Integer, EActionType> typeCaches = new HashMap<>();
    public static final Map<String, EActionType> nameCaches = new HashMap<>();

    static {
        for (EActionType t : values()) {
            if (typeCaches.containsKey(t.getType())) {
                throw new Error("duplicate action type :" + t.getType());
            }
            if (nameCaches.containsKey(t.getConfigName())) {
                throw new Error("duplicate action cfg name :" + t.getConfigName());
            }
            typeCaches.put(t.getType(), t);
            nameCaches.put(t.getConfigName(), t);
        }
    }

    public static EActionType convertByType(int type) {
        return typeCaches.get(type);
    }

    public static EActionType convertByName(String cfgName) {
        return nameCaches.get(cfgName);
    }

    public static void main(String[] args) {
        //        printShort();
        //        printFullShortWithHint();
        printCustomShortWithHint();
    }

    private static void printCustomShortWithHint() {
        StringBuilder sb = new StringBuilder();
        sb.append("行为id\t行为名称\t注释\t可以自定义\n");
        for (EActionType et : values()) {
            sb.append(et.getType()).append("\t").append(et.configName).append("\t").append(et.getComment())
                    .append("\t").append(BitUtil.hasBit(et.attr, Attr.Custom) ? "true" : "")
                    .append("\n");
        }
        System.out.println(sb);
    }

    private static void printFullShortWithHint() {
        StringBuilder sb = new StringBuilder();
        sb.append("行为id\t行为名称\t注释\t是比赛行为\t是球队行为\t是球员行为\t可以自定义\t可以关联AI的行为\n");
        for (EActionType et : values()) {
            sb.append(et.getType()).append("\t").append(et.configName).append("\t").append(et.getComment())
                    .append("\t").append(et.isMatchAct() ? "true" : "")
                    .append("\t").append(et.isTeamAct() ? "true" : "")
                    .append("\t").append(et.isPlayerAct() ? "true" : "")
                    .append("\t").append(BitUtil.hasBit(et.attr, Attr.Custom) ? "true" : "")
                    .append("\t").append(BitUtil.hasBit(et.attr, Attr.AI_Tigger) ? "true" : "")
                    .append("\n");
        }
        System.out.println(sb);
    }

    private static void printShort() {
        String str = "";
        for (EActionType et : values()) {
            str += et.getType() + "\t" + et.configName + "\t" + et.getComment() + "\n";
        }
        System.out.println(str);
    }
}
