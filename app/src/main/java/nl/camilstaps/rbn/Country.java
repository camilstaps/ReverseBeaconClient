package nl.camilstaps.rbn;

public class Country {
	private final String name;
	private final Continent continent;
	private final String[] isoCodes;

	public Country(String name, String continent, String[] isoCodes) {
		this.name = name;
		this.continent = Continent.fromAbbreviation(continent);
		this.isoCodes = isoCodes;
	}

	public String getName() {
		return name;
	}

	public Continent getContinent() {
		return continent;
	}

	public String[] getIsoCodes() {
		return isoCodes;
	}

	public enum Continent {
		Africa, Asia, Europe, NorthAmerica, Oceania, SouthAmerica;

		public static Continent fromAbbreviation(String abbreviation) {
			switch (abbreviation) {
				case "AF": return Africa;
				case "AS": return Asia;
				case "EU": return Europe;
				case "NA": return NorthAmerica;
				case "OC": return Oceania;
				case "SA": return SouthAmerica;
			}
			return null;
		}
	}
}