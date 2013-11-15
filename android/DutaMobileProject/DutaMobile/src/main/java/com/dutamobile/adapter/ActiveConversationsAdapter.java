package com.dutamobile.adapter;

/**
 * Created by Bartosz on 07.11.13.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dutamobile.R;
import com.dutamobile.model.Contact;

import java.util.List;
/**
 * Created by Bartosz on 07.11.13.
 */
public class ActiveConversationsAdapter extends BaseAdapter
{
    static class ViewHolder
    {
        TextView text;
        Fragment fragment;
    }

    private final LayoutInflater inflater;
    private List<Contact> data;

    public ActiveConversationsAdapter(Context context, List<Contact> activeConversations)
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
        if(!data.contains(contact))
        {
            data.add(contact);
            notifyDataSetChanged();
        }
    }

    public void deleteItem(int position)
    {
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

        holder.text.setText(data.get(position).getName());


        return convertView;
    }
}