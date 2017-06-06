package nl.camilstaps.rbn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import nl.camilstaps.util.Logger;

public class CallsignTable {
	private final Collection<TableEntry<Collection<String>, Country>> table = new ArrayList<>();

	private static CallsignTable instance;

	public static CallsignTable getInstance() {
		return instance;
	}

	public static void setup(InputStream inputStream) throws IOException {
		if (instance == null)
			instance = new CallsignTable(inputStream);
	}

	private CallsignTable(InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		Logger.getInstance().addEntry("CallsignTable constructor");

		Collection<String> prefixes = new ArrayList<>();
		while ((line = br.readLine()) != null) {
			if (line.charAt(0) == '>') {
				String[] parts = line.substring(1).split(";");
				Country country = new Country(parts[0], parts[2], parts[1].split("-"));
				table.add(new TableEntry<>(prefixes, country));
				prefixes = new ArrayList<>();
			} else {
				prefixes.add(line);
			}
		}

		Logger.getInstance().addEntry("CallsignTable constructor finishing");
	}

	public Country lookup(Callsign callsign) {
		for (TableEntry<Collection<String>, Country> entry : table)
			for (String prefix : entry.prefixes)
				if (matches(prefix, callsign.toString()))
					return entry.country;
		return null;
	}

	private static boolean matches(String prefix, String callsign) {
		return callsign.length() >= prefix.length() &&
				callsign.substring(0, prefix.length()).equals(prefix);
	}

	private static class TableEntry<A,B> {
		final A prefixes;
		final B country;

		TableEntry(A prefixes, B country) {
			this.prefixes = prefixes;
			this.country = country;
		}
	}
}
