package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dutamobile.R;
import com.dutamobile.net.NetClient;

/**
 * Created by Bartosz on 30.12.13.
 */
public class EditDialog extends DialogFragment
{
    public static String TAG = "Edit Dialog";
    public static String NICK = "n12";
    public static String LOGIN = "log1";

    EditText editText;
    String contactName;
    String contactLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View v = inflater.inflate(R.layout.dialog_edit_contact, null);
        contactName = getArguments().getString(NICK);
        contactLogin = getArguments().getString(LOGIN);

        ((TextView)v.findViewById(R.id.textView3)).setText(contactLogin);
        editText = ((EditText)v.findViewById(R.id.editText));
        editText.setText(contactName);

        getDialog().setTitle(getString(R.string.editting_contact));

        return v;
    }

    @Override
    public void onStop()
    {
        String newName = editText.getText().toString();

        if (!newName.equals(contactName))
           NetClient.GetInstance().PutContact(contactLogin, newName, true);

        super.onStop();
    }
}
