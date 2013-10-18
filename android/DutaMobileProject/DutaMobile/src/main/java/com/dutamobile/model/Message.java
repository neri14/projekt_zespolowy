package com.dutamobile.model;

/**
 * Created by Bartosz on 13.10.13.
 */
public class Message
{
    public Message()
    {
        this.isReceived = false;
        this.messageText = null;
    }

    public Message(String messageText, Boolean isReceived)
    {
        this.isReceived = isReceived;
        this.messageText = messageText;
    }

    private Boolean isReceived;

    public String getMessageText()
    {
        return messageText;
    }

    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    public Boolean isReceived()
    {
        return isReceived;
    }

    public void setIncoming(Boolean isReceived)
    {
        this.isReceived = isReceived;
    }

    private String messageText;
}
