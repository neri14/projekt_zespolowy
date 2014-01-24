package com.dutamobile.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;

import com.dutamobile.DutaApplication;
import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ContactListAdapter;
import com.dutamobile.model.Contact;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.Helper;
import com.dutamobile.util.PullDownRefreshList;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListFragment extends ListFragment implements Refreshable
{
    public static final String TAG = "ContactList";
    public static final String ARG_MESSAGES = "mgs1";
    public static final String ARG_CONTACT_NAME = "cName";
    public static final String ARG_CONTACT_ID = "cId";
    public static final String ARG_GROUP_CONVERSATION = "gConv";
    public static final String ARG_CONVERSATION_NAME = "cvName";
    private Handler handler;
    private ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.contact_list_layout, null);//super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        handler = new Handler();

        if (getListAdapter() == null || getListAdapter().getCount() == 0)
            setListAdapter(new ContactListAdapter(getActivity(), ((DutaApplication) getActivity().getApplication()).GetContactList()));
        Helper.getSupportActionBar(getActivity()).setTitle(getString(R.string.app_name));
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListItemSelect(position);
                return true;
            }
        });
        ((PullDownRefreshList) getListView()).setOnRefreshListener(new PullDownRefreshList.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                ((DutaApplication) getActivity().getApplication()).DownloadContactList();

                getListView().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        ((PullDownRefreshList) getListView()).onRefreshComplete();
                    }
                }, 5000);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        if (actionMode == null)
        {
            Contact contact = (Contact) getListAdapter().getItem(position);
            StartConversation(contact);
        }
        else
            onListItemSelect(position);
    }

    private void onListItemSelect(int position)
    {
        ContactListAdapter adapter = (ContactListAdapter) getListAdapter();
        adapter.toggleSelection(position);

        int count = adapter.getSelectedCount();
        boolean hasCheckedItems = count > 0;

        if (hasCheckedItems && actionMode == null)
            actionMode = ((MainActivity) getActivity()).startSupportActionMode(new ActionModeCallback(getListAdapter()));
        else if (!hasCheckedItems && actionMode != null) actionMode.finish();

        if (actionMode != null)
        {
            actionMode.setTitle(String.format("%d %s", count, getResources().getQuantityString(R.plurals.selected, count)));
            actionMode.invalidate();
        }
    }

    public void RefreshView()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    ((ContactListAdapter) getListAdapter()).setData(((DutaApplication) getActivity().getApplication()).GetContactList());
                    ((ContactListAdapter) getListAdapter()).notifyDataSetInvalidated();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.contact_list, menu);
    }

    private void CreateGroupConversation()
    {
        SparseBooleanArray tmp = ((ContactListAdapter) getListAdapter()).getSelectedPositions();
        int size = tmp.size() + 1;
        SortedMap<Integer, String> map = new TreeMap<Integer, String>();
        StringBuilder nameBuilder = new StringBuilder();
        map.put(Helper.MyID, getResources().getString(R.string.me));
        for(int i = 1 ; i < size; i++)
        {
            Contact c = ((Contact) getListAdapter().getItem(tmp.keyAt(i)));
            map.put(c.getId(), c.getName());
        }
        for (int id : map.keySet()) nameBuilder.append(id);
        Contact group = new Contact(true, "Chat_" + nameBuilder.toString(), map.keySet(), map.values());
        ((ContactListAdapter) getListAdapter()).getData().add(group);
        ((ContactListAdapter) getListAdapter()).notifyDataSetChanged();
        //Helper.startTask(new AddContactTask(getActivity(), group.getLogin(), group.getName(), EditDialog.MODE.ADD.getMode()));
        StartConversation(group);
    }

    private void StartConversation(Contact contact)
    {
        boolean gc = contact.isGroupConversation();
        contact.setNewMessage(false);
        Bundle args = new Bundle();
        args.putIntArray(ARG_CONTACT_ID, contact.getIdArray());
        args.putStringArray(ARG_CONTACT_NAME, contact.getNamesArray(getString(R.string.me)));
        args.putString(ARG_CONVERSATION_NAME, contact.getName());
        args.putBoolean(ARG_GROUP_CONVERSATION,gc);
        args.putSerializable(ARG_MESSAGES, (Serializable) contact.getMessages());
        Helper.fragmentReplacement(getActivity().getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + contact.getName(), args);
    }

    private class ActionModeCallback implements ActionMode.Callback
    {
        ContactListAdapter adapter;

        private ActionModeCallback(Adapter listAdapter)
        {
            this.adapter = (ContactListAdapter) listAdapter;
        }

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu)
        {
            mode.getMenuInflater().inflate(R.menu.contextual_contact_list, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu)
        {
            int count = adapter.getSelectedCount();

            if (count == 1)
            {
                menu.findItem(R.id.action_cl_group_conversation).setVisible(false);
                menu.findItem(R.id.action_cl_edit).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.action_cl_group_conversation).setVisible(!adapter.IsAnyGroupConversationSelected());
                menu.findItem(R.id.action_cl_edit).setVisible(false);
            }

            return false;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.action_cl_delete:
                {
                    SparseBooleanArray selected = adapter.getSelectedPositions();
                    for (int i = (selected.size() - 1); i >= 0; i--)
                    {
                        if (selected.valueAt(i))
                        {
                            Contact selectedItem = (Contact) adapter.getItem(selected.keyAt(i));
                            NetClient.GetInstance().RemoveContact(selectedItem);
                            adapter.removeItem(selectedItem);
                        }
                    }
                    break;
                }
                case R.id.action_cl_edit:
                {
                    Contact contact = (Contact) adapter.getItem(adapter.getSelectedPositions().keyAt(0));
                    EditDialog editDialog = new EditDialog();
                    Bundle args = new Bundle();
                    args.putString(EditDialog.ARG_LOGIN, contact.getLogin());
                    args.putString(EditDialog.ARG_NICK, contact.getName());
                    args.putBoolean(EditDialog.ARG_MODE, EditDialog.MODE.EDIT.getMode());
                    editDialog.setArguments(args);
                    editDialog.show(getActivity().getSupportFragmentManager(), EditDialog.TAG);

                    break;
                }
                case R.id.action_cl_group_conversation:
                {
                    CreateGroupConversation();
                    break;
                }

                default:
                    return false;
            }

            mode.finish();

            return true;
        }

        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode)
        {
            adapter.removeSelection();
            actionMode = null;
        }
    }

}
