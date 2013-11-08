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
    private static final int [] statusIcons = new int[]
            {
                    R.drawable.status_available,
                    R.drawable.status_away,
                    R.drawable.status_busy,
                    R.drawable.status_offline
            };



    public static void fragmentReplacement(FragmentManager fragmentManager, Class fragmentClass, boolean addToBackStack, String tag)
    {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);

        try
        {
            if(fragment == null)
            {
                android.util.Log.i("Fragment Replacement", "Creating new instance of fragment...");
                fragment = (Fragment) fragmentClass.newInstance();
                android.util.Log.i("Fragment Replacement", "Fragment created.");
            }
        }
        catch(Exception e)
        {
            android.util.Log.e("Fragment Replacement", "Creating new instance of fragment failed. " + e.getMessage());
        }


        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content_frame, fragment, tag);
        CURRENT_FRAGMENT = tag;

        if (addToBackStack)
        {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    public synchronized static ActionBar getSupportActionBar(Activity actionBarActivity)
    {
        if(actionBar == null)
            if(actionBarActivity instanceof ActionBarActivity)
                actionBar = ((ActionBarActivity)actionBarActivity).getSupportActionBar();
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
