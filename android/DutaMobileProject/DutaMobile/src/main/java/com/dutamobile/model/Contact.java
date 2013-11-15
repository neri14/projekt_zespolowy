package com.dutamobile.model;

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
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
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

    private int id;
    private String name;
    private Status status;
    private String description;
    private List<Message> messages;
}
