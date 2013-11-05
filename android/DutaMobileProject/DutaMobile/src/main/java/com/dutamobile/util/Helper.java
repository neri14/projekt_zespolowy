package com.dutamobile.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.dutamobile.R;
import com.dutamobile.model.Status;
import com.google.gson.Gson;

/**
 * Created by Bartosz on 12.10.13.
 */
public class Helper
{
    public static String CURRENT_FRAGMENT;
    private static ActionBar actionBar = null;
    private static Gson gson = null;
    private static int [] statusIcons = new int[]
            {
                    R.drawable.status_available,
                    R.drawable.status_away,
                    R.drawable.status_busy,
                    R.drawable.status_offline
            };



    public static void fragmentReplacement(FragmentManager fragmentManager, Fragment fragment, boolean addToBackStack, String tag)
    {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content_frame, fragment, tag);
        CURRENT_FRAGMENT = tag;

        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public synchronized static ActionBar getSupportActionBar(Activity activity)
    {
        if(actionBar == null)
            if(activity instanceof ActionBarActivity)
                actionBar = ((ActionBarActivity)activity).getSupportActionBar();
        return actionBar;
    }

    public static Drawable getStatusIndicator(Context context, Status status)
    {
        return context.getResources().getDrawable(statusIcons[status.ordinal()]);
    }

    public synchronized static Gson getGsonInstance()
    {
        if(gson == null)
            gson = new Gson();
        return gson;
    }



}
