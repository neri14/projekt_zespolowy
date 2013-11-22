package com.dutamobile;

import android.app.Application;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.dutamobile.fragments.RefreshableFragment;
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
    private List<Contact> activeConversations;
    private FragmentManager _fragmentManager;

    private MessageReceiver messageReceiver;
    private StatusUpdater statusUpdater;

    @Override
    public void onCreate()
    {
        super.onCreate();

        contactList = new ArrayList<Contact>();
        activeConversations = new ArrayList<Contact>();
        Helper.MyID = getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE).getInt("MyUserID", 1000);
    }

    private void MergeMessagesWithContacts()
    {
        if(contactList == null ) return;

        if(messageList == null) return;

        for(Message m : messageList)
        {
            for(int id : m.getUsers())
            {
                if(id == Helper.MyID) continue;

                for(Contact c : contactList)
                {
                    if(c.getId() == id)
                        c.addMessage(m);
                }
            }
        }

        messageList.clear();
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

    public List<Contact> getContactList()
    {
        return contactList;
    }

    public List<Contact> getActiveConversationsList()
    {
        return activeConversations;
    }

    public void DownloadData(FragmentManager fragmentManager)
    {
        if(this._fragmentManager == null && fragmentManager != null)
            this._fragmentManager = fragmentManager;


        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                contactList = NetClient.GetInstance().GetContactList();
                // messageList = NetClient.GetInstance().GetMessage();

                //MergeMessagesWithContacts();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                RefreshView(Helper.CURRENT_FRAGMENT, true);

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
                MergeMessagesWithContacts();

                RefreshView(Helper.CURRENT_FRAGMENT, false);
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

                UpdateContactStatuses(data);
            }

            return null;
        }
    }

    private void RefreshView(String tag, Boolean newDataSet)
    {
        Fragment f = _fragmentManager.findFragmentByTag(tag);
        if(f instanceof RefreshableFragment)
            ((RefreshableFragment) f).RefreshView(newDataSet);
    }

    public void StartReceiving()
    {
        //FIXME poprawić odbieranie wiadomości i aktualizacje statusów
        if(messageReceiver == null)
        {
            messageReceiver = new MessageReceiver();
            messageReceiver.execute();
        }

        if(statusUpdater == null)
        {
            statusUpdater = new StatusUpdater();
            statusUpdater.execute();
        }
    }

    public void StopReceiving()
    {
        if(statusUpdater != null) statusUpdater.stop();
        if(messageReceiver != null) messageReceiver.stop();

    }

    public void MockUpdate(List<Message> messageList, List<StatusUpdateResponse> statusUpdate)
    {
        this.messageList = messageList;

        MergeMessagesWithContacts();

        if(statusUpdate != null)
        {
            UpdateContactStatuses(statusUpdate);
            RefreshView(Helper.CURRENT_FRAGMENT, false);
        }
    }

}
