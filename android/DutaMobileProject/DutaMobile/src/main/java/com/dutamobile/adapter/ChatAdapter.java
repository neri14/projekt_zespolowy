package com.dutamobile.adapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dutamobile.R;
import com.dutamobile.model.Message;

import java.util.List;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatAdapter extends BaseAdapter //implements ListAdapter
{
    static class ViewHolder
    {
        TextView messageView;
    }

    private List<Message> data;
    private LayoutInflater inflater;
    private int height;
    private int width;
    private Context ctx;

    public ChatAdapter(Context context, List<Message> messages)
    {
        this.data = messages;
        this.inflater = LayoutInflater.from(context);
        this.ctx = context;

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels;

    }

    public void addMessage(Message message)
    {
        data.add(message);
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return data.size();
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
            convertView = inflater.inflate(R.layout.chat_item, parent, false);
            holder.messageView = (TextView) convertView.findViewById(R.id.chat_text_1);
            holder.messageView.setMaxWidth((int) (0.75 * width));
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        Message msg = data.get(position);

        if(msg != null)
        {
            holder.messageView.setText(msg.getMessageText());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if(msg.isReceived())
            {
                holder.messageView.setBackgroundResource(R.drawable.receive_msg_bg);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.messageView.setGravity(Gravity.LEFT);
            }
            else
            {
                holder.messageView.setBackgroundResource(R.drawable.send_msg_bg);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.messageView.setGravity(Gravity.RIGHT);
            }

            holder.messageView.setLayoutParams(params);
            holder.messageView.setPadding(8,8,8,8);
        }
        return convertView;
    }
}

