package com.dutamobile;

import android.app.Application;
import android.os.AsyncTask;
import android.webkit.CookieSyncManager;

import com.dutamobile.fragments.EditDialog;
import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.UpdateMessageOutput;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.AddContactTask;
import com.dutamobile.util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz on 17.11.13.
 */
public class DutaApplication extends Application
{
    private static final long DAY_IN_MILLIS = 86400000;
    private MainActivity mainActivity;
    private List<Contact> contactList;
    private List<Message> messageList;
    private MessageReceiver messageReceiver;
    private StatusUpdater statusUpdater;

    @Override
    public void onCreate()
    {
        super.onCreate();
        contactList = new ArrayList<Contact>();
        CookieSyncManager.createInstance(this);
    }

    private UpdateMessageOutput MergeMessagesWithContacts(boolean forNewMessages)
    {
        UpdateMessageOutput output = new UpdateMessageOutput();
        if (contactList == null || messageList == null) return output;
        String currentName;
        if (Helper.CURRENT_FRAGMENT.contains("Chat-"))
            currentName = Helper.CURRENT_FRAGMENT.substring(5);
        else currentName = "";
        for (Message m : messageList)
        {
            if (m.getUsers().size() == 2)
            {
                for (int id : m.getUsers())
                {
                    if (id == Helper.MyID) continue;
                    for (Contact c : contactList)
                        if (c.getId() == id)
                        {
                            c.addMessage(m);
                            if (!c.getName().equals(currentName))
                            {
                                output.setOnlyForCurrent(false);
                                c.setNewMessage(forNewMessages);
                            }
                            output.setNewMessage(true);
                            break;
                        }
                }
            }
            else
            {
                StringBuilder nameBuilder = new StringBuilder("Chat_");
                List<Integer> users = m.getUsers();
                for(int id : users) nameBuilder.append(id);

                Contact c = GetContactByLogin(nameBuilder.toString());
                if(c == null)
                {
                    c = new Contact(true, nameBuilder.toString(), m.getUsers());
                    contactList.add(c);
                    android.util.Log.v("GROUP DA", c.getLogin());
                    Helper.startTask(new AddContactTask(mainActivity, c.getLogin(), c.getName(), EditDialog.MODE.ADD.getMode()));
                }

                c.addMessage(m);
                if (!c.getName().equals(currentName))
                {
                    output.setOnlyForCurrent(false);
                    c.setNewMessage(forNewMessages);
                }
                output.setNewMessage(true);
                break;
            }
        }
        messageList.clear();
        return output;
    }

    private void UpdateContactStatuses(List<StatusUpdateResponse> update)
    {
        for (StatusUpdateResponse u : update)
            for (Contact c : contactList)
                if (c.getId() == u.getUser_id())
                {
                    c.Update(u);
                    break;
                }
        mainActivity.UpdateView();
    }

    public List<Contact> GetContactList()
    {
        return contactList;
    }

    public Contact GetContactByLogin(String login)
    {
        Contact theContact = null;
        for (Contact c : contactList) if (c.getLogin().equals(login)) theContact = c;
        return theContact;
    }

    public void ClearContactList()
    {
        if (contactList != null && contactList.size() > 0)
            contactList.clear();
    }

    public void DownloadContactList()
    {
        Helper.startTask(new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                contactList = NetClient.GetInstance().GetContactList();
                GetArchive();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);
                mainActivity.UpdateView();
            }
        });
    }

    private void GetArchive()
    {
        long to = System.currentTimeMillis();
        long from = to - DAY_IN_MILLIS;
        messageList = NetClient.GetInstance().GetArchive(from, to);
        MergeMessagesWithContacts(false);
    }

    public void StartReceiving()
    {
        if (messageReceiver == null)
        {
            messageReceiver = new MessageReceiver();
            Helper.startTask(messageReceiver);
        }

        if (statusUpdater == null)
        {
            statusUpdater = new StatusUpdater();
            Helper.startTask(statusUpdater);
        }
    }

    public void StopReceiving()
    {
        if (statusUpdater != null)
        {
            statusUpdater.stop();
            statusUpdater.cancel(true);
            statusUpdater = null;
        }

        if (messageReceiver != null)
        {
            messageReceiver.stop();
            messageReceiver.cancel(true);
            messageReceiver = null;
        }
    }

    public void SetMainActivity(MainActivity mainActivity)
    {
        this.mainActivity = mainActivity;
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
            while (Run)
            {
                messageList = NetClient.GetInstance().GetMessage();
                UpdateMessageOutput output = MergeMessagesWithContacts(true);
                Helper.setChatItemUpdateStatus(!output.isOnlyForCurrent());
                if (output.isNewMessage()) mainActivity.UpdateView();
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
            while (Run)
            {
                List<StatusUpdateResponse> data = NetClient.GetInstance().GetStatusUpdate();
                if (data != null) UpdateContactStatuses(data);
            }
            return null;
        }
    }
}
