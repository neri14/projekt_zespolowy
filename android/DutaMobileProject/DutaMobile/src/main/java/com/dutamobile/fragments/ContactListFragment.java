package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dutamobile.DutaApplication;
import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ContactListAdapter;
import com.dutamobile.model.Contact;
import com.dutamobile.util.Helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListFragment extends ListFragment implements Refreshable
{
    public static String TAG = "ContactList";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if(getListAdapter() == null)
            setListAdapter(new ContactListAdapter(getActivity(), ((DutaApplication)getActivity().getApplication()).GetContactList()));

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

        List<Contact> contacts = ((DutaApplication)getActivity().getApplication()).GetContactList();

        if(contacts == null)
            contacts = new ArrayList<Contact>(); //FIXME

        return contacts;
    }

    public void RefreshView()
    {
            getListView().post(new Runnable()
            {
                @Override
                public void run()
                {
                    ((ContactListAdapter)getListAdapter()).setData(((DutaApplication)getActivity().getApplication()).GetContactList());
                    ((ContactListAdapter)getListAdapter()).notifyDataSetInvalidated();
                }
            });
    }
}
