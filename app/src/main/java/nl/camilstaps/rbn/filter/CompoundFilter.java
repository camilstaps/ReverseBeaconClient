package nl.camilstaps.rbn.filter;

import java.util.List;

import nl.camilstaps.rbn.Record;

public class CompoundFilter implements Filter {
    private final List<Filter> filters;
    private final Method method;

    public CompoundFilter(List<Filter> filters, Method method) {
        this.filters = filters;
        this.method = method;
    }

    @Override
    public boolean matches(Record record) {
        switch (method) {
            case And:
                for (Filter filter : filters)
                    if (!filter.matches(record))
                        return false;
                return true;
            case Or:
                for (Filter filter : filters)
                    if (filter.matches(record))
                        return true;
                return false;
            default:
                throw new IllegalArgumentException("Unknown method " + method + " for CompoundFilter.");
        }
    }

    enum Method {
        And, Or
    }
}