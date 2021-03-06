package nl.camilstaps.rbn.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.util.ArraySet;
import android.widget.Toast;

import java.util.Arrays;

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

public final class RBNApplication extends Application implements SharedPreferences.OnSharedPreferenceChangeListener {
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

		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		speedFilter = new RangeFilter(Filter.Field.Speed, 0, 0, Speed.SpeedUnit.WPM);

		compoundFilter.add(bandFilter);
		compoundFilter.add(modeFilter);
		compoundFilter.add(typeFilter);
		compoundFilter.add(speedFilter);
		compoundFilter.add(deContFilter);
		compoundFilter.add(dxContFilter);

		Resources ress = getApplicationContext().getResources();
		for (String band : prefs.getStringSet(PREF_FILTER_BAND,
				new ArraySet<>(Arrays.asList(ress.getStringArray(R.array.band_values)))))
			bandFilter.add(new Band(Float.valueOf(band) / 100));
		for (String mode : prefs.getStringSet(PREF_FILTER_MODE,
				new ArraySet<>(Arrays.asList(ress.getStringArray(R.array.modes)))))
			modeFilter.add(Entry.Mode.valueOf(mode));
		for (String type : prefs.getStringSet(PREF_FILTER_TYPE,
				new ArraySet<>(Arrays.asList(ress.getStringArray(R.array.types)))))
			typeFilter.add(Entry.Type.valueOf(type));
		speedFilter.setRange(
				prefs.getFloat(PREF_FILTER_SPEED_MIN, 0),
				prefs.getFloat(PREF_FILTER_SPEED_MAX, 50));
		for (String cont : prefs.getStringSet(PREF_FILTER_DE_CONTINENT,
				new ArraySet<>(Arrays.asList(ress.getStringArray(R.array.continent_abbreviations)))))
			deContFilter.add(Country.Continent.fromAbbreviation(cont));
		for (String cont : prefs.getStringSet(PREF_FILTER_DX_CONTINENT,
				new ArraySet<>(Arrays.asList(ress.getStringArray(R.array.continent_abbreviations)))))
			dxContFilter.add(Country.Continent.fromAbbreviation(cont));

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
			quickToast(getResources().getString(R.string.connecting));

			new AsyncTask<Void, Exception, Void>() {
				@Override
				protected Void doInBackground(Void... x) {
					boolean done = false;
					while (!done) {
						done = true;
						try {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(getApplicationContext());
							prefs.registerOnSharedPreferenceChangeListener(RBNApplication.this);
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

				protected void onPostExecute(Void v) {
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (client != null) {
			Resources ress = getApplicationContext().getResources();
			switch (key) {
				case PREF_CALLSIGN:
					client.setCall(sharedPreferences.getString(PREF_CALLSIGN,
						ress.getString(R.string.pref_callsign_default)));
					break;
				case PREF_HOST:
					client.setHost(sharedPreferences.getString(PREF_HOST,
						ress.getString(R.string.pref_host_default)));
					break;
				case PREF_PORT:
					client.setPort(Integer.valueOf(sharedPreferences.getString(PREF_PORT,
							ress.getString(R.string.pref_port_default))));
					break;
			}
		}
	}
}
