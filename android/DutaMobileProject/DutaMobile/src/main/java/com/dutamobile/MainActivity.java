package com.dutamobile;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dutamobile.fragments.ContactListFragment;
import com.dutamobile.model.Status;
import com.dutamobile.util.Helper;

public class MainActivity extends ActionBarActivity
{
    public static String PREFS_MAIN = "main-prefs";
    private MenuItem status_item;
    private Status myStatus;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = null;

        if (savedInstanceState != null)
        {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, Helper.CURRENT_FRAGMENT);
            Helper.fragmentReplacement(getSupportFragmentManager(), fragment, false, fragment.getTag());
        }

        if (fragment == null)
        {
            fragment = new ContactListFragment();
            Helper.fragmentReplacement(getSupportFragmentManager(), fragment, false, "ContactList");
        }

        setup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        status_item = menu.findItem(R.id.action_status_indicator);
        status_item.setIcon(Helper.getStatusIndicator(this, myStatus));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getGroupId() == R.id.status_group)
        {
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_MAIN, MODE_PRIVATE).edit();
            status_item.setIcon(item.getIcon());
            switch (item.getItemId())
            {
                case R.id.action_status_available:
                    editor.putString("status", Status.AVAILABLE.toString());
                    Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_status_away:
                    editor.putString("status", Status.AWAY.toString());
                    Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_status_busy:
                    editor.putString("status", Status.BUSY.toString());
                    Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.action_status_offline:
                    editor.putString("status", Status.OFFLINE.toString());
                    Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
                    break;
            }

            editor.commit();
        }

        if(mDrawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Fragment f = getSupportFragmentManager().findFragmentByTag(Helper.CURRENT_FRAGMENT);
        getSupportFragmentManager().putFragment(outState, Helper.CURRENT_FRAGMENT, f);
    }

    private void setup()
    {
        myStatus = Status.valueOf(getSharedPreferences(PREFS_MAIN, MODE_PRIVATE).getString("status", "OFFLINE"));

        String[] drawerItemsStrings = getResources().getStringArray(R.array.drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemsStrings));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                   /* host Activity */
                mDrawerLayout,          /* DrawerLayout object */
                R.drawable.blank,   /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,   /* "open drawer" description for accessibility */
                R.string.app_name       /* "close drawer" description for accessibility */
        )
        {
            public void onDrawerClosed(View view)
            {
                //getSupportActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
            {
                //getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            //TODO
            switch (position)
            {
                default:
                    //finish();
                    break;
            }

            mDrawerLayout.closeDrawer(mDrawerList);

        }

    }
}
