package com.dutamobile.model;

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

    private int id;
    private String name;
    private Status status;
}
