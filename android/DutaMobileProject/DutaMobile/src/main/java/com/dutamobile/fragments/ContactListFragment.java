package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dutamobile.DutaApplication;
import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ContactListAdapter;
import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;
import com.dutamobile.util.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListFragment extends ListFragment implements RefreshableFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if(getListAdapter() == null)
            setListAdapter(new ContactListAdapter(getActivity(), GetContacts()));

        Helper.getSupportActionBar(getActivity()).setTitle(getString(R.string.app_name));

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        Contact c = (Contact)getListAdapter().getItem(position);
        Bundle args = new Bundle();
        args.putSerializable("Messages", (Serializable) c.getMessages());
        args.putString("ContactName", c.getName());
        args.putInt("ContactID", c.getId());
        Helper.fragmentReplacement(getActivity().getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + c.getName(), args);
        ((MainActivity)getActivity()).rightAdapter.addItem(c);
    }

    private List<Contact> GetContacts()
    {
        int counter = -1;

        List<Contact> contacts = ((DutaApplication)getActivity().getApplication()).getContactList();

        if(contacts == null)
            contacts = new ArrayList<Contact>(); //FIXME

        for(Contact c : contacts)
        {
            counter = c.getId();
            c.setMessages(generateConversation(c.getName(),counter));
        }

        Contact c = new Contact();
        c.setId(++counter);
        c.setLogin("john12");
        c.setName("John");
        c.setDescription("Cool men!");
        c.setStatus(Status.AWAY);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        c = new Contact();
        c.setId(++counter);
        c.setName("Marie");
        c.setLogin("cuntMarie");
        c.setDescription("I just bought new shoes!");
        c.setStatus(Status.AVAILABLE);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        c = new Contact();
        c.setId(++counter);
        c.setName("Alice");
        c.setLogin("junkiegirl14");
        c.setDescription("Fucking rabbit!");
        c.setStatus(Status.BUSY);
        c.setMessages(generateConversation(c.getName(), counter));
        contacts.add(c);

        return contacts;
    }

    private List<Message> generateConversation(String name, int id) //FIXME usunąć w ostatecznej wersji
    {
        Random r = new Random();

        String [] mgs = new String []
                {
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut augue eros, ullamcorper id mauris a, lacinia ultricies purus. Phasellus ligula enim, fringilla vitae elit eget, consequat gravida felis. Vivamus sem elit, semper eu rhoncus tristique, porta eget metus. Aliquam erat volutpat. Etiam vel eros vitae sapien ultricies blandit vitae convallis orci. Praesent laoreet ante quis ligula fermentum sodales. Praesent adipiscing lacus in metus tristique, et imperdiet purus eleifend. Vivamus fringilla commodo velit.",
                        "Ohh cool",
                        "Hi! Don't believe man, I just get new job!",
                        "Hi, what's up?"
                };

        List<Message> data = new ArrayList<Message>();
        int []  l = new int [] {Helper.MyID, id };

        Message m = new Message(name, l);
        m.setAuthor(id);
        m.setTimestamp(System.currentTimeMillis());
        data.add(m);

        for(int i = 0 ; i < 4 ; i++)
        {
            m = new Message();
            m.setAuthor(i%2 == 0 ? Helper.MyID : id);
            m.setTimestamp(System.currentTimeMillis());
            m.setMessageText(mgs[r.nextInt(4)]);
            data.add(m);
        }

        return data;
    }

    @Override
    public void RefreshView(final boolean newDataSet)
    {
            getListView().post(new Runnable()
            {
                @Override
                public void run()
                {
                    if(newDataSet)
                    ((ContactListAdapter)getListAdapter()).setData(GetContacts());
                    else
                    ((ContactListAdapter)getListAdapter()).notifyDataSetInvalidated();
                }
            });
    }
}
