package com.ftkj.util.table;

import com.ftkj.util.IntervalInt;
import com.ftkj.util.StringUtil;
import com.google.common.collect.ImmutableList;

/**
 * 权重配置. 格式: id,权重;id,权重;
 *
 * @author luch
 */
public final class DefaultWeightList extends WeightList<DefaultWeightElement> {
    private static final long serialVersionUID = 3957210293237480783L;

    public DefaultWeightList(ImmutableList<DefaultWeightElement> elements, int totalWeight) {
        super(elements, totalWeight);
    }

    private static final class Holder {
        private static final DefaultWeightList EMPTY = new DefaultWeightList(ImmutableList.of(), 0);
    }

    /**
     * 解析权重格式字符串. 格式为: id,权重;id,权重;
     */
    public static DefaultWeightList parse(String str) {
        if (str == null || str.isEmpty() || str.equals(StringUtil.SEMICOLON)) {
            return Holder.EMPTY;
        }
        String[] arrA = str.split(StringUtil.SEMICOLON);
        ImmutableList.Builder<DefaultWeightElement> list = ImmutableList.builder();

        int lower = 0;
        for (int i = 0; i < arrA.length; i++) {
            try {
                String strA = arrA[i];
                String[] arrB = strA.split(StringUtil.COMMA);
                int id = Integer.parseInt(arrB[0]);
                int weight = Integer.parseInt(arrB[1]);
                DefaultWeightElement iqr = new DefaultWeightElement(
                        id,
                        new IntervalInt(lower, lower + weight - 1));
                list.add(iqr);
                lower += weight;
            } catch (Exception e) {
                throw new RuntimeException("i " + i + " str " + str, e);
            }
        }
        return new DefaultWeightList(list.build(), lower);
    }

}
