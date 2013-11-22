package com.dutamobile.model;

import com.dutamobile.model.response.StatusUpdateResponse;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Bartosz on 12.10.13.
 */
public class Contact
{
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

    private List<Message> messages;

    public void Update(StatusUpdateResponse update)
    {
        status = update.getStatus();
        description = update.getDescription();
    }
}
