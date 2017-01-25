package nl.camilstaps.rbn.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Record;
import nl.camilstaps.rbn.Speed;
import nl.camilstaps.rbn.filter.AnyOfFilter;
import nl.camilstaps.rbn.filter.CompoundFilter;
import nl.camilstaps.rbn.filter.Filter;
import nl.camilstaps.rbn.filter.RangeFilter;

public final class RBNApplication extends Application {
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
	final AnyOfFilter<Record.Mode> modeFilter = new AnyOfFilter<>(Filter.Field.Mode);
	final AnyOfFilter<Record.Type> typeFilter = new AnyOfFilter<>(Filter.Field.Type);
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
			modeFilter.add(Record.Mode.valueOf(mode));
		for (String type : prefs.getStringSet(PREF_FILTER_TYPE, new ArraySet<String>()))
			typeFilter.add(Record.Type.valueOf(type));
		speedFilter.setRange(
				prefs.getFloat(PREF_FILTER_SPEED + "_min", 0),
				prefs.getFloat(PREF_FILTER_SPEED + "_max", 50));
	}

	public void quickToast(String text) {
		if (toast == null)
			toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		else
			toast.setText(text);
		toast.show();
	}

	public void registerClientListener(final NewRecordListener listener) {
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

	public Filter getMainFilter() {
		return compoundFilter;
	}

	public AnyOfFilter<Band> getBandFilter() {
		return bandFilter;
	}

	public AnyOfFilter<Record.Mode> getModeFilter() {
		return modeFilter;
	}

	public AnyOfFilter<Record.Type> getTypeFilter() {
		return typeFilter;
	}

	public RangeFilter getSpeedFilter() {
		return speedFilter;
	}
}
