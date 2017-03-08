package nl.camilstaps.rbn.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.CallsignTable;
import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Speed;
import nl.camilstaps.rbn.filter.AnyOfFilter;
import nl.camilstaps.rbn.filter.CompoundFilter;
import nl.camilstaps.rbn.filter.Filter;
import nl.camilstaps.rbn.filter.RangeFilter;

public final class RBNApplication extends Application {
	public static final String PREF_CALLSIGN = "callsign";
	public static final String PREF_HOST = "host";
	public static final String PREF_PORT = "port";
	public static final String PREF_FILTER_BAND = "filter_band";
	public static final String PREF_FILTER_MODE = "filter_mode";
	public static final String PREF_FILTER_TYPE = "filter_type";
	public static final String PREF_FILTER_SPEED = "filter_speed";
	public static final String PREF_FILTER_SPEED_MIN = "filter_speed_min";
	public static final String PREF_FILTER_SPEED_MAX = "filter_speed_max";

	private Toast toast;
	private Client client;

	SharedPreferences prefs;

	final CompoundFilter compoundFilter = new CompoundFilter(CompoundFilter.Method.And);
	final AnyOfFilter<Band> bandFilter = new AnyOfFilter<>(Filter.Field.Band);
	final AnyOfFilter<Entry.Mode> modeFilter = new AnyOfFilter<>(Filter.Field.Mode);
	final AnyOfFilter<Entry.Type> typeFilter = new AnyOfFilter<>(Filter.Field.Type);
	RangeFilter speedFilter;

	@Override
	public void onCreate() {
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		speedFilter = new RangeFilter(Filter.Field.Speed, 0, 0, Speed.SpeedUnit.WPM);

		compoundFilter.add(bandFilter);
		compoundFilter.add(modeFilter);
		compoundFilter.add(typeFilter);
		compoundFilter.add(speedFilter);

		for (String band : prefs.getStringSet(PREF_FILTER_BAND, new ArraySet<String>()))
			bandFilter.add(new Band(Float.valueOf(band) / 100));
		for (String mode : prefs.getStringSet(PREF_FILTER_MODE, new ArraySet<String>()))
			modeFilter.add(Entry.Mode.valueOf(mode));
		for (String type : prefs.getStringSet(PREF_FILTER_TYPE, new ArraySet<String>()))
			typeFilter.add(Entry.Type.valueOf(type));
		speedFilter.setRange(
				prefs.getFloat(PREF_FILTER_SPEED_MIN, 0),
				prefs.getFloat(PREF_FILTER_SPEED_MAX, 50));

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					CallsignTable.getInstance(getResources().openRawResource(R.raw.cty));
				} catch (Exception e) {
					slowToast(getResources().getString(R.string.error_load_callsigns));
				}
				return null;
			}
		}.execute();
	}

	public void quickToast(String text) {
		if (toast == null)
			toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		else
			toast.setText(text);
		toast.show();
	}

	public void slowToast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	public void registerClientListener(final NewRecordListener listener) {
		if (client == null) {
			quickToast("Connecting...");

			new AsyncTask<Void, Exception, Void>() {
				@Override
				protected Void doInBackground(Void... x) {
					try {
						SharedPreferences prefs = PreferenceManager
								.getDefaultSharedPreferences(getApplicationContext());
						Resources ress = getApplicationContext().getResources();
						client = new Client(
								prefs.getString(PREF_CALLSIGN,
										ress.getString(R.string.pref_callsign_default)),
								prefs.getString(PREF_HOST,
										ress.getString(R.string.pref_host_default)),
								Integer.valueOf(prefs.getString(PREF_PORT,
										ress.getString(R.string.pref_port_default))));
						client.register(listener);
						client.setFilter(compoundFilter);
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

	public AnyOfFilter<Band> getBandFilter() {
		return bandFilter;
	}

	public AnyOfFilter<Entry.Mode> getModeFilter() {
		return modeFilter;
	}

	public AnyOfFilter<Entry.Type> getTypeFilter() {
		return typeFilter;
	}

	public RangeFilter getSpeedFilter() {
		return speedFilter;
	}
}
