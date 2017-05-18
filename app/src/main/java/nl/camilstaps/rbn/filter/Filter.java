package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Entry;

public interface Filter {
	boolean matches(Entry entry);

	enum Field {
		Dx, DxContinent, De, DeContinent, Band, Mode, Type, Frequency, Speed, Strength
	}
}