package com.dutamobile;

import android.app.Application;

import com.dutamobile.model.Contact;
import com.dutamobile.util.Helper;
import com.dutamobile.util.NetClient;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Bartosz on 17.11.13.
 */
public class DutaApplication extends Application
{
    private List<Contact> contactList;

    @Override
    public void onCreate()
    {
        super.onCreate();

        try
        {

            //FIXME usunąć w finalnej wersji***************************
            // automatyczne logowanie z pominięciem ekranu logowania
            // ma to na celu uniknięcie wkurwienia przy każdorazowym włączeniu aplikacji
            // która wymaga podania loginu i hasła

            NetClient.GetInstance().Login("user_a", "pass");

            ///********************************************************

            contactList = NetClient.GetInstance().GetContactList();
            //NetClient.GetInstance().GetMessage();
            NetClient.GetInstance().GetStatusUpdate();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (TimeoutException e)
        {
            e.printStackTrace();
        }

        Helper.MyID = getSharedPreferences(Helper.PREFS_MAIN, MODE_PRIVATE).getInt("MyUserID", 0);




    }

    public List<Contact> getContactList()
    {
        return contactList;
    }

}
