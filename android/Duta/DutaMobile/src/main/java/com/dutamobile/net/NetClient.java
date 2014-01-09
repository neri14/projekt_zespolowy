package com.dutamobile.net;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.NoCookieException;
import com.dutamobile.model.Status;
import com.dutamobile.model.response.LoginResponse;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.util.Helper;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NetClient
{
    private HttpURLConnection Client;
    private final String ServerAddress = "http://10.0.3.2:1404/Service";
    //private final String ServerAddress = "http://duta.somee.com/Service";

    private NetClient()
    {
        //Client = new DefaultHttpClient();
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
        Client.disconnect();
        Client = null;
        mInstance = null;
    }

    public LoginResponse Login(final String login, final String password) throws IOException
    {
        final String endpoint = "/Login";
        LoginResponse loginResponse = null;

        Client = (HttpURLConnection) new URL(ServerAddress + endpoint).openConnection();
        Client.setRequestMethod("POST");
        Client.setDoInput(true);
        Client.setDoOutput(true);

        // write our POST fields
        PrintWriter writer = new PrintWriter(Client.getOutputStream());
        writer.println("login=" + login + "&password=" + password + "&action=session_auth&https=1");
        writer.close();

        loginResponse = Helper.getObjectFromJson(Client.getInputStream(), LoginResponse.class);

        GetSessionCookie();

        return loginResponse;
    }

    private synchronized void GetSessionCookie()
    {
        List<String> cookieList = Client.getHeaderFields().get("Set-Cookie");
        if (cookieList != null)
        {
            for (String cookieTemp : cookieList)
            {
                CookieManager.getInstance().setCookie(Client.getURL().toString(), cookieTemp);
            }
        }
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
                   // Client.execute(post).getEntity().consumeContent();
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
            Client = (HttpURLConnection) new URL(ServerAddress + endpoint).openConnection();
            Client.setRequestMethod("POST");
            Client.setDoInput(true);
            Client.setDoOutput(true);

            String cookie = CookieManager.getInstance().getCookie(Client.getURL().toString());
            if (cookie != null)
            {
                Client.setRequestProperty("Cookie", cookie);
            }
            else
            {
                throw new NoCookieException(ServerAddress + endpoint);
            }

            Type type = new TypeToken<List<Contact>>(){}.getType();
            data = Helper.getObjectFromJson(Client.getInputStream(), type);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (ProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (NoCookieException e)
        {
            e.printStackTrace();
        }

        Client.disconnect();

        return data;
    }
/*
    public void PutContact(final String login, final String nickname, final boolean update )
    {
        final String endpointAdd = "/AddContact";
        final String endpointUpdate = "/UpdateContact";


        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    List<NameValuePair> data = new ArrayList<NameValuePair>();
                    data.add(new BasicNameValuePair("login", login));
                    data.add(new BasicNameValuePair("nickname", nickname));

                    HttpPost post = new HttpPost(ServerAddress + (update ? endpointUpdate : endpointAdd));
                    post.setEntity(new UrlEncodedFormEntity(data));

                    Client.execute(post).getEntity().consumeContent();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void RemoveContact(final String login)
    {
        final String endpoint = "/RemoveContact";

        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... params)
            {
                try
                {
                    List<NameValuePair> data = new ArrayList<NameValuePair>();
                    data.add(new BasicNameValuePair("login", login));

                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    post.setEntity(new UrlEncodedFormEntity(data));

                    Client.execute(post).getEntity().consumeContent();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
*/
    public List<StatusUpdateResponse> GetStatusUpdate()
    {
        final String endpoint = "/GetStatusUpdate";

        List<StatusUpdateResponse> statusUpdates = null;

        try
        {/*
            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                Type type = new TypeToken<List<StatusUpdateResponse>>(){}.getType();
                statusUpdates = Helper.getObjectFromJson(response, type);
            }

            response.getEntity().consumeContent();*/
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

                    //Client.execute(post).getEntity().consumeContent();
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

        long timestamp = -1;

        try
        {
            /*
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("message", message));
            for (int id : usersIds)
                data.add(new BasicNameValuePair("users", "" + id));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            post.setEntity(new UrlEncodedFormEntity(data));
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
                timestamp = getObjectFromJson(response, long.class);

            response.getEntity().consumeContent();
            */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return timestamp; // == null ? -1 : timestamp.getTimestamp();
    }

    public List<Message> GetMessage()
    {
        final String endpoint = "/GetMessage";

        List<Message> data = null;
/*
        try
        {
            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                Type type = new TypeToken<List<Message>>(){}.getType();
                data = getObjectFromJson(response, type);
            }

            response.getEntity().consumeContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
        return data;
    }
/*
    public Contact GetUserData(final int userId)
    {
        //FIXME

        final String endpoint = "/GetUserData";

        Contact user = null;

        AsyncTask <Void, Void, Contact> task = new AsyncTask<Void, Void, Contact>()
        {
            @Override
            protected Contact doInBackground(Void... params)
            {
                Contact user = null;

                try
                {
                    List<NameValuePair> data = new ArrayList<NameValuePair>();
                    data.add(new BasicNameValuePair("user_id", "" + userId));

                    HttpPost post = new HttpPost(ServerAddress + endpoint);
                    HttpResponse response = Client.execute(post);

                    if(response.getStatusLine().getStatusCode() == 200)
                    {
                        user = Helper.getObjectFromJson(response, Contact.class);
                    }

                    response.getEntity().consumeContent();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return user;
            }
        };

        return user;

    }

    public Contact GetUserData(final String login)
    {
        final String endpoint = "/GetUserData";

        Contact user = null;

        try
        {

            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("login", "" + login));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                user = Helper.getObjectFromJson(response, Contact.class);
            }

            response.getEntity().consumeContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return user;

    }
*/
    public void Ping(final boolean asyncPing)
    {
        final String endpoint = "/Ping";
        final String endpointAsync = "/PingAsync";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Client = (HttpURLConnection) new URL(ServerAddress + (asyncPing ? endpointAsync : endpoint)).openConnection();
                    Client.setRequestMethod("POST");
                    Client.setDoInput(true);
                    Client.setDoOutput(true);

                    String cookie = CookieManager.getInstance().getCookie(Client.getURL().toString());
                    if (cookie != null)
                    {
                        Client.setRequestProperty("Cookie", cookie);
                    }
                    else
                    {
                        throw new NoCookieException(ServerAddress + endpoint);
                    }
                    Client.connect();

                    GetSessionCookie();
                    Client.getContent();

                }
                catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch (ProtocolException e)
                {
                    e.printStackTrace();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (NoCookieException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }
/*
    public Object GetArchive(final long fromDate, final long toDate)
    {
        final String endpoint = "/GetArchive";

        Object archive = null;

        try
        {
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("from", "" + fromDate));
            data.add(new BasicNameValuePair("to", "" + toDate));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                archive = Helper.getObjectFromJson(response, Object.class);
            }

            response.getEntity().consumeContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return archive;
    }

    public Object GetArchiveFilteredByUserName(final long fromDate, final long toDate, final String username)
    {
        final String endpoint = "/GetArchiveFilteredByUserName";

        Object archive = null;

        try
        {
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("from", "" + fromDate));
            data.add(new BasicNameValuePair("to", "" + toDate));
            data.add(new BasicNameValuePair("username", username));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                archive = Helper.getObjectFromJson(response, Object.class);
            }

            response.getEntity().consumeContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return archive;
    }

    public Object GetArchiveFilteredByUserId(final long fromDate, final long toDate, final int userId)
    {
        final String endpoint = "/GetArchiveFilteredByUserId";

        Object archive = null;

        try
        {
            List<NameValuePair> data = new ArrayList<NameValuePair>();
            data.add(new BasicNameValuePair("from", "" + fromDate));
            data.add(new BasicNameValuePair("to", "" + toDate));
            data.add(new BasicNameValuePair("userid", userId + ""));

            HttpPost post = new HttpPost(ServerAddress + endpoint);
            HttpResponse response = Client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200)
            {
                archive = Helper.getObjectFromJson(response, Object.class);
            }

            response.getEntity().consumeContent();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return archive;
    }
*/
}

