package com.dutamobile.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.adapter.ContactListAdapter;
import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;
import com.dutamobile.util.Helper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        Bundle args = new Bundle();
        args.putSerializable("Messages", (Serializable) c.getMessages());
        args.putString("ContactName", c.getName());
        Helper.fragmentReplacement(getActivity().getSupportFragmentManager(), ChatFragment.class, true, "Chat-" + c.getName(), args);
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
        c.setMessages(generateConversation(c.getName()));
        data.add(c);

        c = new Contact();
        c.setId(1);
        c.setName("Marie");
        c.setDescription("I just bought new shoes!");
        c.setStatus(Status.AVAILABLE);
        c.setMessages(generateConversation(c.getName()));
        data.add(c);

        c = new Contact();
        c.setId(2);
        c.setName("Alice");
        c.setDescription("Fucking rabbit!");
        c.setStatus(Status.BUSY);
        c.setMessages(generateConversation(c.getName()));
        data.add(c);

        return data;
    }

    private List<Message> generateConversation(String name)
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

        Message m = new Message();
        m = new Message();
        m.setIncoming(true);
        m.setMessageText(name);
        data.add(m);

        for(int i = 0 ; i < 4 ; i++)
        {
            m = new Message();
            m.setIncoming(i % 2 == 0);
            m.setMessageText(mgs[r.nextInt(4)]);
            data.add(m);
        }

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
