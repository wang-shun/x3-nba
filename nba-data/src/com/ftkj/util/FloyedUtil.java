package com.ftkj.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 弗洛伊德算法   求最短路径
 * @author monnes
 * @version V1.0
 */
public class FloyedUtil {
    private static final Logger logger = LoggerFactory.getLogger(FloyedUtil.class);

    public static int M = Integer.MAX_VALUE;
    public static final String flag = "->";

    public static int MAXSUM(int a, int b) {
        return (a != M && b != M) ? (a + b) : M;
    }
    
    /**
     * 弗洛伊德算法 求最短距离与路径
     * @param dist
     * @return
     */
    public static ArrayList<Integer[][]> flody(Integer[][] dist) {
        int size = dist.length;
        Integer[][] path = new Integer[size][size];//存储的是从i->j经过的最后一个节点     
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                path[i][j] = i;
            }
        }
        for (int k = 0; k < size; k++) {
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (dist[i][j] > MAXSUM(dist[i][k], dist[k][j])) {
                        path[i][j] = path[k][j];//存储的是从i->j经过的最后一个节点        
                        dist[i][j] = MAXSUM(dist[i][k], dist[k][j]);
                    }
                }
            }
        }
        ArrayList<Integer[][]> list = new ArrayList<Integer[][]>();
        list.add(dist);
        list.add(path);
        return list;
    }

    private static Integer[] reverse(Integer[] chain, int count) {
        int temp;
        for (int i = 0, j = count - 1; i < j; i++, j--) {
            temp = chain[i];
            chain[i] = chain[j];
            chain[j] = temp;
        }
        return chain;
    }
    
    /**
     * 显示路径 得到最近路径Map对象
     * @param list
     * @return
     */
    public static Map<String,Integer> display_path(ArrayList<Integer[][]> list) {
        Integer[][] dist = list.get(0);
        Integer[][] path = list.get(1);
        int size = dist.length; 
        Integer[] chain = new Integer[size];
        //
        Map<String,Integer> retMap = new HashMap<String, Integer>();
        logger.info("orign->dist" + " dist " + " path");
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                //if (i != j) {//只是避免了vi->vi的输出
                    //输出源到目的地 
                    String desc = "   " + (i) + flag + (j) + "     ";
                    //输出最短路径的长度      
                    if (dist[i][j] == M) {
                        desc += " NA ";
                    } else {
                        desc += dist[i][j] + "      ";
                        //存入最短路径值
                        retMap.put(i + flag + (j), dist[i][j]);
                        //显示路径
                        int count = 0;
                        int k = j;
                        do {
                            k = chain[count++] = path[i][k];
                        } while (i != k);
                        chain = reverse(chain, count);
                        //输出路径 
                        desc += chain[0] + "";
                        for (k = 1; k < count; k++) {
                            desc += flag + (chain[k]);
                        }
                        desc += flag + j;
                    }
                    logger.info(desc);
               // }
            }
        }
        return retMap;
    }
    
    /**
     * 根据数据得到最小路径的Map集合
     * @param dist
     * @return
     */
    public static Map<String,Integer> getMinPathMap(Integer[][] dist) {
        ArrayList<Integer[][]> list = flody(dist);
        return display_path(list);
    }
    
    public static void main(String[] args) {
        Integer[][] dist = { 
                { 0, 5, M, M, M, M , M}, 
                { 5, 0, 15, M, 15, 10 , M},
                { M, 15, 0, 15, M, 10 , M}, 
                { M, M, 15, 0, 15, 10 , M},
                { M, 15, M, 15, 0, 10 , M}, 
                { M, 10, 10, 10, 10, 0, 10},
                { M, M, M, M, M, 10, 0}};// 建立一个权值矩阵 
        ArrayList<Integer[][]> list = flody(dist);
        display_path(list);
    }
}
