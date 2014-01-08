package com.dutamobile.model;

import java.util.List;

/**
 * Created by Bartosz on 01.12.13.
 */
public class ActiveChat
{
    private String ChatName;

    public String getChatName()
    {
        return ChatName;
    }

    public void setChatName(String chatName)
    {
        ChatName = chatName;
    }

    public ActiveChat(Contact contact)
    {
        this.Checked = false;
        this.ChatId = contact.getId();
        this.ChatDisplayName = contact.getName();
        this.ChatName = "Chat-" + ChatDisplayName;
        this.MessageList = contact.getMessages();
    }

    public int getChatId()
    {
        return ChatId;
    }

    public void setChatId(int chatId)
    {
        ChatId = chatId;
    }

    public String getChatDisplayName()
    {
        return ChatDisplayName;
    }

    public void setChatDisplayName(String chatDisplayName)
    {
        ChatDisplayName = chatDisplayName;
    }

    private int ChatId;
    private String ChatDisplayName;

    public boolean isChecked()
    {
        return Checked;
    }

    public void setChecked(boolean checked)
    {
        Checked = checked;
    }

    private boolean Checked;

    public List<Message> getMessageList()
    {
        return MessageList;
    }

    public void setMessageList(List<Message> messageList)
    {
        MessageList = messageList;
    }

    private List<Message> MessageList;

    private ActiveChat()
    {
        Checked = false;
    }

    @Override
    public String toString()
    {
        return ChatDisplayName;
    }
}
