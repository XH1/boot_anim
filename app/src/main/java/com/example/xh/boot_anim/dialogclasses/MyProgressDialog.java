package com.example.xh.boot_anim.dialogclasses;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by xh on 2017/10/11.
 */

public class MyProgressDialog extends ProgressDialog {
    public MyProgressDialog(Context context) {
        super(context);
        setCancelable(false);
        setMessage("正在压缩……");
        setTitle("稍后");
        setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    }
}
