package com.dutamobile;

import android.app.Application;
import android.os.AsyncTask;

import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.util.Helper;
import com.dutamobile.util.NetClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz on 17.11.13.
 */
public class DutaApplication extends Application
{
    private List<Contact> contactList;
    private List<Message> messageList;

    private MessageReceiver messageReceiver;
    private StatusUpdater statusUpdater;

    @Override
    public void onCreate()
    {
        super.onCreate();

        contactList = new ArrayList<Contact>();
    }

    private List<String> MergeMessagesWithContacts()
    {
        ArrayList<String> chatNames;

        if(contactList == null ) return null;

        if(messageList == null) return null;

        chatNames = new ArrayList<String>();

        for(Message m : messageList)
        {
            for(int id : m.getUsers())
            {
                if(id == Helper.MyID) continue;

                for(Contact c : contactList)
                {
                    if(c.getId() == id)
                    {
                        c.addMessage(m);

                        String chatName = "Chat-" + c.getName();

                        if(!chatNames.contains(chatName))
                            chatNames.add(chatName);
                        break;
                    }
                }
            }
        }

        messageList.clear();

        return chatNames;
    }

    private void UpdateContactStatuses(List<StatusUpdateResponse> update)
    {
        for (StatusUpdateResponse u : update)
        {
            for(Contact c : contactList)
                if(c.getId() == u.getUser_id())
                {
                    c.Update(u);
                    break;
                }
        }
    }

    public List<Contact> GetContactList()
    {
        return contactList;
    }

    public void ClearContactList()
    {
        if(contactList != null && contactList.size() > 0)
            contactList.clear();
    }

    public Contact getContactByName(String chatName)
    {
        String name = chatName.substring(chatName.indexOf('-') + 1);

        for(Contact c : contactList)
            if(c.getName().equals(name))
            {
                return c;
            }

        return null;
    }

    public void DownloadContactList()
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                contactList = NetClient.GetInstance().GetContactList();

                if(contactList == null)
                {
                    try
                    {
                        Thread.sleep(10000);
                        contactList = NetClient.GetInstance().GetContactList();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }

                android.util.Log.v("ClientList", contactList != null ? contactList.size() + "" : "null");

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                mainActivity.UpdateView(null, true);

            }
        }.execute();
    }

    public class MessageReceiver extends AsyncTask<Void, Void, Void>
    {
        private boolean Run = true;

        public void stop()
        {
            Run = false;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            while(Run)
            {
                messageList = NetClient.GetInstance().GetMessage();
                List<String> chatNames = MergeMessagesWithContacts();

                mainActivity.UpdateView(chatNames, false);

            }

            return null;
        }
    }

    public class StatusUpdater extends AsyncTask<Void, Void, Void>
    {
        private boolean Run = true;

        public void stop()
        {
            Run = false;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            while(Run)
            {
                List<StatusUpdateResponse> data =  NetClient.GetInstance().GetStatusUpdate();

                if(data != null) UpdateContactStatuses(data);
            }

            return null;
        }
    }

    public void StartReceiving()
    {
        //FIXME Ogarnąć aktualizacje statusów
        if(messageReceiver == null)
        {
            messageReceiver = new MessageReceiver();
            messageReceiver.execute();
        }
        /*
        if(statusUpdater == null)
        {
            statusUpdater = new StatusUpdater();
            statusUpdater.execute();
        }
        */
    }

    public void StopReceiving()
    {
        if(statusUpdater != null) statusUpdater.stop();
        if(messageReceiver != null) messageReceiver.stop();
    }

    private MainActivity mainActivity;

    public void SetMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
    }
}
