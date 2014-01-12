package com.dutamobile.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dutamobile.DutaApplication;
import com.dutamobile.R;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.Helper;

/**
 * Created by Bartosz on 30.12.13.
 */
public class EditDialog extends DialogFragment
{
    public enum MODE
    {
        ADD(false),
        EDIT(true);

        MODE(boolean b)
        {
            mode = b;

        }

        public boolean getMode()
        {
            return mode;
        }

        private boolean mode;


    }

    public static final String TAG = "Edit Dialog";
    public static final String ARG_MODE = "mode";
    public static final String ARG_NICK = "n12";
    public static final String ARG_LOGIN = "log1";

    DutaApplication application;
    EditText nickEditText;
    EditText loginEditText;
    String contactName;
    String contactLogin;
    Boolean mode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_edit_contact, null);
        mode = getArguments().getBoolean(ARG_MODE);

        nickEditText = ((EditText) v.findViewById(R.id.editText));
        loginEditText = ((EditText) v.findViewById(R.id.editText2));

        if (mode)
        {
            contactName = getArguments().getString(ARG_NICK);
            contactLogin = getArguments().getString(ARG_LOGIN);
            loginEditText.setText(contactLogin);
            loginEditText.setEnabled(false);
            nickEditText.setText(contactName);
            getDialog().setTitle(getString(R.string.editting_contact));
        }
        else
        {
            application = (DutaApplication) getActivity().getApplication();
            getDialog().setTitle(getString(R.string.adding_contact));
        }
        return v;
    }

    @Override
    public void onStop()
    {
        String newName = nickEditText.getText().toString();

        if (mode)
        {
            if (!newName.equals(contactName))
                NetClient.GetInstance().PutContact(contactLogin, newName, true);
        }
        else
        {
            contactLogin = loginEditText.getText().toString();
            Helper.startTask(new AddContactTask(), newName);
        }

        super.onStop();
    }

    private class AddContactTask extends AsyncTask<String, Void, Void>
    {
        @Override
        protected Void doInBackground(String... params)
        {
            NetClient.GetInstance().PutContact(contactLogin, params[0], false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            super.onPostExecute(aVoid);
            application.DownloadContactList();
        }
    }
}
