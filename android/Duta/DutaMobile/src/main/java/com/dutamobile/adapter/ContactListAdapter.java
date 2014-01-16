package com.dutamobile.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dutamobile.R;
import com.dutamobile.model.Contact;
import com.dutamobile.util.Helper;

import java.util.List;

/**
 * Created by Bartosz on 12.10.13.
 */
public class ContactListAdapter extends BaseAdapter
{
    private final LayoutInflater inflater;
    private List<Contact> data;
    private Context context;
    private SparseBooleanArray mSelectedItemsIds;

    public ContactListAdapter(Context context, List<Contact> data)
    {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.data = data;
        mSelectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount()
    {
        return data != null ? data.size() : 0;
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

    public void setData(List<Contact> data)
    {
        this.data = data;
        notifyDataSetChanged();
    }

    public boolean removeItem(Contact contact)
    {
        return data.remove(contact);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.contact_list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.text_1);
            holder.status = (ImageView) convertView.findViewById(R.id.image_1);
            holder.desc = (TextView) convertView.findViewById(R.id.text_2);
            convertView.setTag(holder);
        } else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Contact c = data.get(position);

        if (c != null)
        {
            holder.name.setText(c.getName());
            holder.status.setImageDrawable(Helper.getStatusIndicator(context, c.getStatus()));
            holder.desc.setText(c.getDescription());

            convertView.setBackgroundColor(mSelectedItemsIds.get(position) ? 0x8834B5E4 : Color.TRANSPARENT);
        }
        return convertView;
    }


    //CAB Method
    public void toggleSelection(int position)
    {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection()
    {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value)
    {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount()
    {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds()
    {
        return mSelectedItemsIds;
    }

    static class ViewHolder
    {
        TextView name;
        TextView desc;
        ImageView status;
    }
}
