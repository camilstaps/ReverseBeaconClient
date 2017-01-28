package nl.camilstaps.rbn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CallsignTable {
	private final List<Pair<String, String>> table = new ArrayList<>();
	private final String defaultValue;

	private static CallsignTable instance;

	public static CallsignTable getInstance() {
		return instance;
	}

	public static CallsignTable getInstance(InputStream inputStream, String defaultValue)
			throws IOException {
		if (instance == null)
			instance = new CallsignTable(inputStream, defaultValue);

		return instance;
	}

	private CallsignTable(InputStream inputStream, String defaultValue) throws IOException {
		this.defaultValue = defaultValue;

		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		while ((line = br.readLine()) != null) {
			String[] parts = line.split("\\|");
			String[] prefixes = parts[0].split(" ");

			for (String prefix : prefixes)
				table.add(new Pair<>(prefix, parts[1]));
		}
	}

	public String lookup(Callsign callsign) {
		for (Pair<String, String> entry : table)
			if (matches(entry.first, callsign.toString()))
				return entry.second;
		return defaultValue;
	}

	private static boolean matches(String pattern, String callsign) {
		int plen = pattern.length();
		int clen = callsign.length();

		int ci = 0;
		for (int pi = 0; pi < plen; pi++) {
			if (ci >= clen)
				return false;

			switch (pattern.charAt(pi)) {
				case '#': if (!Character.isDigit(callsign.charAt(ci))) return false; break;
				case '?': if (!Character.isLetter(callsign.charAt(ci))) return false; break;
				case '%': if (!Character.isLetterOrDigit(callsign.charAt(ci))) return false; break;
				case ']': throw new RuntimeException();
				case '[':
					boolean canContinue = false;
					char c = callsign.charAt(ci);

					for (; pattern.charAt(pi) != ']'; pi++) {
						if (pattern.charAt(pi + 1) == '-') {
							if (pattern.charAt(pi) <= c && c <= pattern.charAt(pi + 2)) {
								canContinue = true;
								break;
							} else {
								pi += 2;
							}
						} else if (pattern.charAt(pi) == c) {
							canContinue = true;
							break;
						}
					}

					if (!canContinue)
						return false;

					while (pattern.charAt(pi) != ']')
						pi++;

					break;
				default:  if (pattern.charAt(pi) != callsign.charAt(ci)) return false;
			}

			ci++;
		}

		return true;
	}

	private static class Pair<A,B> {
		final A first;
		final B second;

		Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}
}
