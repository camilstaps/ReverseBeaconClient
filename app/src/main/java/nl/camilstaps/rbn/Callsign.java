package nl.camilstaps.rbn;

import android.support.annotation.Nullable;

public final class Callsign {
	private final String callsign;
	private String description;

	public static boolean isValid(String callsign) {
		return callsign.matches("[a-zA-Z]{1,2}\\d{1,4}[a-zA-Z]{0,4}")
				|| callsign.matches("\\d[a-zA-Z]{0,2}\\d{0,4}[a-zA-Z]{1,4}");
	}

	public Callsign (String callsign) {
		this.callsign = callsign;
	}

	public String toString() {
		return callsign;
	}

	@Nullable
	public Country getCountry() {
		return CallsignTable.getInstance().lookup(this);
	}

	public String getDescription(String defaultValue) {
		if (description == null) {
			Country country = getCountry();
			description = country != null ? country.getName() : defaultValue;
		}

		return description;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Callsign && this.callsign.equals(((Callsign) obj).callsign);
	}
}
