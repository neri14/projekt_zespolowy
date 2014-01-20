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

    private boolean onlyForCurrent;
}
