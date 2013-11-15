package com.dutamobile.util;

import android.os.AsyncTask;
import android.util.Log;

import com.dutamobile.model.Contact;
import com.dutamobile.model.LoginResponse;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class NetClient
{
    private HttpClient client = new DefaultHttpClient();

    private final String server = "http://10.0.3.2:1404/Service";

    private NetClient() {

    }

    private static NetClient mInstance = null;

    public static synchronized NetClient getInstance() {
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

                    HttpPost post = new HttpPost(server + endpoint);
                    post.setEntity(new UrlEncodedFormEntity(data));

                    HttpResponse response = client.execute(post);

                    lr = Helper.getObjectFromJson(response, LoginResponse.class);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                return lr != null && lr.isLoggedIn() == 1;
            }
        }.execute(login, password).get(5000, TimeUnit.MILLISECONDS);

    }
}
