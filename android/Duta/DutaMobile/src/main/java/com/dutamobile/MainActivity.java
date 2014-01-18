package com.dutamobile;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.dutamobile.fragments.ContactListFragment;
import com.dutamobile.fragments.EditDialog;
import com.dutamobile.fragments.Refreshable;
import com.dutamobile.fragments.StatusDialog;
import com.dutamobile.model.Status;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.Helper;

public class MainActivity extends ActionBarActivity
{
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setup();
        if (savedInstanceState != null)
        {
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, Helper.CURRENT_FRAGMENT);
            Helper.fragmentReplacement(getSupportFragmentManager(), ((Object) fragment).getClass(), false, fragment.getTag(), null);
            return;
        }
        Helper.fragmentReplacement(getSupportFragmentManager(), ContactListFragment.class, false, ContactListFragment.TAG, null);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_add_contact:
            {
                EditDialog editDialog = new EditDialog();
                Bundle args = new Bundle();
                args.putBoolean(EditDialog.ARG_MODE, EditDialog.MODE.ADD.getMode());
                editDialog.setArguments(args);
                editDialog.show(getSupportFragmentManager(), EditDialog.TAG);
                break;
            }
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        if (!Helper.CURRENT_FRAGMENT.equals(ContactListFragment.TAG))
        {
            super.onBackPressed();
            Helper.CURRENT_FRAGMENT = ContactListFragment.TAG;
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        ((DutaApplication) getApplication()).ClearContactList();
        ((DutaApplication) getApplication()).SetMainActivity(null);
    }

    private void setup()
    {
        SetStatus(true);
        uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
        {
            @Override
            public void uncaughtException(Thread thread, Throwable ex)
            {
                NetClient.GetInstance().Logout();
                uncaughtExceptionHandler.uncaughtException(thread, ex);
            }
        });

        ((DutaApplication) getApplication()).SetMainActivity(this);
        ((DutaApplication) getApplication()).DownloadContactList();
        ((DutaApplication) getApplication()).StartReceiving();

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
        ) {};

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mActiveChatList.setFocusableInTouchMode(true);
        // mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mActiveChatList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void SetStatus(boolean isLogIn)
    {
        Status status = isLogIn ?
                Status.valueOf(getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE).getString("status", "AVAILABLE"))
                : Status.OFFLINE;

        NetClient.GetInstance().SetStatus(status,
                getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE)
                        .getString(StatusDialog.CURRENT_DESC, ""));
    }

    public void UpdateView()
    {
        Fragment f = getSupportFragmentManager().findFragmentByTag(Helper.CURRENT_FRAGMENT);

        if (f != null && f instanceof Refreshable) ((Refreshable) f).RefreshView();
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (parent.getId() == mDrawerList.getId())
            {
                switch (position)
                {
                    case 0:
                    {
                        new StatusDialog().show(getSupportFragmentManager(), "Dialog");
                        break;
                    }
                    case 1:
                    {
                        ((DutaApplication) getApplication()).DownloadContactList();
                        Toast.makeText(getApplication(), "Odświeżono.", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 2:
                    {
                        ((DutaApplication) getApplication()).StopReceiving();
                        SetStatus(false);
                        NetClient.GetInstance().Logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    }
                    default:
                        break;
                }
            }
            mDrawerLayout.closeDrawer(parent);
        }
    }
}
