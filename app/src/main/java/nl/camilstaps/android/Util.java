package nl.camilstaps.android;

import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import org.xml.sax.XMLReader;

public final class Util {
	private static Toast toast;

	// http://stackoverflow.com/a/37905107/1544337
	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html){
		Spanned res;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			res = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY, null, new ExtendedTagHandler());
		} else {
			res = Html.fromHtml(html, null, new ExtendedTagHandler());
		}
		return res;
	}

	public static String formatFloat(float f) {
		if (f == (int) f)
			return String.format("%d", (int) f);
		else
			return String.format("%s", f);
	}

	// http://stackoverflow.com/a/9546532/1544337
	private static class ExtendedTagHandler implements Html.TagHandler {
		boolean first= true;
		String parent=null;
		int index=1;

		@Override
		public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
			if(tag.equals("ul")) parent="ul";
			else if(tag.equals("ol")) parent="ol";
			if(tag.equals("li")){
				if(parent.equals("ul")){
					if(first){
						output.append("\n\t•");
						first= false;
					}else{
						first = true;
					}
				}
				else{
					if(first){
						output.append(String.format("\n\t%d. ", index));
						first= false;
						index++;
					}else{
						first = true;
					}
				}
			}
		}
	}
}
