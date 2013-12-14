package com.dutamobile.util;

import android.os.AsyncTask;

import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;
import com.dutamobile.model.response.LoginResponse;
import com.dutamobile.model.response.MessageResponse;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.dutamobile.util.Helper.getObjectFromJson;

public class NetClient
{
    private HttpClient Client;
   // private final String ServerAddress = "http://10.0.3.2:1404/Service";
    private final String ServerAddress = "http://duta.somee.com/Service";

    private NetClient()
    {
        Client = new DefaultHttpClient();
    }

    private static NetClient mInstance = null;

    public static synchronized NetClient GetInstance()
    {
        if (mInstance == null)
        {
            mInstance = new NetClient();
        }
        return mInstance;
    }

    private void CloseConnection()
    {
        Client.getConnectionManager().shutdown();
        Client = null;
        mInstance = null;
    }

    public LoginResponse Login(final String login, final String password) throws IOException
    {
        final String endpoint = "/Login";

        List<NameValuePair> data = new ArrayList<NameValuePair>();
        data.add(new BasicNameValuePair("login", login));
        data.add(new BasicNameValuePair("password", password));

        HttpPost post = new HttpPost(ServerAddress + endpoint);
        post.setEntity(new UrlEncodedFormEntity(data));

        HttpResponse response = Client.execute(post);

        if(response.getStatusLine().getStatusCode() == 200)
            return getObjectFromJson(response, LoginResponse.class);
        return null;
    }

    public void Logout()
    {
        final String endpoint = "/Logout";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    Client.execute(post);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();

        CloseConnection();
    }

    public List<Contact> GetContactList()
    {
        final String endpoint = "/GetContactList";

        List<Contact> data = null;

        try
        {
            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                Type type = new TypeToken<List<Contact>>(){}.getType();
                data = getObjectFromJson(response, type);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return data;
}

    public List<StatusUpdateResponse> GetStatusUpdate()
    {
        final String endpoint = "/GetStatusUpdate";

        List<StatusUpdateResponse> statusUpdates = null;

        try
        {
            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                Type type = new TypeToken<List<StatusUpdateResponse>>(){}.getType();
                statusUpdates = Helper.getObjectFromJson(response, type);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return statusUpdates;

    }

    public void SetStatus(final Status status, final String description)
    {
        //TODO ogarnąć czy działa
        final String endpoint = "/SetStatus";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    List<NameValuePair> data = new ArrayList<NameValuePair>();
                    data.add(new BasicNameValuePair("status", "" + status.ordinal()));
                    data.add(new BasicNameValuePair("description", description));

                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    post.setEntity(new UrlEncodedFormEntity(data));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public long SendMessage(final String message, final int... usersIds)
    {
        final String endpoint = "/SendMessage";

        MessageResponse timestamp = null;

        try
        {
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("message", message));
            for (int id : usersIds)
                data.add(new BasicNameValuePair("users", "" + id));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            post.setEntity(new UrlEncodedFormEntity(data));

            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
                timestamp = getObjectFromJson(response, MessageResponse.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return timestamp == null ? -1 : timestamp.getTimestamp();
    }

    public List<Message> GetMessage()
    {
        final String endpoint = "/GetMessage";

        List<Message> data = null;

        try
        {
            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                Type type = new TypeToken<List<Message>>(){}.getType();
                data = getObjectFromJson(response, type);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public void Ping()
    {
        final String endpoint = "/Ping";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    Client.execute(post);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public void PingAsync()
    {
        final String endpoint = "/PingAsync";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    Client.execute(post);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }


}

