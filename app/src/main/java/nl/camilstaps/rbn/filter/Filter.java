package nl.camilstaps.rbn.filter;

import nl.camilstaps.rbn.Entry;

public abstract class Filter {
	private boolean enabled = true;

	protected abstract boolean realMatches(Entry entry);

	public boolean matches(Entry entry) {
		return !enabled || realMatches(entry);
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public enum Field {
		Dx, DxContinent, De, DeContinent, Band, Mode, Type, Frequency, Speed, Strength
	}
}