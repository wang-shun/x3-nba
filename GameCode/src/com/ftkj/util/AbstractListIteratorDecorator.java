package com.ftkj.util;


import java.util.ListIterator;

/**
 * Provides basic behaviour for decorating a list iterator with extra functionality.
 * <p>
 * All methods are forwarded to the decorated list iterator.
 *
 * @since 3.0
 * @version $Id: AbstractListIteratorDecorator.java 1686855 2015-06-22 13:00:27Z tn $
 */
public class AbstractListIteratorDecorator<E> implements ListIterator<E> {

    /** The iterator being decorated */
    private final ListIterator<E> iterator;

    //-----------------------------------------------------------------------
    /**
     * Constructor that decorates the specified iterator.
     *
     * @param iterator  the iterator to decorate, must not be null
     * @throws NullPointerException if the iterator is null
     */
    public AbstractListIteratorDecorator(final ListIterator<E> iterator) {
        super();
        if (iterator == null) {
            throw new NullPointerException("ListIterator must not be null");
        }
        this.iterator = iterator;
    }

    /**
     * Gets the iterator being decorated.
     *
     * @return the decorated iterator
     */
    protected ListIterator<E> getListIterator() {
        return iterator;
    }

    //-----------------------------------------------------------------------

    /** {@inheritDoc} */
    public boolean hasNext() {
        return iterator.hasNext();
    }

    /** {@inheritDoc} */
    public E next() {
        return iterator.next();
    }

    /** {@inheritDoc} */
    public int nextIndex() {
        return iterator.nextIndex();
    }

    /** {@inheritDoc} */
    public boolean hasPrevious() {
        return iterator.hasPrevious();
    }

    /** {@inheritDoc} */
    public E previous() {
        return iterator.previous();
    }

    /** {@inheritDoc} */
    public int previousIndex() {
        return iterator.previousIndex();
    }

    /** {@inheritDoc} */
    public void remove() {
        iterator.remove();
    }

    /** {@inheritDoc} */
    public void set(final E obj) {
        iterator.set(obj);
    }

    /** {@inheritDoc} */
    public void add(final E obj) {
        iterator.add(obj);
    }

}
