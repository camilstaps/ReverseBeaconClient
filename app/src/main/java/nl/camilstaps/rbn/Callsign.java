package nl.camilstaps.rbn;

import java.io.Serializable;

public final class Callsign implements Serializable {
	private final String callsign;

	public Callsign (String callsign) {
		this.callsign = callsign;
	}

	public String toString() {
		return callsign;
	}

	public Country getCountry() {
		return Country.fromCallsign(this);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Callsign && this.callsign.equals(((Callsign) obj).callsign);
	}
}
