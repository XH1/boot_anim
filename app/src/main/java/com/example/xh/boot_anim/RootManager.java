package com.example.xh.boot_anim;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xh on 2017/10/3.
 */

enum RootManager {
    ROOT_MANAGER;
    private Context context;

    public static RootManager getInstance() {
        return ROOT_MANAGER;
    }

    public interface RootFailureListener {
        void rootFailure();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public boolean getRootPrivilege(String[] cmds) {
        Process process = null;
        DataOutputStream dos = null;
        BufferedReader errReader = null;
        BufferedReader inReader = null;
        //String cmd = "mount -o rw,remount /system";
        try {
            process = Runtime.getRuntime().exec("su\n");
            Thread.sleep(300);
            dos = new DataOutputStream(process.getOutputStream());
            errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            inReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            for (String cmd : cmds) {
                dos.writeBytes(cmd + "\n");
            }
            dos.writeBytes("exit" + "\n");
            dos.flush();
            process.waitFor();

            String data = "";
            String err = "";
            while ((err = errReader.readLine()) != null && !err.equals("null")) {
                data += "e" + err + "\n";
            }
            String in = "";
            while ((in = inReader.readLine()) != null && !in.equals("null")) {
                data += in + "\n";
            }
            Log.i("tag", data);
        } catch (Exception e) {
            e.printStackTrace();
            ((RootFailureListener) context).rootFailure();
            return false;
        } finally {
            try {
                if (dos != null) dos.close();
                if (inReader != null) inReader.close();
                if (errReader != null) errReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
