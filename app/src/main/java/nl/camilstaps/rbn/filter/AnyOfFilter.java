package nl.camilstaps.rbn.filter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.camilstaps.rbn.Record;

public class AnyOfFilter<T> implements Filter, Collection<T> {
	private final List<T> values;
	private final Field field;

	public AnyOfFilter(Field field, List<T> values) {
		this.values = values;
		this.field = field;
	}

	public AnyOfFilter(Field field) {
		this(field, new ArrayList<T>());
	}

	public static AnyOfFilter just(Field field, Object value) {
		ArrayList<Object> list = new ArrayList<>();
		list.add(value);
		return new AnyOfFilter(field, list);
	}

	@Override
	public boolean matches(Record record) {
		switch (field) {
			case Band:
				return values.contains(record.getBand());
			case Mode:
				return values.contains(record.getMode());
			case Type:
				return values.contains(record.getType());
			case Dx:
				return values.contains(record.getDx());
			case De:
				return values.contains(record.getDe());
			default:
				throw new IllegalArgumentException("Invalid field " + field + " for AnyOfFilter.");
		}
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return values.contains(o);
	}

	@NonNull
	@Override
	public Iterator<T> iterator() {
		return values.iterator();
	}

	@NonNull
	@Override
	public Object[] toArray() {
		return values.toArray();
	}

	@NonNull
	@Override
	public <T1> T1[] toArray(T1[] a) {
		return values.toArray(a);
	}

	@Override
	public boolean add(T t) {
		return values.add(t);
	}

	@Override
	public boolean remove(Object o) {
		return values.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return values.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return values.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return values.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return values.retainAll(c);
	}

	@Override
	public void clear() {
		values.clear();
	}
}