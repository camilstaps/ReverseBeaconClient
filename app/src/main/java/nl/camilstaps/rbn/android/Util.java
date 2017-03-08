package nl.camilstaps.rbn.android;

import android.content.Context;

import nl.camilstaps.rbn.Callsign;
import nl.camilstaps.rbn.Country;

public class Util {
	public static int getFlagResource(Context context, Callsign callsign) {
		Country country = callsign.getCountry();
		String[] isoCodes = {};
		if (country != null)
			isoCodes = country.getIsoCodes();

		for (String iso : isoCodes) {
			int identifier = context.getResources().getIdentifier(
					"flag_" + iso.toLowerCase(), "drawable", "nl.camilstaps.rbn");
			if (identifier != 0)
				return identifier;
		}

		return context.getResources().getIdentifier(
				"flag__generic", "drawable", "nl.camilstaps.rbn");
	}
}
