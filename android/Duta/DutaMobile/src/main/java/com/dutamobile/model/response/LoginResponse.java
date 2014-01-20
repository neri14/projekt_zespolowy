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

    public int getUser_id()
    {
        return user_id;
    }

    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    private int user_id;
}
