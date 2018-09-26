package com.ftkj.manager.rank;

/**
 * 球队战力排行榜
 *
 * @author tim.huang
 * 2017年6月5日
 */
public class TeamPowerRank extends Rank {
    private static final long serialVersionUID = 1L;

    private long teamId;
    private String name;
    private String logo;
    private String leagueName;
    /** 球队总战力. 包括阵容球员战力和球队战力 */
    private int totalCap;
    /** 阵容首发进攻战力 + 球队场下进攻战力 */
    private int ocap;
    /** 阵容首发防守战力 + 球队场下防守战力 */
    private int dcap;

    public TeamPowerRank(long teamId, String name, String logo,
                         String leagueName, int totalCap, int ocap, int dcap) {
        super();
        this.teamId = teamId;
        this.name = name;
        this.logo = logo;
        this.leagueName = leagueName;
        this.totalCap = totalCap;
        this.ocap = ocap;
        this.dcap = dcap;
    }

    public void updateScore(int totalCap, int ocap, int dcap) {
        this.totalCap = totalCap;
        this.ocap = ocap;
        this.dcap = dcap;
        super.setScore(totalCap);
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public int getTotalCap() {
        return totalCap;
    }

    public void setTotalCap(int totalCap) {
        this.totalCap = totalCap;
    }

    public int getOcap() {
        return ocap;
    }

    public void setOcap(int ocap) {
        this.ocap = ocap;
    }

    public int getDcap() {
        return dcap;
    }

    public void setDcap(int dcap) {
        this.dcap = dcap;
    }

    @Override
    public void updateScore() {
    }

}
