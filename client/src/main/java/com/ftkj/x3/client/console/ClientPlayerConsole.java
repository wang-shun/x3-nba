package com.ftkj.x3.client.console;

import com.ftkj.console.PlayerConsole;
import com.ftkj.util.InfiniteRandomList;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author luch
 */
@Component
public class ClientPlayerConsole {
    private int maxGrade;
    private List<Integer> allPlayerIds;

    public void init() {
        maxGrade = PlayerConsole.getPlayerGradeMap().keySet().stream().max(Integer::compareTo).orElse(0);
    }

    public List<Integer> getAllPlayerIds() {
        if (allPlayerIds == null) {
            allPlayerIds = Collections.unmodifiableList(new ArrayList<>(PlayerConsole.getPlayerBeanMap().keySet()));
        }
        return allPlayerIds;
    }

    public int getMaxGrade() {
        return maxGrade;
    }

    public void afterReloadNbaPlayer() {

    }

    /** 无限随机所有球员id */
    public InfiniteRandomList<Integer> infiniteRandomAllPids() {
        return infiniteRandomAllPids(ThreadLocalRandom.current());
    }

    public InfiniteRandomList<Integer> infiniteRandomAllPids(Random random) {
        return new InfiniteRandomList<>(random, getAllPlayerIds());
    }

}
