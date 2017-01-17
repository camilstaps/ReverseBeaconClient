package nl.camilstaps.rbn.android;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import nl.camilstaps.rbn.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            addPreferencesFromResource(R.xml.preferences);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}