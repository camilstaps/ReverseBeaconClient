package nl.camilstaps.list;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class EndDiscardingList<E> implements List<E>, Serializable {
	private final transient Object[] elements;
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

	public synchronized void bumpToEnd(int index) {
		E temp = get(index);
		int end = (cursor - 1 + maxLength) % maxLength;
		for (int i = getRealIndex(index); i != end; i = (i + 1) % maxLength)
			elements[i] = elements[(i+1) % maxLength];
		elements[end] = temp;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E get(int index) {
		return (E) elements[getRealIndex(index)];
	}

	@Override
	public synchronized E set(int index, E element) {
		E prev = get(index);
		elements[getRealIndex(index)] = element;
		return prev;
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
		return new Itr();
	}

	@NonNull
	@Override
	public Object[] toArray() {
		throw new UnsupportedOperationException();
	}

	@NonNull
	@Override
	public <T> T[] toArray(@NonNull T[] a) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean add(E e) {
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
	public boolean containsAll(@NonNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public synchronized boolean addAll(@NonNull Collection<? extends E> c) {
		for (E e : c)
			if (!add(e))
				return false;
		return true;
	}

	@Override
	public boolean addAll(int index, @NonNull Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		cursor = 0;
		size = 0;
		looped = false;
	}

	private class Itr implements Iterator<E> {
		private int index = 0;
		private final int expectedCursor = cursor;

		@Override
		public boolean hasNext() {
			return index < size();
		}

		@Override
		public E next() {
			if (cursor != expectedCursor)
				throw new ConcurrentModificationException();
			if (index >= size())
				throw new NoSuchElementException();
			return get(index++);
		}
	}
}
