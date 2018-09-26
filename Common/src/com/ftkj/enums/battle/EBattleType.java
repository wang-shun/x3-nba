package com.ftkj.enums.battle;

import java.util.HashMap;
import java.util.Map;

public enum EBattleType {
    普通比赛(1, "Common_Match"),
    主线赛程(2, "Main_Match"),
    即时比赛跨服(3, "CrossPK_Match"),
    街球副本(4, "Stree_Match"),
    联盟赛(5, "League_Match"),
    训练馆(6, "Train_Match"),
    联盟球馆赛(7, "LeagueArena"),
    擂台赛(8, "Room_Match"),
    联盟组队赛(9, "LeagueGroup_Match"),
    /** 主线赛程. 常规赛 */
    Main_Match_Normal(30, "Main_Match"),
    /** 主线赛程. 锦标赛 */
    Main_Match_Championship(31, "Main_Match"),
    /** 天梯赛 */
    Ranked_Match(37, "Ranked_Match"),
    /** 全明星 */
    AllStar(38, "AllStar_Match"),
    /** 个人竞技场 */
    Arena(39, "Arena_Match"),
    /** 好友切磋 */
    Friend_Match(40, "Friend_Match"),
    /** 新秀对抗赛 */
    Starlet_Dual_Meet(41, "Starlet_Dual_Meet"),   
    /** 极限挑战 */
    Limit_Challenge(42, "Limit_Challenge"),
    /** 球星荣耀*/
    honorMatch(43, "honorMatch"),
    
    首场模拟比赛(99, "Help_Match"),

    // 多人赛-列表
    多人赛_100(100, "Knockout_Match"),
    多人赛_101(101, "Knockout_Match"),
    多人赛_102(102, "Knockout_Match"),
    多人赛_103(103, "Knockout_Match"),
    多人赛_104(104, "Knockout_Match"),
    多人赛_105(105, "Knockout_Match"),
    多人赛_106(106, "Knockout_Match"),
    多人赛_107(107, "Knockout_Match"),
    多人赛_108(108, "Knockout_Match"),
    多人赛_109(109, "Knockout_Match"),

    多人赛_110(110, "Knockout_Match"),
    多人赛_111(111, "Knockout_Match"),
    多人赛_112(112, "Knockout_Match"),
    多人赛_113(113, "Knockout_Match"),
    多人赛_114(114, "Knockout_Match"),
    多人赛_115(115, "Knockout_Match"),
    多人赛_116(116, "Knockout_Match"),
    多人赛_117(117, "Knockout_Match"),
    多人赛_118(118, "Knockout_Match"),
    多人赛_119(119, "Knockout_Match"),

    多人赛_120(120, "Knockout_Match"),
    多人赛_121(121, "Knockout_Match"),
    多人赛_122(122, "Knockout_Match"),
    多人赛_123(123, "Knockout_Match"),
    多人赛_124(124, "Knockout_Match"),
    多人赛_125(125, "Knockout_Match"),
    多人赛_126(126, "Knockout_Match"),
    多人赛_127(127, "Knockout_Match"),
    多人赛_128(128, "Knockout_Match"),
    多人赛_129(129, "Knockout_Match"),

    多人赛_130(130, "Knockout_Match"),
    多人赛_131(131, "Knockout_Match"),
    多人赛_132(132, "Knockout_Match"),
    多人赛_133(133, "Knockout_Match"),
    多人赛_134(134, "Knockout_Match"),
    多人赛_135(135, "Knockout_Match"),
    多人赛_136(136, "Knockout_Match"),
    多人赛_137(137, "Knockout_Match"),
    多人赛_138(138, "Knockout_Match"),
    多人赛_139(139, "Knockout_Match"),

    多人赛_140(140, "Knockout_Match"),
    多人赛_141(141, "Knockout_Match"),
    多人赛_142(142, "Knockout_Match"),
    多人赛_143(143, "Knockout_Match"),
    多人赛_144(144, "Knockout_Match"),
    多人赛_145(145, "Knockout_Match"),
    多人赛_146(146, "Knockout_Match"),
    多人赛_147(147, "Knockout_Match"),
    多人赛_148(148, "Knockout_Match"),
    多人赛_149(149, "Knockout_Match"),

