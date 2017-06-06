package nl.camilstaps.rbn.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.CallsignTable;
import nl.camilstaps.rbn.Client;
import nl.camilstaps.rbn.Country;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.NewRecordListener;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.Speed;
import nl.camilstaps.rbn.filter.AnyOfFilter;
import nl.camilstaps.rbn.filter.CompoundFilter;
import nl.camilstaps.rbn.filter.Filter;
import nl.camilstaps.rbn.filter.RangeFilter;
import nl.camilstaps.util.Logger;

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
	public static final String PREF_FILTER_DE_CONTINENT = "filter_de_continent";
	public static final String PREF_FILTER_DX_CONTINENT = "filter_dx_continent";

	private Toast toast;
	private Client client;

	SharedPreferences prefs;

	final CompoundFilter compoundFilter = new CompoundFilter(CompoundFilter.Method.And);
	final AnyOfFilter<Band> bandFilter = new AnyOfFilter<>(Filter.Field.Band);
	final AnyOfFilter<Entry.Mode> modeFilter = new AnyOfFilter<>(Filter.Field.Mode);
	final AnyOfFilter<Entry.Type> typeFilter = new AnyOfFilter<>(Filter.Field.Type);
	final AnyOfFilter<Country.Continent> deContFilter = new AnyOfFilter<>(Filter.Field.DeContinent);
	final AnyOfFilter<Country.Continent> dxContFilter = new AnyOfFilter<>(Filter.Field.DxContinent);
	RangeFilter speedFilter;

	@Override
	public void onCreate() {
		super.onCreate();

		Logger.getInstance().setAsUncaughtExceptionHandler();
		Logger.getInstance().addEntry("RBNApplication onCreate()");

		try {
			PackageManager pacman = getPackageManager();
			String version = pacman.getPackageInfo(getPackageName(), 0).versionName;
			Logger.getInstance().addEntry("Info: v. " + version);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		speedFilter = new RangeFilter(Filter.Field.Speed, 0, 0, Speed.SpeedUnit.WPM);

		compoundFilter.add(bandFilter);
		compoundFilter.add(modeFilter);
		compoundFilter.add(typeFilter);
		compoundFilter.add(speedFilter);
		compoundFilter.add(deContFilter);
		compoundFilter.add(dxContFilter);

		for (String band : prefs.getStringSet(PREF_FILTER_BAND, new ArraySet<String>()))
			bandFilter.add(new Band(Float.valueOf(band) / 100));
		for (String mode : prefs.getStringSet(PREF_FILTER_MODE, new ArraySet<String>()))
			modeFilter.add(Entry.Mode.valueOf(mode));
		for (String type : prefs.getStringSet(PREF_FILTER_TYPE, new ArraySet<String>()))
			typeFilter.add(Entry.Type.valueOf(type));
		speedFilter.setRange(
				prefs.getFloat(PREF_FILTER_SPEED_MIN, 0),
				prefs.getFloat(PREF_FILTER_SPEED_MAX, 50));
		for (String cont : prefs.getStringSet(PREF_FILTER_DE_CONTINENT, new ArraySet<String>()))
			deContFilter.add(Country.Continent.fromAbbreviation(cont));
		for (String cont : prefs.getStringSet(PREF_FILTER_DX_CONTINENT, new ArraySet<String>()))
			dxContFilter.add(Country.Continent.fromAbbreviation(cont));

		Logger.getInstance().addEntry("RBNApplication onCreate(): starting CallsignTable setup");

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					CallsignTable.setup(getResources().openRawResource(R.raw.cty));
				} catch (Exception e) {
					slowToast(getResources().getString(R.string.error_load_callsigns));
				}
				return null;
			}
		}.execute();

		Logger.getInstance().addEntry("RBNApplication onCreate(): finishing");
	}

	public void quickToast(String text) {
		Logger.getInstance().addEntry("RBNApplication quickToast(): '" + text + "'");
		if (toast == null)
			toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
		else
			toast.setText(text);
		toast.show();
	}

	public void slowToast(String text) {
		Logger.getInstance().addEntry("RBNApplication slowToast(): '" + text + "'");
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
	}

	public void registerClientListener(final NewRecordListener listener) {
		Logger.getInstance().addEntry("RBNApplication registerClientListener(): " + listener.toString());
		if (client == null) {
			quickToast(getResources().getString(R.string.connecting));

			new AsyncTask<Void, Exception, Void>() {
				@Override
				protected Void doInBackground(Void... x) {
					boolean done = false;
					while (!done) {
						Logger.getInstance().addEntry("RBNApplication Client startup iteration");
						done = true;
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
							done = false;
						}

						if (!done) {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							}

							publishProgress(new Exception(
									getResources().getString(R.string.reconnecting)));

							try {
								Thread.sleep(400); // Extra delay to show reconnecting message properly
							} catch (InterruptedException e) {
							}
						}
					}
					return null;
				}

				protected void onProgressUpdate(Exception... es) {
					for (Exception e : es)
						quickToast(e.getMessage());
				}

				protected void onPostExecute(Void _) {
					quickToast(getResources().getString(R.string.listening));
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

	public AnyOfFilter<Country.Continent> getDeContinentFilter() {
		return deContFilter;
	}

	public AnyOfFilter<Country.Continent> getDxContinentFilter() {
		return dxContFilter;
	}
}
