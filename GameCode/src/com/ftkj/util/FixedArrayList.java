package com.ftkj.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * same as {@link Arrays.ArrayList}
 *
 * @author luch
 */
public class FixedArrayList<E> extends AbstractList<E>
        implements RandomAccess, java.io.Serializable {
    private static final long serialVersionUID = -3379834399369225973L;
    private final E[] a;

    @SuppressWarnings("unchecked")
    public FixedArrayList(Class<E> clazz, int size) {
        a = (E[]) Array.newInstance(clazz, size);
    }

    public FixedArrayList(E[] array) {
        a = Objects.requireNonNull(array);
    }

    @Override
    public int size() {
        return a.length;
    }

    @Override
    public Object[] toArray() {
        return a.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] toArray(T[] a) {
        int size = size();
        if (a.length < size) {
            return Arrays.copyOf(this.a, size,
                    (Class<? extends T[]>) a.getClass());
        }
        System.arraycopy(this.a, 0, a, 0, size);
        if (a.length > size) { a[size] = null; }
        return a;
    }

    @Override
    public boolean add(final E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public void add(final int index, final E object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean addAll(final Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("List is fixed size");
    }

    public void resetAll() {
        for (int i = 0; i < a.length; i++) {
            a[i] = null;
        }
    }

    @Override
    public E get(int index) {
        return a[index];
    }

    public E computeIfAbsent(int index,
                             Function<Integer, E> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        E v;
        if ((v = get(index)) == null) {
            E newValue;
            if ((newValue = mappingFunction.apply(index)) != null) {
                set(index, newValue);
                return newValue;
            }
        }
        return v;
    }

    @Override
    public E set(int index, E element) {
        E oldValue = a[index];
        a[index] = element;
        return oldValue;
    }

    @Override
    public int indexOf(Object o) {
        E[] a = this.a;
        if (o == null) {
            for (int i = 0; i < a.length; i++)
                if (a[i] == null) { return i; }
        } else {
            for (int i = 0; i < a.length; i++)
                if (o.equals(a[i])) { return i; }
        }
        return -1;
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }

    @Override
    public E remove(final int index) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean remove(final Object object) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean removeAll(final Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public boolean retainAll(final Collection<?> coll) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        throw new UnsupportedOperationException("List is fixed size");
    }

    @Override
    public Spliterator<E> spliterator() {
        return Spliterators.spliterator(a, Spliterator.ORDERED);
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        Objects.requireNonNull(action);
        for (E e : a) {
            action.accept(e);
        }
    }

    @Override
    public void replaceAll(UnaryOperator<E> operator) {
        Objects.requireNonNull(operator);
        E[] a = this.a;
        for (int i = 0; i < a.length; i++) {
            a[i] = operator.apply(a[i]);
        }
    }

    @Override
    public void sort(Comparator<? super E> c) {
        Arrays.sort(a, c);
    }

    @Override
    public Iterator<E> iterator() {
        return UnmodifiableIterator.unmodifiableIterator(super.iterator());
    }

    @Override
    public ListIterator<E> listIterator() {
        return new FixedSizeListIterator(super.listIterator(0));
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return new FixedSizeListIterator(super.listIterator(index));
    }

    /**
     * List iterator that only permits changes via set()
     */
    private class FixedSizeListIterator extends AbstractListIteratorDecorator<E> {
        private FixedSizeListIterator(final ListIterator<E> iterator) {
            super(iterator);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("List is fixed size");
        }

        @Override
        public void add(final Object object) {
            throw new UnsupportedOperationException("List is fixed size");
        }
    }

    public boolean isFull() {
        return true;
    }

    public int maxSize() {
        return size();
    }

    public static void main(String[] args) {
        List<Long> list = new FixedArrayList<>(Long.class, 64);
        System.out.println(list.get(0));
        list.set(0, 0L);
        System.out.println(list.get(0));
        list.set(63, 63L);
        System.out.println(list.get(63));
        list.set(61, 61L);
        System.out.println(list);

        list.sort((o1, o2) -> {
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return Long.compare(o2, o1);
        });
        System.out.println(list);
    }
}
