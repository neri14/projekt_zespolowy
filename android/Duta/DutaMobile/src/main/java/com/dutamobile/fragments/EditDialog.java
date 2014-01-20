package com.dutamobile.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dutamobile.DutaApplication;
import com.dutamobile.MainActivity;
import com.dutamobile.R;
import com.dutamobile.net.NetClient;
import com.dutamobile.util.Helper;

import java.io.IOException;

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

        private boolean mode;

        public boolean getMode()
        {
            return mode;
        }
    }

    public static final String TAG = "Edit Dialog";
    public static final String ARG_MODE = "mode";
    public static final String ARG_NICK = "n12";
    public static final String ARG_LOGIN = "log1";

    private DutaApplication application;
    private EditText nickEditText;
    private EditText loginEditText;
    private String contactName;
    private String contactLogin;
    private Boolean mode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.dialog_edit_contact, null);
        mode = getArguments().getBoolean(ARG_MODE);

        nickEditText = ((EditText) v.findViewById(R.id.editText));
        loginEditText = ((EditText) v.findViewById(R.id.editText2));
        Button okBtn = (Button) v.findViewById(R.id.button);
        okBtn.setOnClickListener(okListener);
        v.findViewById(R.id.button2).setOnClickListener(cancelListener);

        if (mode)
        {
            contactName = getArguments().getString(ARG_NICK);
            contactLogin = getArguments().getString(ARG_LOGIN);
            loginEditText.setText(contactLogin);
            loginEditText.setEnabled(false);
            nickEditText.setText(contactName);
            getDialog().setTitle(getString(R.string.editting_contact));
            okBtn.setText(R.string.edit);
        }
        else
        {
            okBtn.setText(R.string.add);
            application = (DutaApplication) getActivity().getApplication();
            getDialog().setTitle(getString(R.string.adding_contact));
        }
        return v;
    }

    private View.OnClickListener cancelListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            onStop();
        }
    };

    private View.OnClickListener okListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            String newName = nickEditText.getText().toString();
            Boolean test = mode && !newName.equals(contactName);
            if (!test) contactLogin = loginEditText.getText().toString();
            Helper.startTask(new AddContactTask(), newName, test);
            onStop();
        }
    };

    private class AddContactTask extends AsyncTask<Object, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Object... params)
        {
            try
            {
                return NetClient.GetInstance().PutContact(contactLogin, (String) params[0], (Boolean) params[1]);
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result)
        {
            super.onPostExecute(result);
            if (result)
                if (mode)
                {
                    ((DutaApplication) getActivity().getApplication())
                            .GetContactByLogin(contactLogin).setName(nickEditText.getText().toString());
                    ((MainActivity) getActivity()).UpdateView();
                }
                else application.DownloadContactList();
        }
    }
}
