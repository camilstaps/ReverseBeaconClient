package nl.camilstaps.android;

import android.app.Activity;
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

    public static void quickToast(final Activity activity, final String text) {
        if (activity == null)
            return;

        if (toast == null)
            toast = Toast.makeText(activity.getApplicationContext(), "", Toast.LENGTH_LONG);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast != null) {
                    toast.setText(text);
                    toast.show();
                }
            }
        });
    }
}
