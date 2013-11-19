package com.dutamobile.service;

import android.os.AsyncTask;

import com.dutamobile.util.NetClient;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Bartosz on 17.11.13.
 */
public class ReceiveMessage extends AsyncTask<Void, Void, Void>
{
    private boolean Run = true;
    @Override
    protected Void doInBackground(Void... params)
    {
        while(Run)
        {
            NetClient.GetInstance().GetMessage();
        }

        return null;
    }
}
