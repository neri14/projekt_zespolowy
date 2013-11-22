package com.dutamobile.model.response;

/**
 * Created by Bartosz on 20.11.13.
 */
public class StatusUpdateResponse
{
    public void setUser_id(int user_id)
    {
        this.user_id = user_id;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

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
