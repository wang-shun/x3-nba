package com.ftkj.util.table;

import com.ftkj.util.BinarySearch;
import com.ftkj.util.IntervalInt;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Random;

/**
 * 权重列表.
 *
 * @author luch
 */
public class WeightList<E extends WeightElement> implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(WeightList.class);
    private static final long serialVersionUID = -8999629376827502932L;
    /** 子元素 */
    private final ImmutableList<E> elements;
    /** 总权重 */
    private final int totalWeight;

    public WeightList() {
        this(ImmutableList.of(), 0);
    }

    public WeightList(ImmutableList<E> elements, int totalWeight) {
        this.elements = elements;
        this.totalWeight = totalWeight;
    }

    /** 按权重随机获取一个子元素. */
    public E randomElement(Random tlr) {
        if (totalWeight <= 0) {
            return null;
        }
        int rnd = tlr.nextInt(totalWeight);
        int index = BinarySearch.binarySearch(elements, rnd, (el, key) -> IntervalInt.compare(el.getWeight(), key));
        log.debug("random {} from totalWeight {}, result idx {}, size {}", rnd, totalWeight, index, elements.size());
        if (index < 0) {
            return null;
        }
        return elements.get(index);
    }

    public ImmutableList<E> getElements() {
        return elements;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public String toString() {
        return "{" +
                "\"elements\":" + elements +
                ", \"totalWeight\":" + totalWeight +
                '}';
    }
}