    多人赛_150(150, "Knockout_Match"),
    多人赛_151(151, "Knockout_Match"),
    多人赛_152(152, "Knockout_Match"),
    多人赛_153(153, "Knockout_Match"),
    多人赛_154(154, "Knockout_Match"),
    多人赛_155(155, "Knockout_Match"),
    多人赛_156(156, "Knockout_Match"),
    多人赛_157(157, "Knockout_Match"),
    多人赛_158(158, "Knockout_Match"),
    多人赛_159(159, "Knockout_Match"),

    多人赛_160(160, "Knockout_Match"),
    多人赛_161(161, "Knockout_Match"),
    多人赛_162(162, "Knockout_Match"),
    多人赛_163(163, "Knockout_Match"),
    多人赛_164(164, "Knockout_Match"),
    多人赛_165(165, "Knockout_Match"),
    多人赛_166(166, "Knockout_Match"),
    多人赛_167(167, "Knockout_Match"),
    多人赛_168(168, "Knockout_Match"),
    多人赛_169(169, "Knockout_Match"),

    多人赛_170(170, "Knockout_Match"),
    多人赛_171(171, "Knockout_Match"),
    多人赛_172(172, "Knockout_Match"),
    多人赛_173(173, "Knockout_Match"),
    多人赛_174(174, "Knockout_Match"),
    多人赛_175(175, "Knockout_Match"),
    多人赛_176(176, "Knockout_Match"),
    多人赛_177(177, "Knockout_Match"),
    多人赛_178(178, "Knockout_Match"),
    多人赛_179(179, "Knockout_Match"),

    多人赛_180(180, "Knockout_Match"),
    多人赛_181(181, "Knockout_Match"),
    多人赛_182(182, "Knockout_Match"),
    多人赛_183(183, "Knockout_Match"),
    多人赛_184(184, "Knockout_Match"),
    多人赛_185(185, "Knockout_Match"),
    多人赛_186(186, "Knockout_Match"),
    多人赛_187(187, "Knockout_Match"),
    多人赛_188(188, "Knockout_Match"),
    多人赛_189(189, "Knockout_Match"),

    多人赛_190(190, "Knockout_Match"),
    多人赛_191(191, "Knockout_Match"),
    多人赛_192(192, "Knockout_Match"),
    多人赛_193(193, "Knockout_Match"),
    多人赛_194(194, "Knockout_Match"),
    多人赛_195(195, "Knockout_Match"),
    多人赛_196(196, "Knockout_Match"),
    多人赛_197(197, "Knockout_Match"),
    多人赛_198(198, "Knockout_Match"),
    多人赛_199(199, "Knockout_Match"),;

    private int id;
    private String type;

    private EBattleType(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    //通过ID，取对应的战术枚举
    public static final Map<Integer, EBattleType> battleTypeMap = new HashMap<Integer, EBattleType>();

    static {
        for (EBattleType et : EBattleType.values()) {
            battleTypeMap.put(et.getId(), et);
        }
    }

    /**
     * 根据比赛类型ID取枚举
     *
     * @param id
     * @return
     */
    public static EBattleType getBattleType(int id) {
        return battleTypeMap.get(id);
    }

    public static void main(String[] args) {
        //        printTypeEachLine();
        printKnockoutMatchIds();
    }

    private static void printKnockoutMatchIds() {
        StringBuilder sb = new StringBuilder();
        for (int i = 100; i <= 199; i++) {
            sb.append(i).append(";");
        }
        System.out.println(sb.toString());
    }

    private static void printTypeEachLine() {
        String str = "";
        for (EBattleType et : EBattleType.values()) {
            String name = et.name();
            str += et.getId() + "\t" + name + "\n";
        }
        System.out.println(str);
    }

    public String getType() {
        return type;
    }

}
