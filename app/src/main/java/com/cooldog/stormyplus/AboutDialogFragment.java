package com.cooldog.stormyplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Daniel on 6/6/2015.
 */
public class AboutDialogFragment extends DialogFragment{
    // Shows the about and information page.

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("About");
        builder.setMessage(context.getString(R.string.about));
        builder.setPositiveButton("Great!", null);
        AlertDialog dialog = builder.create();
        return dialog;
    }
}
