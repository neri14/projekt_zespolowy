package com.dutamobile;

import com.dutamobile.model.Contact;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class RESTClient
{
    private HttpContext localContext = new BasicHttpContext();
    private HttpClient client = new DefaultHttpClient();
    private int userID;

    String server = "http://192.168.1.6:51020/Service1.svc";

    private RESTClient() {

    }

    private static RESTClient mInstance = null;

    public static synchronized RESTClient getInstance() {
        if (mInstance == null) {
            mInstance = new RESTClient();
        }
        return mInstance;
    }

    public  List<Contact> getContacts()
    {
        String endpoint = "/contact";
        HttpGet get = new HttpGet(server + endpoint);

        List<Contact> ret = new ArrayList<Contact>();

        HttpResponse response;
        try {
            response = client.execute(get, localContext);


            ret = null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return null;
        }
        return ret;
    }

    public static void setInstanceUserID(int ID) {
        getInstance().userID = ID;
    }

}
