package nl.camilstaps.rbn.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import nl.camilstaps.rbn.R;

public class MainActivity extends AppCompatActivity {
	private DrawerLayout drawer;
	private ListView drawerList;

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
			drawerList = (ListView) findViewById(R.id.activity_main_drawer_list);
			drawerList.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles));
			drawerList.setOnItemClickListener(new DrawerItemClickListener());

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

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectDrawerItem(position);
		}
	}

	private void selectDrawerItem(int position) {
		FragmentManager fm = getFragmentManager();
		String[] tags = new String[] {"log", "filter", "pref"};
		boolean add = false;
		Fragment fragment = fm.findFragmentByTag(tags[position]);

		if (fragment == null) {
			switch (position) {
				case 0: fragment = new LoggingFragment(); break;
				case 1: fragment = new FilterFragment(); break;
				case 2: fragment = new SettingsFragment(); break;
			}
			add = true;
		}

		FragmentTransaction ft = fm.beginTransaction();

		for (String tag : tags) {
			Fragment frag = fm.findFragmentByTag(tag);
			if (frag != null && frag != fragment && !frag.isHidden())
				ft.hide(frag);
		}

		if (add)
			ft.add(R.id.activity_main_content, fragment, tags[position]);
		else
			ft.show(fragment);

		ft.addToBackStack(null).commit();

		drawer.closeDrawer(drawerList);
	}
}
