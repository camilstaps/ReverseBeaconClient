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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.telecom.Call;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import nl.camilstaps.android.*;
import nl.camilstaps.rbn.Callsign;
import nl.camilstaps.rbn.R;
import nl.camilstaps.rbn.RecordCounter;

public class MainActivity extends AppCompatActivity {
	private DrawerLayout drawer;
	private ActionBarDrawerToggle drawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String call = prefs.getString(RBNApplication.PREF_CALLSIGN, "");

		if (!Callsign.isValid(call)) {
			openWelcome();
		} else {
			openFragments();
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (drawerToggle != null)
			drawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
	}

	private void openFragments() {
		setContentView(R.layout.activity_main);

		drawer = (DrawerLayout) findViewById(R.id.activity_main_drawer);
		NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_drawer_navigation);
		navigationView.setNavigationItemSelectedListener(new DrawerItemClickListener());
		drawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);
		drawer.addDrawerListener(drawerToggle);

		FragmentManager fm = getFragmentManager();
		Fragment loggingFragment = fm.findFragmentByTag("log");

		if (loggingFragment == null) {
			loggingFragment = new LoggingFragment();
			loggingFragment.setRetainInstance(true);
			fm.beginTransaction()
					.add(R.id.activity_main_content, loggingFragment, "log").commit();
		}

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		drawerToggle.syncState();
	}

	private void openWelcome() {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		final String call = prefs.getString(RBNApplication.PREF_CALLSIGN, "");

		setContentView(R.layout.activity_welcome);

		final EditText callsignEditText = (EditText) findViewById(R.id.welcome_callsign);
		callsignEditText.setText(call);

		findViewById(R.id.welcome_button).setOnClickListener(new View.OnClickListener() {
			@SuppressLint("CommitPrefEdits")
			@Override
			public void onClick(View v) {
				String call = callsignEditText.getText().toString().trim();

				if (!Callsign.isValid(call)) {
					((RBNApplication) getApplication()).quickToast(String.format(
							getResources().getString(R.string.warning_invalid_callsign), call));
				} else {
					prefs.edit()
							.putString(RBNApplication.PREF_CALLSIGN,
									callsignEditText.getText().toString().trim())
							.commit();

					openInstructions();
				}
			}
		});

		if (!call.equals(""))
			((RBNApplication) getApplication()).quickToast(String.format(
					getResources().getString(R.string.warning_invalid_callsign), call));
	}

	private void openInstructions() {
		setContentView(R.layout.activity_welcome_instructions);
		TextView textView = (TextView) findViewById(R.id.welcome_instructions_text);
		textView.setText(nl.camilstaps.android.Util.fromHtml(getResources().getString(R.string.text_info)));
		textView.setMovementMethod(new ScrollingMovementMethod());
		findViewById(R.id.welcome_instructions_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openFragments();
			}
		});
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
				fragment.setRetainInstance(true);
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
