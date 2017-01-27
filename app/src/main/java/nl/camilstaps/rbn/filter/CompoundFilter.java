package nl.camilstaps.rbn.filter;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import nl.camilstaps.rbn.Entry;

public class CompoundFilter implements Filter, Collection<Filter> {
	private final List<Filter> filters;
	private final Method method;

	public CompoundFilter(List<Filter> filters, Method method) {
		this.filters = filters;
		this.method = method;
	}

	public CompoundFilter(Method method) {
		this(new ArrayList<Filter>(), method);
	}

	@Override
	public boolean matches(Entry entry) {
		switch (method) {
			case And:
				for (Filter filter : filters)
					if (!filter.matches(entry))
						return false;
				return true;
			case Or:
				for (Filter filter : filters)
					if (filter.matches(entry))
						return true;
				return false;
			default:
				throw new IllegalArgumentException("Unknown method " + method + " for CompoundFilter.");
		}
	}

	@Override
	public int size() {
		return filters.size();
	}

	@Override
	public boolean isEmpty() {
		return filters.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return filters.contains(o);
	}

	@NonNull
	@Override
	public Iterator<Filter> iterator() {
		return filters.iterator();
	}

	@NonNull
	@Override
	public Object[] toArray() {
		return filters.toArray();
	}

	@NonNull
	@Override
	public <T> T[] toArray(@NonNull T[] a) {
		return filters.toArray(a);
	}

	@Override
	public boolean add(Filter filter) {
		return filters.add(filter);
	}

	@Override
	public boolean remove(Object o) {
		return filters.remove(o);
	}

	@Override
	public boolean containsAll(@NonNull Collection<?> c) {
		return filters.containsAll(c);
	}

	@Override
	public boolean addAll(@NonNull Collection<? extends Filter> c) {
		return filters.addAll(c);
	}

	@Override
	public boolean removeAll(@NonNull Collection<?> c) {
		return filters.removeAll(c);
	}

	@Override
	public boolean retainAll(@NonNull Collection<?> c) {
		return filters.retainAll(c);
	}

	@Override
	public void clear() {
		filters.clear();
	}

	public enum Method {
		And, Or
	}
}