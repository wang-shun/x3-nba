package com.ftkj.manager.rank;

/**
 * @author tim.huang
 * 2017年5月17日
 */
public class TeamRank extends Rank {
    private static final long serialVersionUID = 1L;

    private long teamId;
    private String name;
    private String logo;
    private int level;
    private String leagueName;

    public TeamRank(long teamId, String name, String logo, int level, String leagueName) {
        super();
        this.teamId = teamId;
        this.name = name;
        this.logo = logo;
        this.level = level;
        this.leagueName = leagueName;
    }

    public void updateScore(int score) {
        this.level = score;
        super.setScore(score);
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
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

    @Override
    public void updateScore() {
        // TODO Auto-generated method stub

    }
}

