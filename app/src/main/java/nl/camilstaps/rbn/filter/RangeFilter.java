package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Record;
import nl.camilstaps.rbn.Speed;

public class RangeFilter implements Filter {
    final private float min, max;
    final private Field field;
    private Speed.SpeedUnit speedUnit;

    public RangeFilter(Field field, float min, float max) {
        this.field = field;
        switch (field) {
            case Band:
            case Frequency:
            case Strength:
                this.min = min;
                this.max = max;
            default:
                throw new IllegalArgumentException("Could not create range filter for " + field + ".");
        }
    }

    public RangeFilter(Field field, float min, float max, Speed.SpeedUnit unit) {
        if (field != Field.Speed)
            throw new IllegalArgumentException("Can only create this range filter for Speed.");
        this.field = field;
        this.min = min;
        this.max = max;
        speedUnit = unit;
    }

    @Override
    public boolean matches(Record record) {
        switch (field) {
            case Band:
                float wavelength = record.getBand().getWavelength();
                return min <= wavelength && wavelength <= max;
            case Frequency:
                return min <= record.getFrequency() && record.getFrequency() <= max;
            case Speed:
                Speed speed = record.getSpeed();
                if (speed.unit == speedUnit)
                    return min <= speed.value && speed.value <= max;
                else
                    return true;
            case Strength:
                return min <= record.getStrength() && record.getStrength() <= max;
            default:
                return false;
        }
    }
}
