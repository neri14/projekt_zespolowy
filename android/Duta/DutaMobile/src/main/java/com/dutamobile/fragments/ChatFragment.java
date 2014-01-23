package com.dutamobile.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ChatAdapter;
import com.dutamobile.model.Message;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.Helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatFragment extends ListFragment implements Refreshable
{
    private EditText message_box;
    private ActionMode actionMode;
    private Handler handler;
    private MenuItem chatItem;

    private int[] contactIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);
        message_box = (EditText) v.findViewById(R.id.edittext_1);
        handler = new Handler();
        v.findViewById(R.id.imagebutton_1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = message_box.getText().toString();
                //if text longer than 0 and doesn't contain only whitespace chars
                if (text.length() > 0 && !text.matches("^\\s*$")) SendMessage(text);
            }
        }
        );

        if (getListAdapter() == null)
        {
            SetCustomAdapter();
        }
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDividerHeight(0);
        getListView().setSelection(getListAdapter().getCount() - 1);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                onListItemSelect(position);
                return true;
            }
        });
        Helper.getSupportActionBar(getActivity()).setTitle(getArguments().getString(ContactListFragment.ARG_CONVERSATION_NAME));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.chat, menu);
        chatItem = menu.findItem(R.id.action_chats);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        if (actionMode != null) onListItemSelect(position);
    }

    private void onListItemSelect(int position)
    {
        ChatAdapter adapter = (ChatAdapter) getListAdapter();
        adapter.toggleSelection(position);

        int count = adapter.getSelectedCount();
        boolean hasCheckedItems = count > 0;

        if (hasCheckedItems && actionMode == null)
        {
            actionMode = ((MainActivity) getActivity()).startSupportActionMode(ActionModeCallback);
            getListView().setDividerHeight(1);
        }
        else if (!hasCheckedItems && actionMode != null) actionMode.finish();

        if (actionMode != null)
            actionMode.setTitle(String.format("%d %s", count, getResources().getQuantityString(R.plurals.selected, count)));
    }

    @Override
    public void RefreshView()
    {
        handler.post(new Runnable()
        {
            @Override
            public void run()
            {
                chatItem.setIcon(Helper.getChatItemUpdateStatus() ? R.drawable.ic_new_message : R.drawable.ic_no_message);
                Helper.setChatItemUpdateStatus(false);
                ((ChatAdapter) getListAdapter()).notifyDataSetInvalidated();
                getListView().setSelection(getListAdapter().getCount() - 1);
            }
        });
    }

    private void SendMessage(final String text)
    {
        new Thread("SendingMessageThread")
        {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        message_box.setText(null);
                    }
                });
                final int [] ids = Arrays.copyOf(contactIds, contactIds.length + 1);
                ids[contactIds.length] = Helper.MyID;
                final long timestamp = NetClient.GetInstance().SendMessage(text, ids);

                handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Message msg = new Message(text, ids);
                        msg.setTimestamp(timestamp);
                        ((ChatAdapter) getListAdapter()).addMessage(msg);
                        getListView().setSelection(getListAdapter().getCount() - 1);
                    }
                }
                );
            }
        }.start();
    }

    private void SetCustomAdapter()
    {
        HashMap<Integer, String> usernames = new HashMap<Integer, String>();
        contactIds = getArguments().getIntArray(ContactListFragment.ARG_CONTACT_ID);
        String [] contactNames = getArguments().getStringArray(ContactListFragment.ARG_CONTACT_NAME);
        for(int i = 0 ; i < contactIds.length ; i++) usernames.put(contactIds[i], contactNames[i]);
        setListAdapter(new ChatAdapter(getActivity(), (List<Message>) getArguments().get(ContactListFragment.ARG_MESSAGES),
                usernames));
    }

    private final ActionMode.Callback ActionModeCallback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.cab_chat, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            ((ChatAdapter) getListAdapter()).removeSelection();
            getListView().setDividerHeight(0);
            actionMode = null;
        }
    };
}
