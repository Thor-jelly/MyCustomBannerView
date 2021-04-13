package com.dongdongwu.mycustombannerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

/**
 * 类描述：自定义banner <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/3/2 10:45 <br/>
 */
public class BannerView extends RelativeLayout {

    /**
     * 轮播图
     */
    private BannerViewPager mBannerVp;
    /**
     * 底部布局
     */
    private RelativeLayout mBannerBottomRl;
    /**
     * 当前图描述
     */
    private TextView mBannerDescribeTv;
    /**
     * 点布局
     */
    private LinearLayout mBannerDotLl;

    /**
     * 自定义的bannerAdapter
     */
    private BannerAdapter mBannerAdapter;
    private Context mContext;

    /**
     * 小点样式--选中
     */
    private Drawable mDotIndicatorSelectDrawable;
    /**
     * 小点样式--未选中
     */
    private Drawable mDotIndicatorNoSelectDrawable;
    /**
     * 当前选中小点位置
     */
    private int mCurrentDotPosition = 0;

    /**
     * 点显示的位置
     */
    private int mDotGravity;
    /**
     * 点的大小
     */
    private int mDotSize;
    /**
     * 点的间距
     */
    private int mDotDistance;
    /**
     * 底部颜色
     */
    private int mBottomColor;
    /**
     * 宽高比
     */
    private float mWideProportion;
    /**
     * 宽高比
     */
    private float mHeightProportion;
    /**
     * 获取点距离item距离
     */
    private float mDotMarginTop;
    /**
     * 提示点类型
     */
    private int mDotHintType = 0;
    /**
     * 是否允许自动滚动
     */
    private boolean mEnableAutoScroll = true;
    /**
     * 是否允许无限轮回
     */
    private boolean mEnableUnlimitedScroll = true;

    public BannerView(Context context) {
        this(context, null);
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
        //加载布局进当前view
        View view = inflate(context, R.layout.banner_layout, this);

        //初始化自定义属性
        initAttribute(attrs);

        //初始化View
        initView(view);
    }

    /**
     * 初始化自定义属性
     */
    private void initAttribute(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.BannerView);

