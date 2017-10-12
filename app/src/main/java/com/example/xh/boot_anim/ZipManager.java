package com.example.xh.boot_anim;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.xh.boot_anim.dialogclasses.MyProgressDialog;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by xh on 2017/10/9.
 */

enum ZipManager {
    INSTANCE;
    private Context context;
    private List<Bitmap> list;
    private ConfigBean configBean;
    private Handler uiHandler;
    private MyProgressDialog mpd;

    // private static int imgCounts;
    private static int current = 0;

    public static ZipManager newInstance() {
        return INSTANCE;
    }

    public boolean backup(String path) {
        if (RootManager.getRootPrivilege(new String[]{"mount -o rw,remount /system"}) && RootManager.getRootPrivilege(new String[]{"cp /system/media/bootanimation.zip " + path})) {

            RootManager.getRootPrivilege(new String[]{"mount -o ro,remount /system"});
            return true;
        }
        return false;
    }

    class ZipThread extends Thread {
        @Override
        public void run() {
            if (RootManager.getRootPrivilege(new String[]{"mount -o rw,remount /system"})) {

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mpd.setMax(list.size());
                        mpd.show();
                    }
                });
                Log.i("time", "desc");
                createDesc();
                Log.i("time", "part1");
                createPart1();
                Log.i("time", "compress");
                compress();
                Log.i("time", "compress end");
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mpd.dismiss();
                        Toast.makeText(context, "压缩成功！", Toast.LENGTH_SHORT).show();
                    }
                });
                RootManager.getRootPrivilege(new String[]{"mv " + context.getFilesDir() + "/bootanimation.zip /system/media/", "chmod 644 /system/media/bootanimation.zip" + "", "rm -r " + context.getFilesDir() + "/bootanimation", "mount -o ro,remount /system"});
            }

        }
    }

    public void createZip(Context context, Handler handler, List<Bitmap> list, ConfigBean configBean) {
        this.uiHandler = handler;
        this.context = context;
        this.list = list;
        this.configBean = configBean;
        mpd = new MyProgressDialog(context);
        final ZipThread zipThread = new ZipThread();
        zipThread.start();
        /*new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                if (!zipThread.isAlive()) {
                    mpd.dismiss();
                    cancel();
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "压缩完成", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        } ,1500,500);*/
    }


    private void compress() {
        try {

            long now = System.currentTimeMillis();
            zip_stored(context.getFilesDir() + "/bootanimation.zip", new File(context.getFilesDir() + "/bootanimation"));
            long end = System.currentTimeMillis();
            Log.i("time", now - end + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File createPart1() {
        File part1 = new File(context.getFilesDir() + "/bootanimation/part1");
        int current = 0;
        if (!part1.exists()) {
            part1.mkdirs();
        }
        for (Bitmap bm : list) {
            try {
                String a = "00" + current;
                String imgName = a.substring(a.length() - 3, a.length());
                File img = new File(part1.toString() + "/img" + imgName + ".png");
                //File img = new File("/sdcard"+"/img"+current+".png");
                img.createNewFile();
                FileOutputStream fos = new FileOutputStream(img);
                bm.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            current++;
        }
        return null;
    }

    public File createDesc() {
        File desc = new File(context.getFilesDir() + "/bootanimation/desc.txt");
        if (!desc.getParentFile().exists()) {
            desc.getParentFile().mkdirs();
        }
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(desc));
            bw.write(configBean.getReX() + " " + configBean.getReY() + " " + configBean.getFrames() + "\r\n");
            bw.write(configBean.getPlay_type() + " " + configBean.getRecycle() + " 0" + " part1" + "\r\n");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return desc;
    }


    public void zip_stored(String zipFileName, File inputFile) throws Exception {
        //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new ZipOutputStream(new FileOutputStream(zipFileName))));
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));
        zip_stored(out, inputFile, "");
        out.close();
    }

    /**
     * 按存储方式压缩
     *
     * @param out
     * @param f
     * @param base
     * @throws Exception
     */
    private void zip_stored(ZipOutputStream out, File f, String base) throws Exception {
        out.setMethod(ZipOutputStream.STORED);

        if (f.isDirectory()) {
            File[] fl = f.listFiles();
            base = base.length() == 0 ? "" : base + "/";
            for (int i = 0; i < fl.length; i++) {
                if (fl[i].getName().indexOf(".zip") == -1) {
                    zip_stored(out, fl[i], base + fl[i].getName());
                }
            }
        } else {
            ZipEntry entry = new ZipEntry(base);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(f.length());
            //Log.i("time", f.length()+"");
            long crc = 0;
            crc = calFileCRC32(f);
            entry.setCrc(crc);
            out.putNextEntry(entry);
            FileInputStream in = new FileInputStream(f);
            byte[] buffer = new byte[512];
            int b;
            while ((b = in.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            out.flush();
            out.closeEntry();
            //Log.i("time", "x");
            in.close();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    mpd.incrementProgressBy(1);
                }
            });
        }
    }

    public long calFileCRC32(File file) throws IOException {
        FileInputStream fi = new FileInputStream(file);
        CheckedInputStream checksum = new CheckedInputStream(fi, new CRC32());
        while (checksum.read() != -1) {
        }
        long temp = checksum.getChecksum().getValue();
        fi.close();
        checksum.close();
        return temp;
    }
}
