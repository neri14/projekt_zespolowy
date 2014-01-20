package com.dutamobile.net;

import android.os.AsyncTask;
import android.webkit.CookieManager;

import com.dutamobile.model.Contact;
import com.dutamobile.model.Message;
import com.dutamobile.model.NoCookieException;
import com.dutamobile.model.Status;
import com.dutamobile.model.response.LoginResponse;
import com.dutamobile.model.response.MessageResponse;
import com.dutamobile.model.response.StatusUpdateResponse;
import com.dutamobile.util.Helper;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NetClient
{
    private static NetClient mInstance = null;

    //private String ServerAddress = "http://10.0.3.2:1404/Service";
    private final String ServerAddress = "http://duta.hostingasp.pl/Service";

    private NetClient() {}

    public static synchronized NetClient GetInstance()
    {
        if (mInstance == null) mInstance = new NetClient();
        return mInstance;
    }

    private synchronized HttpURLConnection CreatePostRequest(String endpoint, String postData, boolean addCookie, String requestMethod) throws IOException
    {
        HttpURLConnection client = (HttpURLConnection) new URL(ServerAddress + endpoint).openConnection();
        client.setRequestMethod(requestMethod);
        client.setDoInput(true);
        client.setDoOutput(true);

        if (addCookie)
        {
            String cookie = CookieManager.getInstance().getCookie(client.getURL().toString());
            if (cookie != null) client.setRequestProperty("Cookie", cookie);
            else new NoCookieException(ServerAddress + endpoint).printStackTrace();
        }
        // write our POST fields
        if (postData != null)
        {
            PrintWriter writer = new PrintWriter(client.getOutputStream());
            writer.println(postData + "&action=session_auth&https=1");
            writer.flush();
            writer.close();
        }
        return client;
    }

    private synchronized void GetSessionCookie(HttpURLConnection client)
    {
        List<String> cookieList = client.getHeaderFields().get("Set-Cookie");
        if (cookieList != null)
            for (String cookieTemp : cookieList)
                CookieManager.getInstance().setCookie(client.getURL().toString(), cookieTemp);
        CookieManager.getInstance().removeExpiredCookie();
    }

    public LoginResponse Login(final String login, final String password) throws IOException
    {
        final String endpoint = "/Login";
        LoginResponse loginResponse;
        String postData = String.format("login=%s&password=%s", login, password);
        HttpURLConnection client = CreatePostRequest(endpoint, postData, false, "POST");
        loginResponse = Helper.getObjectFromJson(client.getInputStream(), LoginResponse.class);
        GetSessionCookie(client);
        return loginResponse;
    }

    public void Logout()
    {
        final String endpoint = "/Logout";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    HttpURLConnection client = CreatePostRequest(endpoint, null, true, "POST");
                    client.connect();
                    if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        CookieManager.getInstance().removeSessionCookie();
                        CookieManager.getInstance().removeExpiredCookie();
                    }
                    client.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, "LogoutThread").start();
    }

    public List<Contact> GetContactList()
    {
        final String endpoint = "/GetContactList";
        List<Contact> data = null;
        try
        {
            HttpURLConnection client = CreatePostRequest(endpoint, null, true, "POST");
            client.setFixedLengthStreamingMode(0);
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<Contact>>() {}.getType();
                data = Helper.getObjectFromJson(client.getInputStream(), type);
            }
            client.disconnect();
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch(ProtocolException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        return data;
    }

    public synchronized boolean PutContact(final String login, final String nickname, final boolean update) throws IOException
    {
        final String endpointAdd = "/AddContact";
        final String endpointUpdate = "/UpdateContact";

        HttpURLConnection client = CreatePostRequest(update ? endpointUpdate : endpointAdd,
                "login=" + login + "&nickname=" + nickname,
                true, "POST");

        client.connect();
        boolean updateComplete = client.getResponseCode() == HttpURLConnection.HTTP_OK;
        client.disconnect();

        return updateComplete;
    }

    public synchronized void RemoveContact(final Contact contact)
    {
        final String endpoint = "/RemoveContact";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    HttpURLConnection client = CreatePostRequest(endpoint, "login=" + contact.getLogin(), true, "POST");
                    client.getResponseCode();
                    client.disconnect();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }, "RemoveContactThread").start();
    }

    public List<StatusUpdateResponse> GetStatusUpdate()
    {
        final String endpoint = "/GetStatusUpdate";
        List<StatusUpdateResponse> statusUpdates = null;
        try
        {
            HttpURLConnection client = CreatePostRequest(endpoint, null, true, "POST");
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<StatusUpdateResponse>>() {}.getType();
                statusUpdates = Helper.getObjectFromJson(client.getInputStream(), type);
                GetSessionCookie(client);
            }
            client.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return statusUpdates;
    }

    public void SetStatus(final Status status, final String description)
    {
        final String endpoint = "/SetStatus";

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String postData = String.format("status=%d&description=%s", status.ordinal(), description);
                    HttpURLConnection client = CreatePostRequest(endpoint, postData, true, "GET");
                    client.getResponseCode();
                    client.disconnect();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public synchronized long SendMessage(final String message, final int... usersIds)
    {
        final String endpoint = "/SendMessage";

        MessageResponse timestamp = null;
        try
        {
            StringBuilder postData = new StringBuilder();
            postData.append("message=");
            postData.append(message);
            for (int id : usersIds)
            {
                postData.append("&users=");
                postData.append(id);
            }
            HttpURLConnection client = CreatePostRequest(endpoint, postData.toString(), true, "POST");
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
                timestamp = Helper.getObjectFromJson(client.getInputStream(), MessageResponse.class);
        }
        catch(Exception e)
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
            HttpURLConnection client = CreatePostRequest(endpoint, null, true, "POST");

            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<Message>>() {}.getType();
                data = Helper.getObjectFromJson(client.getInputStream(), type);
            }
            client.disconnect();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return data;
    }

    public Contact GetUserData(final int userId) { return GetUserData(userId, null); }

    public Contact GetUserData(final String login) { return GetUserData(-1, login); }

    private Contact GetUserData(final int userId, final String login)
    {
        final String endpoint = "/GetUserData";
        Contact user = null;
        String postData;
        if (userId == -1 && login == null)
            throw new IllegalArgumentException("You can use ONLY ONE of parameter: userId OR login");
        if (login == null) postData = "user_id=" + userId;
        else postData = "login=" + login;

        AsyncTask<String, Void, Contact> task = new AsyncTask<String, Void, Contact>()
        {
            @Override
            protected Contact doInBackground(String... params)
            {
                try
                {
                    HttpURLConnection client = CreatePostRequest(endpoint, params[0], true, "POST");
                    if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
                    {
                        return Helper.getObjectFromJson(client.getInputStream(), Contact.class);
                    }
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
                return null;
            }
        };
        task.execute(postData);

        try
        {
            user = task.get(10, TimeUnit.SECONDS);
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        catch(ExecutionException e)
        {
            e.printStackTrace();
        }
        catch(TimeoutException e)
        {
            e.printStackTrace();
        }
        return user;
    }

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
                    HttpURLConnection client = CreatePostRequest((asyncPing ? endpointAsync : endpoint), null, true, "POST");
                    if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
                        GetSessionCookie(client);
                    client.disconnect();
                }
                catch(MalformedURLException e)
                {
                    e.printStackTrace();
                }
                catch(ProtocolException e)
                {
                    e.printStackTrace();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public List<Message> GetArchive(final long fromDate, final long toDate)
    {
        final String endpoint = "/GetArchive";
        List<Message> archive = null;
        try
        {
            String postData = String.format("from=%d&to=%d", fromDate, toDate);
            HttpURLConnection client = CreatePostRequest(endpoint, postData, true, "POST");
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<Message>>() {}.getType();
                archive = Helper.getObjectFromJson(client.getInputStream(), type);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return archive;
    }

    public List<Message> GetArchiveFilteredByUserName(final long fromDate, final long toDate, final String username)
    {
        final String endpoint = "/GetArchiveFilteredByUserName";
        List<Message> archive = null;
        try
        {
            String postData = String.format("from=%d&to=%d&username=%s", fromDate, toDate, username);
            HttpURLConnection client = CreatePostRequest(endpoint, postData, true, "POST");
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<Message>>() {}.getType();
                archive = Helper.getObjectFromJson(client.getInputStream(), type);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return archive;
    }

    public List<Message> GetArchiveFilteredByUserId(final long fromDate, final long toDate, final int userId)
    {
        final String endpoint = "/GetArchiveFilteredByUserId";
        List<Message> archive = null;
        try
        {
            String postData = String.format("from=%d&to=%d&userid=%d", fromDate, toDate, userId);
            HttpURLConnection client = CreatePostRequest(endpoint, postData, true, "POST");
            if (client.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                Type type = new TypeToken<List<Message>>() {}.getType();
                archive = Helper.getObjectFromJson(client.getInputStream(), type);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return archive;
    }
}

