package com.dutamobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.util.Helper;
import com.dutamobile.util.NetClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class MainActivity extends ActionBarActivity
{

    private MenuItem status_item;
    private Status myStatus;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ListView mActiveChatList;
    private ActionBarDrawerToggle mDrawerToggle;


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
            SharedPreferences.Editor editor = getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE).edit();
            status_item.setIcon(item.getIcon());
            Status status = null;

            switch (item.getItemId())
            {
                case R.id.action_status_available:
                    status = Status.AVAILABLE;
                    break;
                case R.id.action_status_away:
                    status = Status.AWAY;
                    break;
                case R.id.action_status_busy:
                    status = Status.BUSY;
                    break;
                case R.id.action_status_offline:
                    status = Status.OFFLINE;
                    break;
            }

            editor.putString("status", status.toString()).commit();
            Toast.makeText(this, item.getTitle().toString(), Toast.LENGTH_SHORT).show();
            NetClient.GetInstance().SetStatus(status, "Mój opis" );
        }

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
        ((DutaApplication) getApplication()).DownloadData(getSupportFragmentManager());
        //((DutaApplication) getApplication()).StartReceiving();

        myStatus = Status.valueOf(getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE).getString("status", "AVAILABLE"));

        String[] drawerItemsStrings = getResources().getStringArray(R.array.drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mActiveChatList = (ListView) findViewById(R.id.right_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawerItemsStrings));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        rightAdapter = new ActiveConversationsAdapter(this, ((DutaApplication)getApplication()).getActiveConversationsList());
        mActiveChatList.setAdapter(rightAdapter);
        mActiveChatList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                String name = ((Contact)rightAdapter.getItem(position)).getName();
                rightAdapter.deleteItem(position);

                if(Helper.CURRENT_FRAGMENT.equals("Chat-" + name)) onBackPressed();

                Toast.makeText(getApplication(), R.string.conversation_closed, Toast.LENGTH_SHORT).show();

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
                if (mDrawerLayout.isDrawerOpen(mActiveChatList)) mDrawerLayout.closeDrawer(mActiveChatList);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView)
            {
                invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        //mActiveChatList.setFocusableInTouchMode(true);
       // mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, mActiveChatList);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onBackPressed()
    {
        if (mDrawerLayout.isDrawerOpen(mActiveChatList))
        {
           mDrawerLayout.closeDrawer(mActiveChatList);
        }
        else
        {
            super.onBackPressed();
            Helper.CURRENT_FRAGMENT = "ContactList";
        }


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
                    case 0:
                    {
                        List<Message> ml = new ArrayList<Message>();
                        List<StatusUpdateResponse> su = new ArrayList<StatusUpdateResponse>();


                        Random r = new Random();
                        for(int i = 0; i < 4 ; i++)
                        {
                            int t_id = ((DutaApplication)getApplication()).getContactList().get(r.nextInt(4)).getId();
                            Message m = new Message("text" + i, new int[] { Helper.MyID , t_id});
                            m.setAuthor(m.getUsers().get(r.nextInt(1)));
                            m.setTimestamp(System.currentTimeMillis());
                            ml.add(m);
                        }

                        int index = r.nextInt(4);
                        StatusUpdateResponse su_i = new StatusUpdateResponse();
                        su_i.setUser_id(index);
                        su_i.setDescription("TEST-" + r.nextInt(100));
                        su_i.setStatus(Status.OFFLINE.ordinal());

                        su.add(su_i);

                        ((DutaApplication)getApplication()).MockUpdate(ml, su);

                        Toast.makeText(getApplication(), "Nuuuuda! " + index, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case 1:
                    {
                        Toast.makeText(getApplication(), "Tu się będzie ustawiało opis.", Toast.LENGTH_SHORT).show(); break;
                    }
                    case 2:
                    {
                        ((DutaApplication)getApplication()).StopReceiving();
                        NetClient.GetInstance().Logout();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                        break;
                    }
                    default: break;
                }
            }
            else
            {
                Contact contact = ((DutaApplication)getApplication()).getActiveConversationsList().get(position);

                if (getSupportFragmentManager().findFragmentByTag("Chat-" + contact.getName()) == null)
                {
                    Bundle args = new Bundle();
                    args.putSerializable("Messages", (Serializable) contact.getMessages());
                    args.putString("ContactName", contact.getName());
                    args.putInt("ContactID", contact.getId());

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

}
