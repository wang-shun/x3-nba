package com.ftkj.util;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 无限随机产生列表中的元素
 */
public final class InfiniteRandomList<E> extends InfiniteRandom<E> {
    private final List<E> list;

    public InfiniteRandomList(Random random, List<E> list) {
        super(random, list.size());
        this.list = list;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    protected class Itr extends IRItr {
        private int count;

        @Override
        public E next() {
            if (count >= Integer.MAX_VALUE) {
                throw new IllegalStateException("loop Integer.MAX_VALUE");
            }
            count++;
            return list.get(nextInt());
        }
    }

}
