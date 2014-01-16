package com.dutamobile.adapter;

/**
 * Created by Bartosz on 07.11.13.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dutamobile.R;
import com.dutamobile.model.ActiveChat;
import com.dutamobile.model.Contact;

import java.util.List;
/**
 * Created by Bartosz on 07.11.13.
 */
public class ActiveConversationsAdapter extends BaseAdapter
{
    public boolean isAnyItemsChecked()
    {
        return itemsChecked > 0;
    }

    private int itemsChecked;

    static class ViewHolder
    {
        TextView text;
    }

    private final LayoutInflater inflater;
    private List<ActiveChat> data;

    public ActiveConversationsAdapter(Context context, List<ActiveChat> activeConversations)
    {
        this.inflater = LayoutInflater.from(context);
        this.data = activeConversations;
    }

    @Override
    public int getCount()
    {
        return data.size();
    }

    public void addItem(Contact contact)
    {
        String name = contact.getName();

        for(ActiveChat ac : data )
            if(ac.getChatDisplayName().equals(name)) return;

        data.add(new ActiveChat(contact));
        notifyDataSetChanged();
    }

    public void deleteItem(int position)
    {
        if(data.get(position).isChecked()) itemsChecked--;

        data.remove(position);
        notifyDataSetChanged();
    }


    @Override
    public Object getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return data.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if(convertView == null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.active_chat_list_item, null);
            holder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.text.setText(data.get(position).getChatDisplayName());

        boolean checked = data.get(position).isChecked();
        convertView.setBackgroundColor(checked ? Color.argb(128, 200, 200, 200) : Color.TRANSPARENT);

        if(checked) itemsChecked++;

        return convertView;
    }

    @Override
    public void notifyDataSetInvalidated()
    {
        super.notifyDataSetInvalidated();
        itemsChecked = 0;
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        itemsChecked = 0;
    }
}