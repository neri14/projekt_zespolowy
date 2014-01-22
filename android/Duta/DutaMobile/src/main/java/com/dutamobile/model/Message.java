package com.dutamobile.model;

import com.dutamobile.util.Helper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Bartosz on 13.10.13.
 */
public class Message implements Serializable
{
    public Message(String messageText, List<Contact> contactList)
    {
        this.users = new ArrayList<Integer>();

        if (contactList != null)
        {
            for (Contact c : contactList)
                this.users.add(c.getId());
        }

        this.messageText = messageText;
    }

    public Message(String messageText, int[] contactList)
    {
        this.users = new ArrayList<Integer>();

        if (contactList != null)
        {
            for (int i : contactList)
                this.users.add(i);
        }

        this.messageText = messageText;

        this.author = Helper.MyID;
    }

    public Message(String messageText, ArrayList<Integer> contactList)
    {
        this.users = contactList;

        this.messageText = messageText;

        this.author = Helper.MyID;
    }

    public String getMessageText()
    {
        return messageText;
    }

    public void setMessageText(String messageText)
    {
        this.messageText = messageText;
    }

    public List<Integer> getUsers()
    {
        return users;
    }

    public void setUsers(List<Integer> users)
    {
        this.users = users;
    }

    public long getTimestamp()
    {
        return timestamp;
    }

    public String getDate()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public int getAuthor()
    {
        return author;
    }

    public void setAuthor(int author)
    {
        this.author = author;
    }

    private int author;
    private long timestamp;
    @SerializedName("message")
    private String messageText;
    private List<Integer> users;
}
