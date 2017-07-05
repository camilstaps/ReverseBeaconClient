package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.Speed;

public class RangeFilter extends Filter {
	private float min, max;
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

	public float getMin() {
		return min;
	}

	public float getMax() {
		return max;
	}

	public void setRange(float min, float max) {
		this.min = min;
		this.max = max;
	}

	@Override
	protected boolean realMatches(Entry entry) {
		switch (field) {
			case Band:
				float wavelength = entry.getBand().getWavelength();
				return min <= wavelength && wavelength <= max;
			case Frequency:
				float frequency = entry.getAvgFrequency();
				return min <= frequency && frequency <= max;
			case Speed:
				Speed speed = entry.getAvgSpeed();
				return speed.unit != speedUnit || min <= speed.value && speed.value <= max;
			case Strength:
				return min <= entry.getMaxStrength() && entry.getMinStrength() <= max;
			default:
				return false;
		}
	}
}