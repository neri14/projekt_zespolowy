package com.dutamobile.model;

/**
 * Created by Bartosz on 09.01.14.
 */
public class NoCookieException extends Exception
{
    String URL;

    public NoCookieException(String url)
    {
       URL = url;
    }

    @Override
    public void printStackTrace()
    {
        super.printStackTrace();

        android.util.Log.e("No Cookie", "There's no cookie for " + URL);
    }
}
