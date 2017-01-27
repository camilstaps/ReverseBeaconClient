package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Entry;

public interface Filter {
	boolean matches(Entry entry);

	enum Field {
		Dx, De, Band, Mode, Type, Frequency, Speed, Strength
	}
}