package nl.camilstaps.rbn;

import java.io.Serializable;

public final class Callsign implements Serializable {
	private String callsign;

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
		if (!(obj instanceof Callsign))
			return false;
		return this.callsign == ((Callsign) obj).callsign;
	}
}
