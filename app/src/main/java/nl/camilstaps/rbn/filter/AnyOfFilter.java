package nl.camilstaps.rbn.filter;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import nl.camilstaps.rbn.Record;

public class AnyOfFilter<T> implements Filter {
    private final List<T> values;
    private final Field field;

    public AnyOfFilter(Field field, List<T> values) {
        this.values = values;
        this.field = field;
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
}
