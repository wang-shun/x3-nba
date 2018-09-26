package com.ftkj.util;

import java.util.Iterator;
import java.util.Random;

/**
 * 无限随机
 */
public abstract class InfiniteRandom<E> implements Iterable<E> {
    private final java.util.Random random;
    private final int bound;

    public InfiniteRandom(Random random, int bound) {
        this.random = random;
        this.bound = bound;
    }

    protected int nextInt() {
        return random.nextInt(bound);
    }

    protected abstract class IRItr implements Iterator<E> {
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final boolean hasNext() {
            return true;
        }
    }

}
