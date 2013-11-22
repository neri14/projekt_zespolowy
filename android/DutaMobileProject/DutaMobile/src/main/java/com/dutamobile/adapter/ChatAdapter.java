package com.dutamobile.adapter;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dutamobile.DutaApplication;
import com.dutamobile.R;
import com.dutamobile.model.Message;
import com.dutamobile.util.Helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bartosz on 13.10.13.
 */
public class ChatAdapter extends BaseAdapter //implements ListAdapter
{
    static class ViewHolder
    {
        RelativeLayout container;
        TextView messageView;
        TextView usernameView;
        TextView timestampView;
        RelativeLayout.LayoutParams params1;
    }

    private List<Message> data;
    private final LayoutInflater inflater;
    private int width;
    private HashMap<Integer, String> usernames;

    public ChatAdapter(Context context, List<Message> messages, HashMap<Integer, String> usernames)
    {
        this.usernames = usernames;
        this.data = messages;
        this.inflater = LayoutInflater.from(context);

        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;

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
            holder.container = (RelativeLayout) convertView.findViewById(R.id.chat_container);
            holder.messageView = (TextView) convertView.findViewById(R.id.text_1);
            holder.usernameView = (TextView) convertView.findViewById(R.id.text_3);
            holder.messageView.setMaxWidth((int) (0.75 * width));
            holder.timestampView = (TextView) convertView.findViewById(R.id.text_2);
            holder.params1 = (RelativeLayout.LayoutParams) holder.messageView.getLayoutParams();
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
            holder.timestampView.setText(msg.getDate());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

            if(msg.getAuthor() != Helper.MyID)
            {
                holder.usernameView.setText(usernames.get(msg.getAuthor()) + ":");
                holder.container.setBackgroundResource(R.drawable.receive_msg_bg);

                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                holder.container.setGravity(Gravity.LEFT);
            }
            else
            {
                holder.usernameView.setText("Ja:");
                holder.container.setBackgroundResource(R.drawable.send_msg_bg);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                holder.container.setGravity(Gravity.RIGHT);
            }

            holder.container.setLayoutParams(params);
        }
        return convertView;
    }
}

