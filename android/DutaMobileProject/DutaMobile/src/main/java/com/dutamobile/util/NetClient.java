package com.dutamobile.util;

import android.os.AsyncTask;

import com.dutamobile.model.Contact;
import com.dutamobile.model.response.LoginResponse;
import com.dutamobile.model.Message;
import com.dutamobile.model.Status;
import com.dutamobile.model.response.MessageRespone;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.dutamobile.util.Helper.*;


public class NetClient
{
    private HttpClient Client;
    private final String ServerAddress = "http://10.0.3.2:1404/Service";
    private final long TIME_OUT = 10000;

    private NetClient()
    {
        Client = new DefaultHttpClient();
    }

    private static NetClient mInstance = null;

    public static synchronized NetClient GetInstance() {
        if (mInstance == null) {
            mInstance = new NetClient();
        }
        return mInstance;
    }

    public Boolean Login(String login, String password) throws ExecutionException, InterruptedException, TimeoutException
    {
        final String endpoint = "/Login";

        return new AsyncTask<String, Void, Boolean>()
        {
            @Override
            protected Boolean doInBackground(String... params)
            {
                LoginResponse lr = null;

                try
                {
                    List<NameValuePair> data = new ArrayList<NameValuePair>();
                    data.add(new BasicNameValuePair("login", params[0]));
                    data.add(new BasicNameValuePair("password", params[1]));

                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    post.setEntity(new UrlEncodedFormEntity(data));

                    HttpResponse response = Client.execute(post);

                    lr = getObjectFromJson(response, LoginResponse.class);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return lr != null && lr.isLoggedIn() == 1;

            }
        }.execute(login, password).get(TIME_OUT, TimeUnit.MILLISECONDS);

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
    }

    public List<Contact> GetContactList()
    {
        final String endpoint = "/GetContactList";

        try
        {
            return new AsyncTask<Void, Void, List<Contact>>()
            {
                @Override
                protected List<Contact> doInBackground(Void... params)
                {
                    List<Contact> data = null;

                    try
                    {
                        HttpPost post = new HttpPost(ServerAddress + endpoint);

                        HttpResponse response = Client.execute(post);

                        Type type = new TypeToken<List<Contact>>(){}.getType();
                        data = getObjectFromJson(response, type);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    return data;
                }
            }.execute().get(TIME_OUT, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void GetStatusUpdate()
    {
        final String endpoint = "/GetStatusUpdate";

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

    public void SetStatus(final Status status, final String description)
    {
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
                    data.add(new BasicNameValuePair("description", description ));

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

    public long SendMessage(final String message, final List<Integer> usersIds)
    {
        final String endpoint = "/SendMessage";

        try
        {
            return new AsyncTask<Void, Void, Long>()
            {
                MessageRespone timestamp = null;
                @Override
                protected Long doInBackground(Void... params)
                {
                    try
                    {
                        List<NameValuePair> data = new ArrayList<NameValuePair>();
                        data.add(new BasicNameValuePair("message", message));
                        for(int id : usersIds)
                            data.add(new BasicNameValuePair("users", "" + id));

                        HttpPost post = new HttpPost(ServerAddress + endpoint);
                        post.setEntity(new UrlEncodedFormEntity(data));

                        HttpResponse response = Client.execute(post);

                        timestamp = getObjectFromJson(response, MessageRespone.class);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    return timestamp.getTimestamp();
                }
            }.execute().get(TIME_OUT,TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }

        return -2;
    }

    public List<Message> GetMessage()
    {
        final String endpoint = "/GetMessage";

        try
        {
            return new AsyncTask<Void, Void, List<Message>>()
            {
                @Override
                protected List<Message> doInBackground(Void... params)
                {
                    List<Message> data = null;

                    try
                    {
                        HttpPost post = new HttpPost(ServerAddress + endpoint);
                        HttpResponse response = Client.execute(post);

                        Type type = new TypeToken<List<Message>>(){}.getType();
                        data = getObjectFromJson(response, type);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                    return data;
                }
            }.execute().get(TIME_OUT, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}
