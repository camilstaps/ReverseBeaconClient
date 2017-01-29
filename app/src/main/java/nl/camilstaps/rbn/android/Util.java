package nl.camilstaps.rbn.android;

import android.content.Context;

import nl.camilstaps.rbn.Callsign;

public class Util {
	public static int getFlagResource(Context context, Callsign callsign) {
		return context.getResources().getIdentifier(
				"flag_" + callsign.getCountry().toString().toLowerCase(),
				"drawable", "nl.camilstaps.rbn");
	}
}
