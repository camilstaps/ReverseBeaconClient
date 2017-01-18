package nl.camilstaps.rbn.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.widget.Toast;

import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.R;

public final class RBNApplication extends Application {
	private Toast toast;
	private Client client;

	public void quickToast(String text) {
		if (toast == null)
			toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		else
			toast.setText(text);
		toast.show();
	}

	public void registerClientListener(final Client.NewRecordListener listener) {
		if (client == null) {
			quickToast("Connecting...");

			new AsyncTask<Void, Exception, Void>() {
				@Override
				protected Void doInBackground(Void... x) {
					try {
						SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
						Resources ress = getApplicationContext().getResources();
						client = new Client(
								prefs.getString("callsign", ress.getString(R.string.pref_callsign_default)),
								prefs.getString("host", ress.getString(R.string.pref_host_default)),
								Integer.valueOf(prefs.getString("port", ress.getString(R.string.pref_port_default))));
						client.register(listener);
					} catch (Exception e) {
						e.printStackTrace();
						publishProgress(e);
					}
					return null;
				}

				protected void onProgressUpdate(Exception... es) {
					for (Exception e : es)
						quickToast(e.getMessage());
				}

				protected void onPostExecute(Void result) {
					quickToast("Listening...");
				}
			}.execute();
		} else {
			client.register(listener);
		}
	}
}
