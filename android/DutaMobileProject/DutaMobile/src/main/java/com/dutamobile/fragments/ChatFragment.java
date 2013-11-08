package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.dutamobile.R;
import com.dutamobile.adapter.ChatAdapter;
import com.dutamobile.model.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatFragment extends ListFragment
{
    private ImageView send_btn;
    private EditText message_box;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.chat_fragment, container, false);

        send_btn = (ImageView) v.findViewById(R.id.imagebutton_1);
        message_box = (EditText) v.findViewById(R.id.edittext_1);

        send_btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ((ChatAdapter)getListAdapter()).addMessage(new Message(message_box.getText().toString(), false));
                message_box.setText(null);
                getListView().setSelection(getListAdapter().getCount() - 1);
            }
        }
        );

        if(getListAdapter() == null)
            setListAdapter(new ChatAdapter(getActivity(), setData()));

        ((ListView)v.findViewById(android.R.id.list)).setDividerHeight(0);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getListView().setSelection(getListAdapter().getCount() - 1);
    }



    private List<Message> setData()
    {
        Random r = new Random();

        String [] mgs = new String []
                {
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut augue eros, ullamcorper id mauris a, lacinia ultricies purus. Phasellus ligula enim, fringilla vitae elit eget, consequat gravida felis. Vivamus sem elit, semper eu rhoncus tristique, porta eget metus. Aliquam erat volutpat. Etiam vel eros vitae sapien ultricies blandit vitae convallis orci. Praesent laoreet ante quis ligula fermentum sodales. Praesent adipiscing lacus in metus tristique, et imperdiet purus eleifend. Vivamus fringilla commodo velit.",
                        "Ohh cool",
                        "Aenean lorem erat, pretium id neque non, vulputate vehicula sem. Praesent nec posuere felis, in pellentesque lorem. Ut in tincidunt quam.",
                        "TEST MESSAGE"
                };

        List<Message> data = new ArrayList<Message>();

        Message m;

        for(int i = 0 ; i < 4 ; i++)
        {
            m = new Message();
            m.setIncoming(i % 2 == 0);
            m.setMessageText(mgs[r.nextInt(4)]);
            data.add(m);
        }

        return data;
    }


}
