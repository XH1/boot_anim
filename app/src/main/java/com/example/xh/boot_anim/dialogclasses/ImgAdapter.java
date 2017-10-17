package com.example.xh.boot_anim.dialogclasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xh.boot_anim.R;

import java.util.List;

/**
 * Created by xh on 2017/10/13.
 */
//使用泛型！看RecyclerView.Adapter中的方法就明白了
public class ImgAdapter extends RecyclerView.Adapter<ImgAdapter.ViewHolder> {
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_HEADER = 1;
    private static final int TYPE_FOOTER = 2;
    private static final int TYPE_EMPTY = 3;

    private View headerView;
    private View footerView;
    private Context context;
    private List<Bitmap> bitmapList;

    public ImgAdapter(List<Bitmap> bitmapList) {
        this.bitmapList = bitmapList;
    }

    public ImgAdapter(Context context, List<Bitmap> bitmapList) {
        this(bitmapList);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EMPTY) {
            Log.i("tag", "asdasdas");

            View view = LayoutInflater.from(context).inflate(R.layout.activity_listview_empty, parent, false);

            return new ViewHolder(view);
        }

        if (headerView != null && viewType == TYPE_HEADER) {
            Log.i("tag", "asdasdas1");
            return new ViewHolder(headerView);
        }
        if (footerView != null && viewType == TYPE_FOOTER) {
            Log.i("tag", "asdasdas2");
            return new ViewHolder(footerView);
        }
        Log.i("tag", "asdasdas3");
        View view = View.inflate(parent.getContext(), R.layout.activity_listview_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.i("tag", "asd2");
        if (getItemViewType(position) != TYPE_NORMAL) {
            return;
        }
        int nPosition = (headerView == null ? position : position - 1);
        holder.imgView.setImageBitmap(bitmapList.get(nPosition));
        holder.textView.setText("第" + position + "帧..");
    }

    @Override
    public int getItemCount() {
        if (bitmapList.size() <= 0) {
            return 1;
        }
        if (headerView == null && footerView == null) {
            return bitmapList.size();
        } else if (headerView == null && footerView != null) {
            return bitmapList.size() + 1;
        } else if (headerView != null && footerView == null) {
            return bitmapList.size() + 1;
        } else {
            return bitmapList.size() + 2;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgView;
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            if (itemView == headerView || itemView == footerView) return;
            imgView = (ImageView) itemView.findViewById(R.id.item_img);
            textView = (TextView) itemView.findViewById(R.id.item_text);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("tag", "asdasdas11");
        if (bitmapList.size() <= 0) {
            return TYPE_EMPTY;
        }
        if (position == 0 && headerView != null) {
            return TYPE_HEADER;
        }
        if (position == getItemCount() - 1 && footerView != null) {
            return TYPE_FOOTER;
        }
        return TYPE_NORMAL;
    }

    public View getHeaderView() {
        return headerView;
    }

    public void setHeaderView(View headerView) {
        this.headerView = headerView;
    }

    public View getFooterView() {
        return footerView;
    }

    public void setFooterView(View footerView) {
        this.footerView = footerView;
    }
    /*private LayoutInflater inflater;
    private List<Bitmap> bitmapList;

    public ImgAdapter(Context context, List<Bitmap> bitmapList) {
        this.inflater = LayoutInflater.from(context);
        this.bitmapList = bitmapList;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder viewHolder = null;
        if (convertView == null) {
            view = inflater.inflate(R.layout.activity_listview_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            view = convertView;
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.imageView.setImageBitmap(bitmapList.get(position));
        viewHolder.textView.setText("第"+position+"帧..");
        return view;
    }

    private class ViewHolder{
        private TextView textView;
        private ImageView imageView;

        public ViewHolder(View view) {
            this.textView = (TextView) view.findViewById(R.id.item_text);
            this.imageView = (ImageView) view.findViewById(R.id.item_img);
            this.imageView = imageView;
        }
    }*/
}