        //是否允许无限轮回
        mEnableUnlimitedScroll = array.getBoolean(R.styleable.BannerView_enableUnlimitedScroll, true);
        //获取是否允许自动滚动
        mEnableAutoScroll = array.getBoolean(R.styleable.BannerView_enableAutoScroll, true);
        //获取提示点类型
        mDotHintType = array.getInt(R.styleable.BannerView_dotHintType, 0);
        //获取点距离item距离
        mDotMarginTop = array.getDimension(R.styleable.BannerView_dotMarginTop, 0);
        //获取点的位置
        mDotGravity = array.getInt(R.styleable.BannerView_dotGravity, 1);
        //获取小点选中和未选中样式
        mDotIndicatorSelectDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorSelectColor);
        if (mDotIndicatorSelectDrawable == null) {
            mDotIndicatorSelectDrawable = new ColorDrawable(0xffff0000);
        }
        mDotIndicatorNoSelectDrawable = array.getDrawable(R.styleable.BannerView_dotIndicatorNoSelectColor);
        if (mDotIndicatorNoSelectDrawable == null) {
            mDotIndicatorNoSelectDrawable = new ColorDrawable(0xffffffff);
        }
        //点的大小
        mDotSize = (int) array.getDimension(R.styleable.BannerView_dotSize, 8);
        //点的间距
        mDotDistance = (int) array.getDimension(R.styleable.BannerView_dotDistance, 4);
        //底部条颜色
        mBottomColor = array.getColor(R.styleable.BannerView_bottomColor, 0x00000000);
        //宽高比
        mWideProportion = array.getFloat(R.styleable.BannerView_wideProportion, 0);
        mHeightProportion = array.getFloat(R.styleable.BannerView_heightProportion, 0);

        //最后array需要回收
        array.recycle();
    }

    /**
     * 初始化View
     */
    private void initView(View view) {
        mBannerBottomRl = (RelativeLayout) view.findViewById(R.id.banner_bottom_rl);
        mBannerVp = (BannerViewPager) view.findViewById(R.id.banner_vp);
        mBannerDescribeTv = (TextView) view.findViewById(R.id.banner_describe_tv);
        mBannerDotLl = (LinearLayout) view.findViewById(R.id.banner_dot_ll);

        //设置底部颜色
        mBannerBottomRl.setBackgroundColor(mBottomColor);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Context context = getRootView().getContext();
        context = null;
        mContext = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置适配器
     */
    public void setAdapter(final BannerAdapter adapter) {
        mBannerAdapter = adapter;
        if (mDotMarginTop > 0) {
            RelativeLayout.LayoutParams bannerVpLayoutParams = (LayoutParams) mBannerVp.getLayoutParams();
            bannerVpLayoutParams.setMargins(0, 0, 0, (int) mDotMarginTop);
            mBannerVp.setLayoutParams(bannerVpLayoutParams);
        }
        mBannerVp.setAdapter(adapter,
                mEnableUnlimitedScroll);

        if (mBannerAdapter.getCount() > 1) {
            mBannerVp.setEnabledAutoScroll(mEnableAutoScroll);
            //如果是点自动初始化点，如果是number类型，需要自定义创建view
            if (mBannerDotLl.getChildCount() > 0) {
                mBannerDotLl.removeAllViews();
            }
            if (mDotHintType == 0) {
                //初始化点的指示器
                initDotIndicator();
            } else {
                //设置点的位置
                mBannerDotLl.setGravity(getDotGravity());
                mBannerDotLl.addView(adapter.setDotHintView());
            }

            adapter.getPageSelect(0);
            //设置轮播后广告和小点选中
            mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    //监听当前选中的位置，并改变小点状态
                    int selectPosition = position % mBannerAdapter.getCount();
                    adapter.getPageSelect(selectPosition);
                    pageSelect(selectPosition);
                }
            });

            //设置初始化的第一条广告
            mBannerDescribeTv.setText(mBannerAdapter.getBannerDescribe(mCurrentDotPosition));
        } else {
            mBannerBottomRl.setVisibility(View.INVISIBLE);
        }

        //动态指定高度
        if (mHeightProportion == 0 && mWideProportion == 0) {
            return;
        }

        int wide = getMeasuredWidth();
        int height = (int) (wide * mHeightProportion / mWideProportion);
        getLayoutParams().height = height;
    }

    /**
     * 监听当前选中的位置，并改变小点状态
     * 设置广告描述
     */
    private void pageSelect(int position) {
        if (mDotHintType == 0) {
            //把之前的点变成未选中状态
            DotIndicatorView oldDotView = (DotIndicatorView) mBannerDotLl.getChildAt(mCurrentDotPosition);
            oldDotView.setDrawable(mDotIndicatorNoSelectDrawable);

            //把position位置的点变成选中状态
            mCurrentDotPosition = position;
            DotIndicatorView nowDotView = (DotIndicatorView) mBannerDotLl.getChildAt(mCurrentDotPosition);
            nowDotView.setDrawable(mDotIndicatorSelectDrawable);
        }

        //设置广告
        mBannerDescribeTv.setText(mBannerAdapter.getBannerDescribe(mCurrentDotPosition));
    }

    /**
     * 初始化点的指示器
     */
    private void initDotIndicator() {
        //获取轮播图数量，也就是要添加点的数量
        int dotCount = mBannerAdapter.getCount();

        //设置点的位置
        mBannerDotLl.setGravity(getDotGravity());

        for (int i = 0; i < dotCount; i++) {
            //不断往点的指示器中添加点
            DotIndicatorView dotIndicatorView = new DotIndicatorView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mDotSize, mDotSize);
            //设置左右间距
            params.leftMargin = params.rightMargin = mDotDistance;
            //设置大小
            dotIndicatorView.setLayoutParams(params);
            //设置背景
            if (i == 0) {
                //选中点
                dotIndicatorView.setDrawable(mDotIndicatorSelectDrawable);
            } else {
                //未选中点
                dotIndicatorView.setDrawable(mDotIndicatorNoSelectDrawable);
            }
            //把点添加进指示器中
            mBannerDotLl.addView(dotIndicatorView);
        }
    }

    /**
     * dip 转成 px
     */
  /*  private int dp2px(int dp) {
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
        return (int) px;
    }*/

    /**
     * 实现自动轮播
     */
    public void startRoll() {
        mBannerVp.startRoll();
    }

    /**
     * 设置页面切换动画持续时间
     */
    public void setScrollDuration(int scrollDuration) {
        mBannerVp.setScrollDuration(scrollDuration);
    }

    /**
     * 获取点的位置
     */
    public int getDotGravity() {
        if (mDotGravity == 0) {
            return Gravity.CENTER;
        } else if (mDotGravity == 1) {
            return Gravity.END;
        } else {
            return Gravity.START;
        }
    }
}
