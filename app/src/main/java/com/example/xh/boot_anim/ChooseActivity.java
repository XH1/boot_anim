package com.example.xh.boot_anim;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.xh.boot_anim.dialogclasses.ConfigFragment;
import com.example.xh.boot_anim.dialogclasses.DirectionListview;
import com.example.xh.boot_anim.dialogclasses.MyFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xh on 2017/10/4.
 */

public class ChooseActivity extends AppCompatActivity implements View.OnClickListener, ConfigFragment.ConfigReturn {
    private LinearLayout btnLayout;
    private Button btnCreate;
    private Button btnChoose;
    private Button btnBackup;
    private ImageView imgShow;
    private DirectionListview listView;
    private int[] to = {R.id.item_img, R.id.item_text};
    private String[] from = {"img", "text"};
    private List<Bitmap> currentBitmapList;//当前选中图片的帧
    private List<Map<String, Object>> list;
    private SimpleAdapter simpleAdpter;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img);
        getRoot();
        init();

    }

    private void getRoot() {
        if (!RootManager.getRootPrivilege(new String[]{""})) {
            new MyFragment("提示", "请授予root权限").show(getFragmentManager(), "get_root_false");
            System.exit(0);
        }
    }

    private void init() {
        btnLayout = (LinearLayout) findViewById(R.id.btn_layout);
        btnChoose = (Button) findViewById(R.id.btn_choose);
        imgShow = new ImageView(this);
        imgShow.setMaxHeight(300);
        imgShow.setMaxWidth(400);
        imgShow.setAdjustViewBounds(true);
        listView = (DirectionListview) findViewById(R.id.listview);
        listView.setScrollDirectionListener(new DirectionListview.onScrollDirectionListener() {
            @Override
            public void onScrollUp() {
                btnLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrollDown() {
                btnLayout.setVisibility(View.INVISIBLE);
            }
        });
        listView.addHeaderView(imgShow);
        btnBackup = (Button) findViewById(R.id.btn_backup);
        btnCreate = (Button) findViewById(R.id.btn_create);
        btnChoose.setOnClickListener(this);
        btnBackup.setOnClickListener(this);
        btnCreate.setOnClickListener(this);
        currentBitmapList = new ArrayList<>();
        handler = new Handler();
        list = new ArrayList<Map<String, Object>>();
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
                if (ZipManager.newInstance().backup(getFilesDir().toString() + "/bootanimation.zip.backup")) {
                    Toast.makeText(this, "bootanimation已保存到" + getFilesDir().toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "备份失败", Toast.LENGTH_LONG).show();
                }
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
        //intent.setData();
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
                String imgPath = ImgUtils.getImgPath(ChooseActivity.this, data);
                if (imgPath != null) {
                    //imgShow.setImageBitmap(BitmapFactory.decodeFile(imgPath));
                    showListview(imgPath);
                }
                break;
            default:
                break;
        }
    }

    private void showListview(String gifPath) {
        //setAdapter后listview才会有显示，才会加载addheaderview，glide..into(headerview)才会生效
        listView.setAdapter(null);
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
                        listView.setAdapter(null);
                        list.clear();
                        currentBitmapList.clear();
                        canShowCreateButton();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable gifDrawable, File file, Target<GifDrawable> target, boolean b, boolean b1) {
                        Log.i("tag", "xxxx");
                        GifDecoder gifDecoder = gifDrawable.getDecoder();
                        list.clear();
                        currentBitmapList.clear();
                        simpleAdpter = new SimpleAdapter(ChooseActivity.this, list, R.layout.activity_listview_item, from, to);
                        simpleAdpter.setViewBinder(new SimpleAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue(View view, Object data, String textRepresentation) {
                                if ((view instanceof ImageView) & (data instanceof Bitmap)) {
                                    ImageView iv = (ImageView) view;
                                    Bitmap bm = (Bitmap) data;
                                    iv.setImageBitmap(bm);
                                    return true;
                                }
                                return false;
                            }
                        });
                        int count = gifDrawable.getFrameCount();
                        Toast.makeText(ChooseActivity.this, count + "帧", Toast.LENGTH_SHORT).show();
                        for (int current = 0; current < count; current++) {
                            Map<String, Object> map = new HashMap<String, Object>();
                            gifDecoder.advance();
                            Bitmap nextFrame = gifDecoder.getNextFrame();
                            currentBitmapList.add(nextFrame);
                            map.put("img", nextFrame);
                            map.put("text", "第" + current + "帧");
                            list.add(map);
                        }
                        Log.i("tag", currentBitmapList.size() + "一");
                        listView.setAdapter(simpleAdpter);
                        listView.setBackgroundColor(Color.WHITE);
                        listView.setVisibility(View.VISIBLE);
                        canShowCreateButton();
                        return false;
                    }
                })
                .error(R.mipmap.ic_launcher)
                .crossFade(1500)
                .into(imgShow);
        //canShowCreateButton();
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
        } else {
            btnCreate.setEnabled(false);
        }
    }


}