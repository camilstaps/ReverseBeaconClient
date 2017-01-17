package nl.camilstaps.rbn.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import nl.camilstaps.rbn.R;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;
    private ListView drawerList;

    private LoggingFragment loggingFragment;
    private SettingsFragment settingsFragment;
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

            loggingFragment = new LoggingFragment();
            loggingFragment.start(this);
            settingsFragment = new SettingsFragment();

            getFragmentManager().beginTransaction()
                    .add(R.id.activity_main_content, loggingFragment).commit();
            currentFragment = loggingFragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = loggingFragment;
                break;
            case 1:
                fragment = settingsFragment;
                break;
            default:
                throw new IllegalArgumentException("How did you do that!?");
        }

        if (fragment != currentFragment) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.activity_main_content, fragment)
                    .addToBackStack(null)
                    .commit();
            currentFragment = fragment;
        }

        drawer.closeDrawer(drawerList);
    }
}
