package com.dutamobile.model;

import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.util.Helper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bartosz on 12.10.13.
 */
public class Contact implements Serializable
{
    boolean groupConversation = false;

    public Contact()
    {
        messages = new ArrayList<Message>();
    }

    public Contact(Boolean groupConversation, String login, List<Integer> ids )
    {
        this.login = login;
        id = Integer.parseInt(login.substring(5));
        name = "Konwersacja " + id;
        status = Status.AVAILABLE.ordinal();
        messages = new ArrayList<Message>();
        this.groupConversation = groupConversation;
        try
        {
            ids.remove((Integer) Helper.MyID);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        this.ids = new int[ids.size()];
        for (int i = 0 ; i < ids.size() ; i++)
            this.ids[i] = ids.get(i);


    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Status getStatus()
    {
        return Status.values()[status];
    }

    public void setStatus(int status)
    {
        this.status = status;
    }

    public void setStatus(Status status)
    {
        this.status = status.ordinal();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    public void addMessage(Message message) { this.messages.add(message); }

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    @SerializedName("user_id")
    private int id;
    @SerializedName("login")
    private String login;
    @SerializedName("nickname")
    private String name;
    @SerializedName("status")
    private int status;
    @SerializedName("description")
    private String description;

    private int[] ids;

    private List<Message> messages;

    public boolean haveNewMessages()
    {
        return newMessage;
    }

    public void setNewMessage(boolean newMessage)
    {
        this.newMessage = newMessage;
    }

    private boolean newMessage = false;

    public void Update(StatusUpdateResponse update)
    {
        status = update.getStatus();
        description = update.getDescription();
    }

    public int[] getIdArray()
    {
        return groupConversation ?  ids : new int [] { id, Helper.MyID };
    }

    public boolean isGroupConversation()
    {
        return groupConversation;
    }

}
