package com.dutamobile.model.response;

/**
 * Created by Bartosz on 20.11.13.
 */
public class StatusUpdateResponse
{
    private int user_id;
    private int status;
    private String description;

    public int getUser_id()
    {
        return user_id;
    }

    public int getStatus()
    {
        return status;
    }

    public String getDescription()
    {
        return description;
    }
}
