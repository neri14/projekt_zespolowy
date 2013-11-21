package com.dutamobile.model.response;

/**
 * Created by Bartosz on 17.11.13.
 */
public class MessageResponse
{
    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    private long timestamp;
}
