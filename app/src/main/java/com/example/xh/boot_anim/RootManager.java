package com.example.xh.boot_anim;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by xh on 2017/10/3.
 */

public class RootManager {
    public static boolean getRootPrivilege(String[] cmds) {
        Process process = null;
        DataOutputStream dos = null;
        BufferedReader errReader = null;
        BufferedReader inReader = null;
        //String cmd = "mount -o rw,remount /system";
        try {
            process = Runtime.getRuntime().exec("su\n");
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
                data += err + "\n";
            }
            String in = "";
            while ((in = inReader.readLine()) != null && !in.equals("null")) {
                data += in + "\n";
            }
            Log.i("tag", data);
        } catch (Exception e) {
            e.printStackTrace();
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
