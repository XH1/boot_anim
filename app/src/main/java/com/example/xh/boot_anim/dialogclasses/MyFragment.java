package com.example.xh.boot_anim.dialogclasses;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.example.xh.boot_anim.R;

/**
 * Created by xh on 2017/10/8.
 */

public class MyFragment extends DialogFragment {
    private String title;
    private String message;

    public MyFragment(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog alertDialog = new AlertDialog
                .Builder(getActivity())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(true);
        return alertDialog;
    }
}
