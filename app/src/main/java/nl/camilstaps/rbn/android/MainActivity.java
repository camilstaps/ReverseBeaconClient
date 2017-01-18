package nl.camilstaps.rbn.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import nl.camilstaps.rbn.R;

public class MainActivity extends AppCompatActivity {
	private DrawerLayout drawer;
	private ListView drawerList;

	private Fragment currentFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
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
				fm.beginTransaction().add(R.id.activity_main_content, loggingFragment, "log").commit();
				currentFragment = loggingFragment;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		FragmentManager fm = getFragmentManager();
		Fragment fragment;
		String[] tags = new String[] {"log", "pref"};
		boolean add = false;

		switch (position) {
			case 0:
				fragment = fm.findFragmentByTag("log");
				if (fragment == null) {
					fragment = new LoggingFragment();
					add = true;
				}
				break;
			case 1:
				fragment = fm.findFragmentByTag("pref");
				if (fragment == null) {
					fragment = new SettingsFragment();
					add = true;
				}
				break;
			default:
				throw new IllegalArgumentException("How did you do that!?");
		}

		FragmentTransaction ft = fm.beginTransaction();

		for (String tag : tags) {
			Fragment frag = fm.findFragmentByTag(tag);
			if (frag != null && frag != fragment)
				ft.hide(frag);
		}

		if (add)
			ft.add(R.id.activity_main_content, fragment, tags[position]);
		else
			ft.show(fragment);

		ft.addToBackStack(null).commit();

		currentFragment = fragment;

		drawer.closeDrawer(drawerList);
	}
}
