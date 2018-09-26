package com.ftkj.util;

import com.google.common.collect.Lists;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {

    public static int randInt(int max) {
        return ThreadLocalRandom.current().nextInt(max);
    }

    /**
     * 1-10(x>=1&&x<=10)
     *
     * @param min
     * @param max
     * @return
     */
    public static int randInt(int min, int max) {
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /**
     * 是否命中
     *
     * @param max  最大范围
     * @param rate 命中概率范围
     * @return true命中
     */
    public static boolean randHit(int max, int rate) {
        int ran = ThreadLocalRandom.current().nextInt(max) + 1;
        if (ran <= rate) {
            return true;
        }
        return false;
    }

    /**
     * 在数组里面随机
     *
     * @param list     数组，长度必须大于随即大小
     * @param size     随机大小
     * @param isRepeat 是否允许重复， 默认true，false是不重复
     * @return
     */
    public static <T> List<T> randList(List<T> list, int size, boolean isRepeat) {
        List<Integer> hit = getRandomBySeed(DateTime.now().getMillis(), list.size(), size, isRepeat);
        List<T> hitList = Lists.newArrayList();
        hit.forEach(index -> hitList.add(list.get(index)));
        return hitList;
    }

    /**
     * 命中map
     *
     * @param max     概率总值
     * @param rateMap id:概率  例如： max=1000  1：100 2:200  也就是命中1概率100/1000,命中2是200/1000
     * @return 命中的id值， -1 是没有命中结果, 命中值不是满值时，会出现这种情况，也就是 id_1和id_2的总命中不满1000
     */
    @SuppressWarnings("unchecked")
    private static <T> T randMap(int max, Map<T, Integer> rateMap, boolean isDebug) {
        int hit = ThreadLocalRandom.current().nextInt(max);
        List<RanArea> list = mapToArea(rateMap);
        Object hitID = null;
        for (RanArea a : list) {
            if (a.isArea(hit)) {
                hitID = a.id;
                break;
            }
        }
        if (isDebug) {
            System.err.println("area[" + list.toString() + "\n]");
            System.err.println("命中：[max=" + max + "]    hit[" + hit + "]   hitID[" + hitID + "]");
        }
        return (T) hitID;
    }

    /**
     * 随机
     *
     * @param rateMap   原始概率
     * @param adjustMap 调整概率，可以空
     * @return
     */
    public static <T> T randMap(Map<T, Integer> rateMap, Map<T, Integer> adjustMap) {
        if (adjustMap != null) {
            for (T key : adjustMap.keySet()) {
                int addRate = adjustMap.get(key);
                if (rateMap.containsKey(key)) {
                    addRate += rateMap.get(key);
                }
                rateMap.put(key, addRate);
            }
        }
        return randMap(rateMap.values().stream().mapToInt(s -> s).sum(), rateMap, false);
    }

    /**
     * 随机
     *
     * @param rateMap 原始概率 ID:Rate
     * @return int类型ID
     */
    public static <T> T randMap(Map<T, Integer> rateMap) {
        return (T) randMap(rateMap.values().stream().mapToInt(s -> s).sum(), rateMap, false);
    }

    /**
     * 随机，会打印概率，调试调用
     *
     * @param rateMap
     * @return
     */
    public static <T> T testRandMap(Map<T, Integer> rateMap) {
        return randMap(rateMap.values().stream().mapToInt(s -> s).sum(), rateMap, true);
    }

    private static <T> List<RanArea> mapToArea(Map<T, Integer> rateMap) {
        List<RanArea> list = Lists.newArrayList();
        int min = 0;
        int max = 0;
        for (T key : rateMap.keySet()) {
            max = min + rateMap.get(key);
            list.add(new RanArea(key, min, max));
            min = max;
        }
        return list;
    }

    /**
     * 命中范围工具类
     *
     * @author Jay
     * @time:2017年5月12日 下午6:44:30
     */
    private static class RanArea {
        Object id;
        int min;
        int max;

        public RanArea(Object id, int min, int max) {
            super();
            this.id = id;
            this.min = min;
            this.max = max;
        }

        /**
         * 命中数值是否在范围内
         *
         * @param hit
         * @return
         */
        public boolean isArea(int hit) {
            if (hit >= min && hit < max) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            return "\n  RanArea [id=" + id + ", min=" + min + ", max=" + max + "]";
        }
    }

    /**
     * 给定随机因子，最大数，取随机数
     *
     * @param seed     因子不变，产生随机数的顺序不变
     * @param max
     * @param size     不重复的情况下，size不能比max大，否则会出现重复
     * @param repeated 是否允许重复， 默认true，false是不重复
     * @return
     */
    public static List<Integer> getRandomBySeed(long seed, int max, int size, boolean repeated) {
        List<Integer> list = Lists.newArrayList();
        if (max == 0) { return list; }
        Random temp = new Random(seed);
        // 如果size 和 max 比较相近的话，不重复算法效率会很低，要优化一下
        // 当size占max的3/4以上，随机重复后，值+1取模到不重复为止
        boolean justAdd = size * 1.0f / max >= 0.75f;
        while (list.size() < size) {
            int val = temp.nextInt(max);
            if (!repeated && list.contains(val) && max >= size) {
                // 走另外一套不重复概率
                if (justAdd) {
                    while (true) {
                        val = (val + 1) % max;
                        if (!list.contains(val)) {
                            list.add(val);
                            break;
                        }
                    }
                }
                continue;
            }
            list.add(val);
        }
        return list;
    }

    /**
     * 测试
     *
     * @param args
     */
    public static void main(String[] args) {
        //		Map<Integer, Integer> rate = Maps.newHashMap();
        //		rate.put(1, 100);
        //		rate.put(2, 50);
        //		rate.put(3, 25);
        //		rate.put(-1, 25);
        //		RandomUtil.testRandMap(rate);
        System.err.println(getRandomBySeed(DateTime.now().getMillis(), 10, 10, false));
    }

}
