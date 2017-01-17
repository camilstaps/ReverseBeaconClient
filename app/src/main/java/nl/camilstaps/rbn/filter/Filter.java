package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Record;

public interface Filter {
    boolean matches(Record record);

    enum Field {
        Dx, De, Band, Mode, Type, Frequency, Speed, Strength
    }
}
