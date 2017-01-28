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
		return Country.fromCallsign(this);
	}

	public String getDescription() {
		if (description == null)
			description = CallsignTable.getInstance().lookup(this);

		return description;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Callsign && this.callsign.equals(((Callsign) obj).callsign);
	}
}
