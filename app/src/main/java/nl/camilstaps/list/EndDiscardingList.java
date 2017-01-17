package nl.camilstaps.list;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class EndDiscardingList<E> implements List<E> {
	private transient Object[] elements;
	private int cursor = 0;
	private boolean looped = false;
	private int size = 0;
	private final int maxLength;

	public EndDiscardingList(int length) {
		elements = new Object[length];
		maxLength = length;
	}

	private int getRealIndex(int index) {
		if (index >= maxLength)
			throw new NoSuchElementException();
		else if (looped)
			return (cursor + index) % maxLength;
		else
			return index;
	}

	@Override
	public E get(int index) {
		return (E) elements[getRealIndex(index)];
	}

	@Override
	public E set(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void add(int index, E element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public E remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<E> listIterator() {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public ListIterator<E> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean add(E e) {
		elements[cursor] = e;
		size = Math.min(size + 1, maxLength);
		cursor++;
		if (cursor >= maxLength) {
			cursor = 0;
			looped = true;
		}
		return true;
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for (E e : c)
			if (!add(e))
				return false;
		return true;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		cursor = 0;
		size = 0;
		looped = false;
	}
}
