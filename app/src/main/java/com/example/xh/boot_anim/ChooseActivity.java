package com.example.xh.boot_anim;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xh.boot_anim.dialogclasses.ConfigFragment;
import com.example.xh.boot_anim.dialogclasses.ImgAdapter;
import com.example.xh.boot_anim.dialogclasses.MyFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xh on 2017/10/4.
 */

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener, ConfigFragment.ConfigReturn, RootManager.RootFailureListener {
    private Toolbar btnLayout;
    private Button btnCreate;
    private Button btnChoose;
    private Button btnBackup;
    private ImageView imgShow;
    private RecyclerView listView;
    private List<Bitmap> currentBitmapList;//当前选中图片的帧
    private Handler handler;
    private View headerView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        RootManager.getInstance().setContext(this);
        RootManager.getInstance().getRootPrivilege(new String[]{""});
        init();
    }

    private void init() {
        btnLayout = (Toolbar) findViewById(R.id.btn_layout);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        headerView = View.inflate(ChooseActivity.this, R.layout.activity_headerview, null);
        imgShow = (ImageView) headerView.findViewById(R.id.imgShow);

        listView = (RecyclerView) findViewById(R.id.listview);
        btnBackup = (Button) findViewById(R.id.btn_backup);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnChoose.setOnClickListener(this);
        btnBackup.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        currentBitmapList = new ArrayList<>();
        handler = new Handler();

        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setAdapter(new ImgAdapter(this, currentBitmapList));
        canShowCreateButton();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_choose:
                getPermission();
                break;
            case R.id.btn_create:
                ConfigFragment cf = new ConfigFragment();
                cf.show(getFragmentManager(), "config");
                break;
            case R.id.btn_backup:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (ZipManager.newInstance().backup(getFilesDir().toString() + "/bootanimation.zip.backup")) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChooseActivity.this, "bootanimation已保存到" + getFilesDir().toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }

    public void getPermission() {
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(ChooseActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Log.i("permission", "已经获取权限");
            showImg();
        } else {
            Log.i("permission", "没获取权限");
            ActivityCompat.requestPermissions(ChooseActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                    showImg();
                } else {
                    Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void showImg() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/gif");
        startActivityForResult(intent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 2:
                if (data == null) {
                    Toast.makeText(this, "未选择", Toast.LENGTH_SHORT).show();
                    break;
                }
                final String imgPath = ImgUtils.getImgPath(ChooseActivity.this, data);
                if (imgPath != null) {
                    showListview(imgPath);
                }
                break;
            default:
                break;
        }
    }

    private void showListview(String gifPath) {
        //setAdapter后listview才会有显示，才会加载addheaderview，glide..into(headerview)才会生效

        Glide.with(this)
                .load(new File(gifPath))
                .asGif()
                .listener(new RequestListener<File, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, File file, Target<GifDrawable> target, boolean b) {
                        Log.i("tag", "aaaa");
                        MyFragment m = new MyFragment("提示", "请选择gif格式的图片");
                        m.show(getFragmentManager(), "notice");
                        //清空listview和当前的帧
                        currentBitmapList.clear();
                        listView.setAdapter(new ImgAdapter(ChooseActivity.this, currentBitmapList));

                        canShowCreateButton();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable gifDrawable, File file, Target<GifDrawable> target, boolean b, boolean b1) {
                        Log.i("tag", "xxxx");
                        GifDecoder gifDecoder = gifDrawable.getDecoder();
                        currentBitmapList.clear();
                        int count = gifDrawable.getFrameCount();
                        for (int current = 0; current < count; current++) {
                            gifDecoder.advance();
                            Bitmap nextFrame = gifDecoder.getNextFrame();
                            currentBitmapList.add(nextFrame);
                        }
                        Log.i("tag", currentBitmapList.size() + "一");
                        ImgAdapter imgAdapter = new ImgAdapter(ChooseActivity.this, currentBitmapList);
                        //glide into()先将gif加载到headerview的imgshow中
                        imgAdapter.setHeaderView(headerView);
                        listView.setAdapter(imgAdapter);
                        //listView.setAdapter(imgAdapter);
                        //listView.setBackgroundColor(Color.argb(255,249,243,243));
                        canShowCreateButton();
                        return false;
                    }
                })
                .error(R.mipmap.ic_launcher)
                .crossFade(1500)
                .into(imgShow);
    }

    @Override
    public void handleReturn(ConfigBean configBean) {
        //Toast.makeText(this, ""+reX+reY+frames+play_type+recycle, Toast.LENGTH_SHORT).show();
        ZipManager zipManager = ZipManager.newInstance();
        zipManager.createZip(this, handler, currentBitmapList, configBean);

        Log.i("tag", "" + configBean.getReX() + configBean.getReY());
    }

    public void canShowCreateButton() {
        if (currentBitmapList != null && currentBitmapList.size() > 0) {
            btnCreate.setEnabled(true);
            btnCreate.setVisibility(View.VISIBLE);
        } else {
            btnCreate.setEnabled(false);
            btnCreate.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void rootFailure() {
        new MyFragment("提示", "请授予root权限").show(getFragmentManager(), "get_root_false");
        Log.i("tag", "false");
    }
}