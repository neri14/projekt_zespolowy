package com.dutamobile.util;

import android.app.Activity;
import android.os.AsyncTask;

import com.dutamobile.DutaApplication;
import com.dutamobile.MainActivity;
import com.dutamobile.net.NetClient;

import java.io.IOException;

/**
 * Created by Bartosz on 22.01.14.
 */
public class AddContactTask extends AsyncTask<Object, Void, Boolean>
{
    Activity activity;
    String contactLogin;
    String nick;
    boolean mode;

    public AddContactTask(Activity activity, String contactLogin, String nick,  Boolean mode)
    {
        this.activity = activity;
        this.contactLogin = contactLogin;
        this.nick = nick;
        this.mode = mode;
    }

    @Override
    protected Boolean doInBackground(Object... params)
    {
        try
        {
            return NetClient.GetInstance().PutContact(contactLogin, nick, mode);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result)
    {
        super.onPostExecute(result);
        if (result)
            if (mode)
            {
                ((DutaApplication)activity.getApplication())
                        .GetContactByLogin(contactLogin).setName(nick);
                ((MainActivity) activity).UpdateView();
            }
            else ((DutaApplication)activity.getApplication()).DownloadContactList();
    }
}