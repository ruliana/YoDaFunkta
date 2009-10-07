package yodafunkta;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

public class InfiniteList<T> implements List<T>, Queue<T> {

    private final Functor functor;
    private final T startingElement;
    private T currentElement;

    public InfiniteList(Functor functor, T startingElement) {
        this.functor = functor;
        this.startingElement = startingElement;
        this.currentElement = startingElement;
    }

    /**
     * Not possible to add elements to an infinite list.
     */
    public boolean add(T arg0) {
        throw new UnsupportedOperationException("Not possible to add elements to an infinite list.");
    }

    /**
     * Not possible to add elements to an infinite list.
     */
    public void add(int arg0, T arg1) {
        throw new UnsupportedOperationException("Not possible to add elements to an infinite list.");
    }

    /**
     * Not possible to add elements to an infinite list.
     */
    public boolean addAll(Collection<? extends T> arg0) {
        throw new UnsupportedOperationException("Not possible to add elements to an infinite list.");
    }

    /**
     * Not possible to add elements to an infinite list.
     */
    public boolean addAll(int arg0, Collection<? extends T> arg1) {
        throw new UnsupportedOperationException("Not possible to add elements to an infinite list.");
    }

    public boolean contains(Object arg0) {
        throw new UnsupportedOperationException("Not possible to run until the end of and infinite list to find that");
    }

    public boolean containsAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("Not possible to run until the end of and infinite list to find that");
    }

    public int indexOf(Object arg0) {
        throw new UnsupportedOperationException("Not possible to run until the end of and infinite list to find that");
    }

    public int lastIndexOf(Object arg0) {
        throw new UnsupportedOperationException("Not possible to run until the end of and infinite list to find that");
    }

    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException("Only iterator() is supported on infinite lists");
    }

    public ListIterator<T> listIterator(int arg0) {
        throw new UnsupportedOperationException("Only iterator() is supported on infinite lists");
    }

    public boolean remove(Object arg0) {
        throw new UnsupportedOperationException("Cannot remove objects from an infinite lists");
    }

    public T remove(int arg0) {
        throw new UnsupportedOperationException("Cannot remove objects from an infinite lists");
    }

    public boolean removeAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("Cannot remove objects from an infinite lists");
    }

    public boolean retainAll(Collection<?> arg0) {
        throw new UnsupportedOperationException("Cannot remove objects from an infinite lists");
    }

    public T set(int arg0, T arg1) {
        throw new UnsupportedOperationException("Cannot set objects from an infinite lists");
    }

    public int size() {
        throw new UnsupportedOperationException("Infinit lists are infinites, they do not have size");
    }

    public Object[] toArray() {
        throw new UnsupportedOperationException("Cannot convert an infinite list to an array");
    }

    public <T> T[] toArray(T[] arg0) {
        throw new UnsupportedOperationException("Cannot convert an infinite list to an array");
    }

    /**
     * That means "reset"
     */
    public void clear() {
        currentElement = startingElement;
    }

    /**
     * Infinite lists are never empty
     */
    public boolean isEmpty() {
        return false;
    }

    public Iterator<T> iterator() {
        return new Iter();
    }

    public boolean offer(T e) {
        return false;
    }

    public List<T> subList(int start, int end) {
        LinkedList<T> result = new LinkedList<T>();
        T tempElement = startingElement;
        for (int i = 0; i < end; i++) {
            if (i >= start) result.add(tempElement);
            tempElement = functor.run(tempElement);
        }
        return result;
    }

    public T get(int index) {
        T tempElement = startingElement;
        for (int i = 0; i < index; i++) {
            tempElement = functor.run(tempElement);
        }
        return tempElement;
    }


    public T element() {
        return currentElement;
    }

    public T peek() {
        return currentElement;
    }

    public T poll() {
        T result = currentElement;
        currentElement = functor.run(currentElement);
        return result;
    }

    public T remove() {
        return poll();
    }
    
    private class Iter implements Iterator<T> {

        public T current;
        
        // There is always the next element
        public boolean hasNext() {
            return true;
        }

        public T next() {
            if (current == null) {
                current = startingElement;
            } else {
                current = functor.run(current);
            }
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException("Cannot remove elements of an infinite list");
        }
    }
}
