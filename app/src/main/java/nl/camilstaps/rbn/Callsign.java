package nl.camilstaps.rbn;

public final class Callsign {
	private final String callsign;
	private String description;

	public Callsign (String callsign) {
		this.callsign = callsign;
	}

	public String toString() {
		return callsign;
	}

	public Country getCountry() {
		return CallsignTable.getInstance().lookup(this);
	}

	public String getDescription(String defaultValue) {
		if (description == null) {
			Country country = CallsignTable.getInstance().lookup(this);
			description = country != null ? country.getName() : defaultValue;
		}

		return description;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Callsign && this.callsign.equals(((Callsign) obj).callsign);
	}
}
