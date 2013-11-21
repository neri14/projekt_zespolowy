package com.dutamobile.fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ChatAdapter;
import com.dutamobile.model.Message;
import com.dutamobile.util.Helper;
import com.dutamobile.util.NetClient;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatFragment extends ListFragment
{
    private EditText message_box;
    private ActionMode actionMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);

        message_box = (EditText) v.findViewById(R.id.edittext_1);

        v.findViewById(R.id.imagebutton_1).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AsyncTask<Void, Void, Message>()
                {
                    protected Message doInBackground(Void... params)
                    {
                        Message msg = new Message(message_box.getText().toString(), new int[] { 1, 2});
                        long timestamp = NetClient.GetInstance().SendMessage(msg.getMessageText(), msg.getUsers());
                        msg.setTimestamp(timestamp);

                        return msg;
                    }

                    @Override
                    protected void onPostExecute(Message message)
                    {
                        super.onPostExecute(message);

                        ((ChatAdapter) getListAdapter()).addMessage(message);
                        message_box.setText(null);
                        getListView().setSelection(getListAdapter().getCount() - 1);

                    }
                }.execute();
            }
        }
        );

        if(getListAdapter() == null)
        {
            HashMap<Integer, String> usernames = new HashMap<Integer, String>();
            usernames.put(getArguments().getInt("ContactID"), getArguments().getString("ContactName"));

            setListAdapter(new ChatAdapter(getActivity(), (List<Message>) getArguments().get("Messages"), usernames));
        }

        Helper.getSupportActionBar(getActivity()).setTitle(getArguments().getString("ContactName"));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setDividerHeight(0);
        getListView().setSelection(getListAdapter().getCount() - 1);

        getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        getListView().setItemsCanFocus(false);

        /* FIXME zrobić zaznaczenie/odznaczanie wiadomości w celu usunięcia
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
            {
                if(actionMode == null)
                    actionMode = ((MainActivity)getActivity()).startSupportActionMode(contextualActionBarCallback);


                if(view.isSelected())  //TODO zdobyć tablice zaznaczonych elementów (prawdopodobnie z adaptera);
                {
                    view.setSelected(false);
                    view.setBackgroundColor(Color.TRANSPARENT);
                    actionMode.setTitle("Deselected " + position);
                }
                else
                {
                view.setSelected(true);
                view.setBackgroundColor(Color.CYAN);
                actionMode.setTitle("Selected " + position);
                }

        ((ChatAdapter) getListAdapter()).notifyDataSetChanged();

                return true;
            }
        });
        */
    }

    private ActionMode.Callback contextualActionBarCallback = new ActionMode.Callback()
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.chat, menu);
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
            Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
            mode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            actionMode = null;
        }
    };
}
