package com.cooldog.stormyplus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by Daniel on 5/24/2015.
 */
public class AlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.error_title))
               .setMessage(context.getString(R.string.error_message));
        builder.setPositiveButton(context.getString(R.string.error_ok_button),
                null); // Null closes dialogue.
        AlertDialog dialog = builder.create();
        return dialog;
    }


}
