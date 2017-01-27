package nl.camilstaps.rbn.android;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import nl.camilstaps.android.MultiSelectListPreference;
import nl.camilstaps.android.RangePreference;
import nl.camilstaps.rbn.Band;
import nl.camilstaps.rbn.Entry;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.filter.AnyOfFilter;
import nl.camilstaps.rbn.filter.RangeFilter;

public class FilterFragment extends PreferenceFragment
		implements SharedPreferences.OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.filters);

		onSharedPreferenceChanged(null, "");

		((MultiSelectListPreference) findPreference(RBNApplication.PREF_FILTER_BAND))
				.setSummaryLikeEntries();
		((MultiSelectListPreference) findPreference(RBNApplication.PREF_FILTER_MODE))
				.setSummaryLikeEntries();
		((MultiSelectListPreference) findPreference(RBNApplication.PREF_FILTER_TYPE))
				.setSummaryLikeEntries();
		((RangePreference) findPreference(RBNApplication.PREF_FILTER_SPEED))
				.setSummaryLikeValue();
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		RBNApplication app = (RBNApplication) getActivity().getApplication();
		switch (key) {
			case RBNApplication.PREF_FILTER_BAND: {
				MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(key);
				pref.setSummaryLikeEntries();
				AnyOfFilter<Band> filter = app.getBandFilter();
				filter.clear();
				for (String band : pref.getValues())
					filter.add(new Band(Float.valueOf(band) / 100));
				break; }
			case RBNApplication.PREF_FILTER_MODE: {
				MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(key);
				pref.setSummaryLikeEntries();
				AnyOfFilter<Entry.Mode> filter = app.getModeFilter();
				filter.clear();
				for (String mode : pref.getValues())
					filter.add(Entry.Mode.valueOf(mode));
				break; }
			case RBNApplication.PREF_FILTER_TYPE: {
				MultiSelectListPreference pref = (MultiSelectListPreference) findPreference(key);
				pref.setSummaryLikeEntries();
				AnyOfFilter<Entry.Type> filter = app.getTypeFilter();
				filter.clear();
				for (String type : pref.getValues())
					filter.add(Entry.Type.valueOf(type));
				break; }
			case RBNApplication.PREF_FILTER_SPEED_MIN:
			case RBNApplication.PREF_FILTER_SPEED_MAX: {
				try {
					RangePreference pref = (RangePreference)
							findPreference(RBNApplication.PREF_FILTER_SPEED);
					pref.setSummaryLikeValue();
					RangeFilter filter = app.getSpeedFilter();
					filter.setRange(pref.getMinValue(), pref.getMaxValue());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}
}
