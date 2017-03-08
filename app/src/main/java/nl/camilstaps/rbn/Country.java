package nl.camilstaps.rbn;

public class Country {
	private final String name;
	private final String[] isoCodes;

	public Country(String name, String[] isoCodes) {
		this.name = name;
		this.isoCodes = isoCodes;
	}

	public String getName() {
		return name;
	}

	public String[] getIsoCodes() {
		return isoCodes;
	}
}