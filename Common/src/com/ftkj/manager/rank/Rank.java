package com.ftkj.manager.rank;

import java.io.Serializable;

/** 排行榜 */
public abstract class Rank implements Serializable {
    private static final long serialVersionUID = 1L;

    private double score;

    public abstract void updateScore();

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
