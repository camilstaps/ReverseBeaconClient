package nl.camilstaps.android;

import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

public final class Util {
	private static Toast toast;

	// http://stackoverflow.com/a/37905107/1544337
	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html){
		Spanned result;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
		} else {
			result = Html.fromHtml(html);
		}
		return result;
	}
}
