package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
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
import com.dutamobile.util.Helper;

import java.io.Serializable;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListFragment extends ListFragment implements Refreshable
{
    public static String TAG = "ContactList";
    public static String ARG_MESSAGES = "Messages";
    public static String ARG_CONTACT_NAME = "ContactName";
    public static String ARG_CONTACT_ID = "ContactID";

    private ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (getListAdapter() == null)
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
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        if (actionMode == null)
        {
            Contact c = (Contact) getListAdapter().getItem(position);
            Bundle args = new Bundle();
            args.putSerializable(ARG_MESSAGES, (Serializable) c.getMessages());
            args.putString(ARG_CONTACT_NAME, c.getName());
            args.putInt(ARG_CONTACT_ID, c.getId());
            Helper.fragmentReplacement(getActivity().getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + c.getName(), args);
            ((MainActivity) getActivity()).rightAdapter.addItem(c);
        }
        else
            onListItemSelect(position);
    }

    private void onListItemSelect(int position)
    {
        ContactListAdapter adapter = (ContactListAdapter) getListAdapter();
        adapter.toggleSelection(position);

        int count = adapter.getSelectedCount();
        boolean hasCheckedItems =  count > 0;

        if (hasCheckedItems && actionMode == null)
            actionMode = ((MainActivity)getActivity()).startSupportActionMode(new ActionModeCallback(getListAdapter()));
        else if (!hasCheckedItems && actionMode != null)
            actionMode.finish();

        if (actionMode != null)
        {
            actionMode.setTitle(String.valueOf(adapter.getSelectedCount()) + " selected");

            if(count < 3) actionMode.invalidate();
        }
    }

    public void RefreshView()
    {
        getListView().post(new Runnable()
        {
            @Override
            public void run()
            {
                ((ContactListAdapter) getListAdapter()).setData(((DutaApplication) getActivity().getApplication()).GetContactList());
                ((ContactListAdapter) getListAdapter()).notifyDataSetInvalidated();
            }
        });
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
            mode.getMenuInflater().inflate(R.menu.contact_list, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu)
        {
            int count = adapter.getSelectedCount();

            if(count == 1)
            {
                menu.findItem(R.id.action_cl_group_conversation).setVisible(false);
                menu.findItem(R.id.action_cl_edit).setVisible(true);
            }
            else
            {
                menu.findItem(R.id.action_cl_group_conversation).setVisible(true);
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
                    SparseBooleanArray selected = adapter
                            .getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--)
                    {
                        if (selected.valueAt(i))
                        {
                            Contact selectedItem = (Contact) adapter.getItem(selected.keyAt(i));
                            adapter.removeItem(selectedItem);
                        }
                    }
                    mode.finish(); // Action picked, so close the CAB
                    break;
                }
                case R.id.action_cl_edit:
                {
                    Contact contact = (Contact) adapter.getItem(adapter.getSelectedIds().keyAt(0));
                    EditDialog editDialog = new EditDialog();
                    Bundle args = new Bundle();
                    args.putString(EditDialog.LOGIN, contact.getLogin());
                    args.putString(EditDialog.NICK, contact.getName());
                    editDialog.setArguments(args);
                    editDialog.show(getActivity().getSupportFragmentManager(), EditDialog.TAG);

                    break;
                }
                case R.id.action_cl_group_conversation:
                {
                    break;
                }

                default: return false;
            }

            actionMode.finish();

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
