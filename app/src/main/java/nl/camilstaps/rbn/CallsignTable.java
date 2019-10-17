package nl.camilstaps.rbn;

import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CallsignTable {
	private static CallsignTable instance;

	public static CallsignTable getInstance() {
		return instance;
	}

	public static void setup(InputStream inputStream) throws IOException {
		if (instance == null)
			instance = new CallsignTable(inputStream);
	}

	private final Trie<Country> trie = new Trie<>();

	private CallsignTable(InputStream inputStream) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String line;

		Country country = null;
		while ((line = br.readLine()) != null) {
			if (line.charAt(0) == '>') {
				String[] parts = line.substring(1).split(";");
				country = new Country(parts[0], parts[2], parts[1].split("-"));
			} else {
				trie.addPrefix(line, country);
			}
		}
	}

	@Nullable
	public Country lookup(Callsign callsign) {
		return trie.getValue(callsign.toString());
	}

	private static class Trie<T> {
		T value = null;
		Trie<T>[] children;

		Trie() {
			this.children = new Trie[37];
		}

		static int index(char c) {
			if (Character.isDigit(c))
				return c-'0';
			else if (Character.isUpperCase(c))
				return c-'A'+10;
			else if (c=='/')
				return 36;
			else
				throw new IllegalArgumentException("Unknown character '" + c +  "' in callsign");
		}

		void addPrefix(String string, T value) {
			addPrefix(string, 0, value);
		}

		void addPrefix(String string, int start, T value) {
			if (start >= string.length()) {
				this.value = value;
				return;
			}

			int i = Trie.index(string.charAt(start));

			if (children[i] == null)
				children[i] = new Trie<T>();

			children[i].addPrefix(string, start+1, value);
		}

		T getValue(String string) {
			return getValue(string, 0, null);
		}

		T getValue(String string, int start, T currentValue) {
			currentValue = this.value == null ? currentValue : this.value;

			if (start >= string.length())
				return currentValue;

			try {
				int i = Trie.index(string.charAt(start));

				if (children[i]==null)
					return currentValue;

				return children[i].getValue(string, start+1, currentValue);
			} catch (IllegalArgumentException e) {
				return currentValue;
			}
		}
	}
}
