package nl.camilstaps.rbn.android;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import nl.camilstaps.rbn.R;

public class MainActivity extends AppCompatActivity {
	private DrawerLayout drawer;

	private boolean openedWelcome = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		openFragments();
	}

	public void openFragments() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String call = prefs.getString(RBNApplication.PREF_CALLSIGN, "");

		if (call.matches("[a-zA-Z]{1,2}\\d{1,4}[a-zA-Z]{0,4}")
				|| call.matches("\\d[a-zA-Z]{0,2}\\d{0,4}[a-zA-Z]{1,4}")) {
			setContentView(R.layout.activity_main);

			String[] titles = getResources().getStringArray(R.array.side_nav_titles);
			drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer);
			NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_drawer_navigation);
			navigationView.setNavigationItemSelectedListener(new DrawerItemClickListener());

			FragmentManager fm = getFragmentManager();
			Fragment loggingFragment = fm.findFragmentByTag("log");

			if (loggingFragment == null) {
				loggingFragment = new LoggingFragment();
				fm.beginTransaction()
						.add(R.id.activity_main_content, loggingFragment, "log").commit();
			}
		} else {
			if (!openedWelcome) {
				setContentView(R.layout.activity_welcome);

				final EditText callsignEditText = (EditText) findViewById(R.id.welcome_callsign);
				callsignEditText.setText(call);

				findViewById(R.id.welcome_button).setOnClickListener(new View.OnClickListener() {
					@SuppressLint("CommitPrefEdits")
					@Override
					public void onClick(View v) {
						prefs.edit()
								.putString(RBNApplication.PREF_CALLSIGN,
										callsignEditText.getText().toString())
								.commit();

						openFragments();
					}
				});
			}

			if (!call.equals(""))
				((RBNApplication) getApplication()).quickToast(String.format(
						getResources().getString(R.string.warning_invalid_callsign), call));

			openedWelcome = true;
		}
	}

	private class DrawerItemClickListener implements NavigationView.OnNavigationItemSelectedListener {
		@Override
		public boolean onNavigationItemSelected(MenuItem menuItem) {
			FragmentManager fm = getFragmentManager();
			boolean add = false;

			String tag;
			switch (menuItem.getItemId()) {
				case R.id.drawer_log:      tag = "log"; break;
				case R.id.drawer_filters:  tag = "filters"; break;
				case R.id.drawer_settings: tag = "settings"; break;
				case R.id.drawer_info:     tag = "info"; break;
				default: throw new RuntimeException();
			}

			Fragment fragment = fm.findFragmentByTag(tag);

			if (fragment == null) {
				switch (menuItem.getItemId()) {
					case R.id.drawer_log:      fragment = new LoggingFragment(); break;
					case R.id.drawer_filters:  fragment = new FilterFragment(); break;
					case R.id.drawer_settings: fragment = new SettingsFragment(); break;
					case R.id.drawer_info:     fragment = new InfoFragment(); break;
				}
				add = true;
			}

			FragmentTransaction ft = fm.beginTransaction();

			for (String t : new String[] {"log", "filters", "settings", "info"}) {
				Fragment frag = fm.findFragmentByTag(t);
				if (frag != null && frag != fragment && !frag.isHidden())
					ft.hide(frag);
			}

			if (add)
				ft.add(R.id.activity_main_content, fragment, tag);
			else
				ft.show(fragment);

			ft.addToBackStack(null).commit();

			drawer.closeDrawers();

			return true;
		}
	}
}
