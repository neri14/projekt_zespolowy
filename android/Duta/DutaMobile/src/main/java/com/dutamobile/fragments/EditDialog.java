package com.dutamobile.fragments;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dutamobile.R;
import com.dutamobile.util.AddContactTask;
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
            Helper.startTask(new AddContactTask(getActivity(), contactLogin, nickEditText.getText().toString(), mode));
            onStop();
        }
    };
}
