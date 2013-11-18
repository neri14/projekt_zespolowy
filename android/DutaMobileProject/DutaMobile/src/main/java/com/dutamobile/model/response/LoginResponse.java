package com.dutamobile.model.response;

/**
 * Created by Bartosz on 15.11.13.
 */
public class LoginResponse
{
    public int isLoggedIn()
    {
        return logged_in;
    }

    public void setLoggedIn(int logged_in)
    {
        this.logged_in = logged_in;
    }

    private int logged_in;
}
