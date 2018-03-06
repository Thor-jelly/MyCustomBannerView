package com.dongdongwu.mycustombannerview;

import android.content.Context;
import android.os.Build;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * 类描述：<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/3/1 17:04 <br/>
 */

public class BannerScroller extends Scroller {
    /**
     * 动画持续时间
     */
    private int mScrollDuration = 850;

    /**
     * 设置页面切换动画持续时间
     */
    public void setScrollDuration(int scrollDuration) {
        mScrollDuration = scrollDuration;
    }

    public BannerScroller(Context context) {
        this(context, null);
    }

    public BannerScroller(Context context, Interpolator interpolator) {
        this(context, interpolator, context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.HONEYCOMB);
    }

    public BannerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, mScrollDuration);
    }
}
