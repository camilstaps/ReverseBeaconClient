package nl.camilstaps.android;

import android.text.Html;
import android.text.Spanned;

public final class Util {
	// http://stackoverflow.com/a/37905107/1544337
	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html){
		Spanned res;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			res = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
		} else {
			res = Html.fromHtml(html);
		}
		return res;
	}

	public static String formatFloat(float f) {
		if (f == (int) f)
			return String.format("%d", (int) f);
		else
			return String.format("%s", f);
	}
}