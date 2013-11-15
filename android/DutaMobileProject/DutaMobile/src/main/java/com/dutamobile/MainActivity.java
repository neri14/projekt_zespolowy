package com.dutamobile;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
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

import com.dutamobile.adapter.ActiveConversationsAdapter;
import com.dutamobile.fragments.ChatFragment;
import com.dutamobile.fragments.ContactListFragment;
import com.dutamobile.model.Contact;
import com.dutamobile.model.Status;
import com.dutamobile.util.Helper;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity
{
    public static final String PREFS_MAIN = "main-prefs";
    private MenuItem status_item;
    private Status myStatus;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView mActiveChatList;
    private ActionBarDrawerToggle mDrawerToggle;

    private List<Contact> activeConversations;
    public ActiveConversationsAdapter rightAdapter;

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

        Helper.fragmentReplacement(getSupportFragmentManager(), ContactListFragment.class, false, "ContactList", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        status_item = menu.findItem(R.id.action_status_indicator);
        status_item.setIcon(Helper.getStatusIndicator(this, myStatus));
        MenuItem chatItem = menu.findItem(R.id.action_chats);

        if (chatItem != null)
        {
            chatItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick(MenuItem item)
                {
                    if (mDrawerLayout.isDrawerOpen(mDrawerList)) mDrawerLayout.closeDrawer(mDrawerList);

                    if (mDrawerLayout.isDrawerOpen(mActiveChatList))
                        mDrawerLayout.closeDrawer(mActiveChatList);
                    else
                        mDrawerLayout.openDrawer(mActiveChatList);

                    return false;
                }
            });
        }

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
                    break;
                case R.id.action_status_away:
                    editor.putString("status", Status.AWAY.toString());
                    break;
                case R.id.action_status_busy:
                    editor.putString("status", Status.BUSY.toString());
                    break;
                case R.id.action_status_offline:
                    editor.putString("status", Status.OFFLINE.toString());
                    break;
            }

            Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            editor.commit();
        }

        //if(mDrawerToggle.onOptionsItemSelected(item))
        //    return true;

        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
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

        activeConversations = new ArrayList<Contact>();

        myStatus = Status.valueOf(getSharedPreferences(PREFS_MAIN, MODE_PRIVATE).getString("status", "OFFLINE"));

        String[] drawerItemsStrings = getResources().getStringArray(R.array.drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mActiveChatList = (ListView) findViewById(R.id.right_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemsStrings));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        rightAdapter = new ActiveConversationsAdapter(this, activeConversations);
        mActiveChatList.setAdapter(rightAdapter);
        mActiveChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                rightAdapter.deleteItem(position);

                if(getSupportFragmentManager().findFragmentByTag(Helper.CURRENT_FRAGMENT) instanceof ChatFragment)
                    onBackPressed();

                Toast.makeText(getApplication(), "Zamknięto konwersację.", Toast.LENGTH_SHORT).show();

                return true;
            }
        });

        mActiveChatList.setOnItemClickListener(new DrawerItemClickListener());

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
                if (mDrawerLayout.isDrawerOpen(mActiveChatList))
                    mDrawerLayout.closeDrawer(mActiveChatList);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
            {


                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mActiveChatList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            //TODO
            if (parent.getId() == mDrawerList.getId())
            {
                switch (position)
                {
                    case 0: connect(); break;
                    case 1: finish(); break;
                    default: break;
                }
            } else
            {
                Contact contact = activeConversations.get(position);

                if (getSupportFragmentManager().findFragmentByTag("Chat-" + contact.getName()) == null)
                {
                    Bundle args = new Bundle();
                    args.putSerializable("Messages", (Serializable) contact.getMessages());
                    args.putString("ContactName", contact.getName());

                    while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    {
                        getSupportFragmentManager().popBackStackImmediate();
                    }

                    Helper.fragmentReplacement(getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + contact.getName(), args);
                }

            }

            mDrawerLayout.closeDrawer(parent);

        }

    }

    private Boolean connect()
    {
        new AsyncTask<Void, Void, Boolean>()
        {
        @Override
        protected Boolean doInBackground(Void... params)
        {
            //String address = "http://10.0.3.2:1404/Account/Login";
            String address = "http://192.168.1.5/Account/Login";

            try
            {

                HttpClient client = new DefaultHttpClient();

                List<NameValuePair> data = new ArrayList<NameValuePair>();
                data.add(new BasicNameValuePair("username", "asd"));
                data.add(new BasicNameValuePair("password", "zxc"));

                HttpPost post = new HttpPost(address);

                post.setEntity( new UrlEncodedFormEntity(data));

                //HttpResponse response =
                        client.execute(post);

            }
            catch (Exception e) { return false; }

            return true;
        }
        }.execute();

        return true;
    }
}
