package com.example.xh.boot_anim.dialogclasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Created by xh on 2017/10/12.
 */

public class DirectionListview extends ListView {
    private int touchSlop;//获取系统值：一次触摸移动多少pixel才触发移动控件
    private float startY = 0;
    private onScrollDirectionListener scrollDirectionListener;

    public DirectionListview(Context context) {
        this(context, null);
    }

    public DirectionListview(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DirectionListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - startY) >= touchSlop) {
                    if (ev.getY() - startY > 0) {
                        scrollDirectionListener.onScrollDown();
                    } else {
                        scrollDirectionListener.onScrollUp();
                    }
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void setScrollDirectionListener(onScrollDirectionListener scrollDirectionListener) {
        this.scrollDirectionListener = scrollDirectionListener;
    }

    public interface onScrollDirectionListener {
        void onScrollUp();

        void onScrollDown();
    }
}
