package com.dutamobile.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.RESTClient;
import com.dutamobile.adapter.ContactListAdapter;
import com.dutamobile.model.Contact;
import com.dutamobile.model.Status;
import com.dutamobile.util.Helper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListFragment extends ListFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if(getListAdapter() == null)
            setListAdapter(new ContactListAdapter(getActivity(), getContacts()));

        Helper.getSupportActionBar(getActivity()).setTitle(getString(R.string.app_name));

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);

        /*
        new AsyncTask<Void,Void,Void>()
        {

            @Override
            protected Void doInBackground(Void... params)
            {
                Object o = RESTClient.getInstance().getContacts();

                return null;
            }
        };*/

        Contact c = (Contact)getListAdapter().getItem(position);
        Helper.getSupportActionBar(getActivity()).setTitle(c.getName());
        Helper.fragmentReplacement(getActivity().getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + c.getName());
        ((MainActivity)getActivity()).rightAdapter.addItem(c);
    }

    private List<Contact> getContacts()
    {
        List<Contact> data = new ArrayList<Contact>();
        Contact c = new Contact();
        c.setId(0);
        c.setName("John");
        c.setDescription("Cool men!");
        c.setStatus(Status.AWAY);
        data.add(c);

        c = new Contact();
        c.setId(1);
        c.setName("Marie");
        c.setDescription("I just bought new shoes!");
        c.setStatus(Status.AVAILABLE);
        data.add(c);

        c = new Contact();
        c.setId(2);
        c.setName("Alice");
        c.setDescription("Fucking rabbit!");
        c.setStatus(Status.BUSY);
        data.add(c);

        return data;
    }

    private void DeleteMe() //TODO usuń po wszystkim
    {
        final String SOAP_ACTION = "http://tempuri.org/IService1/IsWorking";
        final String METHOD_NAME = "IsWorking";
        final String NAMESPACE = "http://tempuri.org";
        final String URL = "http://192.168.1.6:8733/Design_Time_Addresses/SimpleWebService/SWS?wsdl";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                envelope.setOutputSoapObject(request);

                HttpTransportSE ht = new HttpTransportSE(URL);
                try {
                    ht.call(SOAP_ACTION, envelope);
                    SoapPrimitive response = (SoapPrimitive)envelope.getResponse();

                    Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();



    }


}
