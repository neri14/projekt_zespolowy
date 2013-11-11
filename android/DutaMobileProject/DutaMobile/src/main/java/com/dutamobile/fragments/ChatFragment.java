package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.dutamobile.R;
import com.dutamobile.adapter.ChatAdapter;
import com.dutamobile.model.Message;
import com.dutamobile.util.Helper;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatFragment extends ListFragment
{
    private EditText message_box;

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
                ((ChatAdapter) getListAdapter()).addMessage(new Message(message_box.getText().toString(), false));
                message_box.setText(null);
                getListView().setSelection(getListAdapter().getCount() - 1);
            }
        }
        );

        if(getListAdapter() == null)
            setListAdapter(new ChatAdapter(getActivity(), (List<Message>) getArguments().get("Messages") ));

        ((ListView)v.findViewById(android.R.id.list)).setDividerHeight(0);

        Helper.getSupportActionBar(getActivity()).setTitle(getArguments().getString("ContactName"));

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setSelection(getListAdapter().getCount() - 1);
    }
}
