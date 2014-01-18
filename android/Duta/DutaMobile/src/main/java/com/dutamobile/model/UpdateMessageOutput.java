package com.dutamobile.model;

/**
 * Created by Bartosz on 17.01.14.
 */
public class UpdateMessageOutput
{
    public UpdateMessageOutput()
    {
        newMessage = false;
        onlyForCurrent = true;
        count = 0;
    }

    private boolean newMessage;

    public boolean isOnlyForCurrent()
    {
        return onlyForCurrent;
    }

    public void setOnlyForCurrent(boolean onlyForCurrent)
    {
        this.onlyForCurrent = onlyForCurrent;
    }

    public boolean isNewMessage()
    {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage)
    {
        this.newMessage = newMessage;
    }

    public int getCount()
    {
        return count;
    }

    public void IncrementCount()
    {
        this.count++;
    }

    private boolean onlyForCurrent;
    private int count;
}
