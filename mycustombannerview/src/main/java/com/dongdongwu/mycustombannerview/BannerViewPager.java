package com.dongdongwu.mycustombannerview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：自定义Banner <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/3/1 14:50 <br/>
 */

public class BannerViewPager extends ViewPager {
    private static final String TAG = "BannerViewPager";

    /**
     * 切换发送的what
     */
    private static final int SCROLL_MSG = 1;
    /**
     * 默认切换时间 ms
     */
    private static final int SCROLL_TIME = 3500;

    /**
     * 自定义BannerView，默认的自定义的adapter
     */
    private BannerAdapter mBannerAdapter;

    /**
     * 切换到下一张的时间 ms值
     */
    private int mScrollNextTime = SCROLL_TIME;

    /**
     * 改变ViewPager切换的速率 - 自定义的页面切换的Scroller
     */
    private BannerScroller mBannerScroller;

    private Activity mActivity;

    /**
     * 保存View容器
     */
    List<View> mViewContainerList;

    /**
     * 是否允许自动滚动
     */
    private boolean mEnableAutoScroll = true;

    /**
     * 是否允许无限轮回
     */
    private boolean mEnableUnlimitedScroll = true;

    private MyActivityLifecycleCallbacks mDefaultActivityLifecycleCallbacks;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (mEnableAutoScroll) {
                //每隔多少秒后切换到下一页
                int itemCount = getCurrentItem() + 1;
                int allCount = mBannerAdapter.getCount();
                if (!mEnableUnlimitedScroll
                        && itemCount >= allCount
                ) {
                    return;
                }

                setCurrentItem(itemCount);
                //不断执行
                startRoll();
            }
        }
    };
    private List<View> mReuseView;

    public BannerViewPager(Context context) {
        this(context, null);
    }

    public BannerViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        mActivity = (Activity) context;
        mDefaultActivityLifecycleCallbacks = new MyActivityLifecycleCallbacks(mActivity, this);

        try {
            //改变ViewPager的速率
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            //设置参数
            mScroller.setAccessible(true);
            mBannerScroller = new BannerScroller(context);
            mScroller.set(this, mBannerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEnabledAutoScroll(boolean enableAutoScroll) {
        mEnableAutoScroll = enableAutoScroll;
        if (enableAutoScroll) {
            startRoll();
        } else {
            stopRoll();
        }
    }

    /**
     * 设置适配器
     *
     * @param enableUnlimitedScroll 是否允许无限轮播
     */
    public void setAdapter(BannerAdapter adapter, boolean enableUnlimitedScroll) {
        mBannerAdapter = adapter;
        mEnableUnlimitedScroll = enableUnlimitedScroll;
        //设置父类ViewPager的adapter
        setAdapter(new BannerPagerAdapter(adapter.getCount(), enableUnlimitedScroll));

        //注册生命周期
        mActivity.getApplication().registerActivityLifecycleCallbacks(mDefaultActivityLifecycleCallbacks);
    }

    /**
     * 实现自动轮播
     */
    public void startRoll() {
        //如果图小于2张不实现轮播
        if (mBannerAdapter != null && mBannerAdapter.getCount() < 2) {
            return;
        }

        //清除消息，防止多次发送
        if (mHandler.hasMessages(SCROLL_MSG)) {
            mHandler.removeMessages(SCROLL_MSG);
        }

        //发送延迟空消息，实现自动轮播
        if (mEnableAutoScroll) {
            mHandler.sendEmptyMessageDelayed(SCROLL_MSG, mScrollNextTime);
        }
    }

    /**
     * 设置页面切换动画持续时间
     */
    public void setScrollDuration(int scrollDuration) {
        mBannerScroller.setScrollDuration(scrollDuration);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
//        Log.e("123===", "-->");
//        Log.e("123===", "-->" + action);
//        Log.e("123===", "-->");
        //如果图小于2张不实现轮播，因此点击事件不需要处理
        if (mBannerAdapter != null && mBannerAdapter.getCount() > 1) {
            if (action == MotionEvent.ACTION_DOWN) {
                //销毁Handle 停止发送，解决内存泄漏
                stopRoll();
            } else if (
                    action == MotionEvent.ACTION_UP
                            || action == MotionEvent.ACTION_CANCEL
            ) {
                //开始自动轮播
                startRoll();
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        //销毁Handle 停止发送，解决内存泄漏
        stopRoll();
        mHandler = null;
        mActivity.getApplication().unregisterActivityLifecycleCallbacks(mDefaultActivityLifecycleCallbacks);
        mDefaultActivityLifecycleCallbacks = null;
        mActivity = null;
        if (mViewContainerList != null) {
            mViewContainerList.clear();
        }
        mViewContainerList = null;
        mBannerAdapter = null;

        super.onDetachedFromWindow();

    }

    /**
     * 销毁Handle 停止发送，解决内存泄漏
     */
    private void stopRoll() {

        //如果图小于2张不实现轮播
        if (mBannerAdapter != null && mBannerAdapter.getCount() < 2) {
            return;
        }

        //销毁Handle 停止发送，解决内存泄漏
        if (mHandler.hasMessages(SCROLL_MSG)) {
            mHandler.removeMessages(SCROLL_MSG);
        }
    }

    /**
     * 得到复用View
     */
    private View getReuseView() {
        for (int i = 0; mViewContainerList != null && i < mViewContainerList.size(); i++) {
            View view = mViewContainerList.get(i);
            if (view.getParent() == null) {
                return view;
            }
        }
        return null;
    }

    /**
     * 给ViewPager设置适配器
     */
    private class BannerPagerAdapter extends PagerAdapter {
        private int count;
        private boolean enableUnlimitedScroll;

        public BannerPagerAdapter(int count, boolean enableUnlimitedScroll) {
            this.count = count;
            this.enableUnlimitedScroll = enableUnlimitedScroll;
        }

        @Override
        public int getCount() {
            //为了实现无限循环
            if (count == 1) {
                //如果只有一张图片不循环
                return 1;
            }

            if (!enableUnlimitedScroll) {
                return count;
            }

            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            //官方推荐这么写，源码中写了
            return view == object;
        }

        //创建viewpager 条目回调方法
        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            /*
                如果这里写死，比如直接ImageView imageView = new ImageView();
                所以这里要使用adapter设置模式，为了让我们自定义
             */
            View bannerItemView = mBannerAdapter.getView(position % mBannerAdapter.getCount(), getReuseView());
            //添加自定义的View样式
            container.addView(bannerItemView);
            return bannerItemView;
        }

        //销毁ViewPager 条目回调方法
        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            /*
                下面为什么注释呢，可以直接点进去看一下源码，只是抛出一个异常没有做其他处理
                throw new UnsupportedOperationException("Required method destroyItem was not overridden");
             */
            //super.destroyItem(container, position, object);
            container.removeView((View) object);
//            object = null;//释放一下内存

            /*
                仿list View设计重用view
             */
            if (mViewContainerList == null) {
                mViewContainerList = new ArrayList<>();
            }
            mViewContainerList.add((View) object);
        }
    }

    private static class MyActivityLifecycleCallbacks extends DefaultActivityLifecycleCallbacks {
        private SoftReference<Activity> srActivity;
        private SoftReference<BannerViewPager> srVp;

        public MyActivityLifecycleCallbacks(Activity activity, BannerViewPager vp){
            srActivity = new SoftReference<>(activity);
            srVp = new SoftReference<>(vp);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            super.onActivityResumed(activity);
            Log.d(TAG, "onActivityResumed: ");
            //是不是监听当前activity的生命周期
            if (srActivity == null ||srActivity.get() != activity) {
                return;
            }

            //如果图小于2张不实现轮播
            if (srVp == null || (srVp.get().mBannerAdapter != null && srVp.get().mBannerAdapter.getCount() < 2)) {
                return;
            }

            if (srVp.get().mEnableAutoScroll) {
                //开启轮播
                srVp.get().startRoll();
            }
        }

        @Override
        public void onActivityStopped(Activity activity) {
            super.onActivityStopped(activity);
            Log.d(TAG, "onActivityStopped: ");
            //是不是监听当前activity的生命周期
            if (srActivity == null ||srActivity.get() != activity) {
                return;
            }

            //如果图小于2张不实现轮播
            if (srVp == null || (srVp.get().mBannerAdapter != null && srVp.get().mBannerAdapter.getCount() < 2)) {
                return;
            }

            //停止轮播
            srVp.get().stopRoll();
        }
    }
}