package com.dutamobile.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.dutamobile.R;
import com.dutamobile.model.Status;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * Created by Bartosz on 12.10.13.
 */
public class Helper
{
    private static boolean ChatIconStatus = false;
    public static int MyID;
    public static String PREFS_PRIVATE;
    public static final String PREFS_MAIN = "main-prefs";
    public static String CURRENT_FRAGMENT;
    private static ActionBar actionBar = null;
    private static Gson gson = null;
    private static final int[] statusIcons = new int[]
            {
                    R.drawable.status_available,
                    R.drawable.status_away,
                    R.drawable.status_offline
            };

    public static void fragmentReplacement(FragmentManager fragmentManager, Class fragmentClass, boolean addToBackStack, String tag, Bundle passedData)
    {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        try
        {
            if (fragment == null)
            {
                android.util.Log.i("Fragment Replacement", String.format("Creating new instance of %s...", fragmentClass.getSimpleName()));
                fragment = (Fragment) fragmentClass.newInstance();
                android.util.Log.i("Fragment Replacement", String.format("%s created.", fragmentClass.getSimpleName()));

                if (passedData != null)
                {
                    fragment.setArguments(passedData);
                }
            }
            else
                android.util.Log.i("Fragment Replacement", String.format("%s already exists.", fragmentClass.getSimpleName()));
        }
        catch(Exception e)
        {
            android.util.Log.e("Fragment Replacement", String.format("Creating new instance of %s failed.\n%s", fragmentClass.getSimpleName(), e.getMessage()));
        }

        if (fragment.isVisible())
        {
            android.util.Log.i("Fragment Replacement", String.format("No replacement - %s is already shown.", fragmentClass.getSimpleName()));
            return;
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.content_frame, fragment, tag);

        CURRENT_FRAGMENT = tag;

        if (addToBackStack)
        {
            transaction.addToBackStack(tag);
        }

        transaction.commit();
    }

    public synchronized static ActionBar getSupportActionBar(Activity actionBarActivity)
    {
        if (actionBar == null)
            if (actionBarActivity instanceof ActionBarActivity)
                actionBar = ((ActionBarActivity) actionBarActivity).getSupportActionBar();
        return actionBar;
    }

    public static synchronized boolean getChatItemUpdateStatus()
    {
        return ChatIconStatus;
    }

    public static synchronized void setChatItemUpdateStatus(boolean state)
    {
        ChatIconStatus = state;
    }

    public static Drawable getStatusIndicator(Context context, Status status)
    {
        return context.getResources().getDrawable(statusIcons[status.ordinal()]);
    }

    public static <T> void startTask(AsyncTask<T, ?, ?> asyncTask, T... params)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    //JSON
    public synchronized static Gson getGsonInstance()
    {
        if (gson == null)
            gson = new Gson();
        return gson;
    }

    private static String getJsonFromResponse(InputStream content) throws IOException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        for (String line; (line = reader.readLine()) != null; ) builder.append(line).append("\n");

        return builder.toString();
    }

    public static <T> T getObjectFromJson(InputStream response, Type type) throws IOException
    {
        String json = getJsonFromResponse(response);
        return getGsonInstance().fromJson(json, type);
    }
}
