# [MyCustomBannerView](https://github.com/Thor-jelly/MyCustomBannerView)

[![GitHub release](https://img.shields.io/badge/release-1.1.1-green.svg)](https://github.com/Thor-jelly/MyCustomBannerView/releases)

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
dependencies {
    compile 'com.github.Thor-jelly: MyCustomBannerView:最新版本号'
}
```

# 已具有功能

- 默认功能
    - 当只有banner的count为1时，底部被INVISIBLE

- xml属性
    - 是否允许无限轮回 enableUnlimitedScroll
    - 是否允许自动滚动 enableAutoScroll
    - 提示点类型 dotHintType
    - 点距离滚动条目距离 dotMarginTop
    - 点选中的颜色 dotIndicatorSelectColor
    - 点未选中的颜色 dotIndicatorNoSelectColor
    - 点的大小 dotSize
    - 点的间距 dotDistance
    - 点的位置 dotGravity
    - 底部条颜色 bottomColor
    - 宽高比 wideProportion heightProportion

- 代码设置
    - setAdapter设置
    - startRoll 开始轮播，需要在xml设置可以自动轮播
    - setScrollDuration 设置轮播切换时间

# 具体代码请看最新版本的源码

# Android无限广播轮播
> 使用了类似view复用的方法
> 
    if (reuseView == null) {
        iv = new ImageView(MainActivity.this);
    } else {
        iv = (ImageView) reuseView;
        Log.d(TAG, "getView: 界面复用View"+reuseView);
    }


# 分析实现方式
- 系统ViewPager + 自定义 extends ViewPagers
- 自定义ViewGroup + extends HorizontalScrollView

# 参数的传递
- 传递图片链接数组
- Adapter适配器模式

# ViewPager源码分析
> 参照[Android无限广告轮播 - ViewPager源码分析](https://www.jianshu.com/p/32b8fd8b202d)

# 实现
1. 自定义View BannerViewPager

    - 创建自定义View Pager
    
    ```
    /**
     * 类描述：自定义Banner <br/>
     * 创建人：吴冬冬<br/>
     * 创建时间：2018/3/1 14:50 <br/>
     */
    public class BannerViewPager extends ViewPager {
        /**
         * 自定义BannerView，默认的自定义的adapter
         */
        private BannerAdapter mBannerAdapter;
    
        public BannerViewPager(Context context) {
            super(context);
        }
    
        public BannerViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    
        public void setAdapter(BannerAdapter adapter) {
            mBannerAdapter = adapter;
            //设置父类ViewPager的adapter
            setAdapter(new BannerPagerAdapter());
        }
    
        /**
         * 给ViewPager设置适配器
         */
        private class BannerPagerAdapter extends PagerAdapter {
            @Override
            public int getCount() {
                //为了实现无限循环
                return Integer.MAX_VALUE;
            }
    
            @Override
            public boolean isViewFromObject(View view, Object object) {
                //官方推荐这么写，源码中写了
                return view == object;
            }
    
            //创建viewpager 条目回调方法
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                /*
                    如果这里写死，比如直接ImageView imageView = new ImageView();
                    所以这里要使用adapter设置模式，为了让我们自定义
                 */
                View bannerItemView = mBannerAdapter.getView(position);
                //添加自定义的View样式
                container.addView(bannerItemView);
                return bannerItemView;
            }
    
            //销毁ViewPager 条目回调方法
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                /*
                    下面为什么注释呢，可以直接点进去看一下源码，只是抛出一个异常没有做其他处理
                    throw new UnsupportedOperationException("Required method destroyItem was not overridden");
                 */
                //super.destroyItem(container, position, object);
                container.removeView((View) object);
                object = null;//释放一下内存
            }
        }
    }
    ```
    
    - 创建适配器
    
    ```
    /**
     * 类描述：<br/>
     * 创建人：吴冬冬<br/>
     * 创建时间：2018/3/1 15:04 <br/>
     */
    public abstract class BannerAdapter {
        /**
         * 根据位置获取ViewPager的子View
         *
         * @param reuseView 复用view
         */
        public abstract View getView(int position, View reuseView);

        /**
         * 获取轮播图数量
         */
        public abstract int getCount();

        /**
         * 获得当前广告位描述
         *
         * @param currentDotPosition 当前选中轮播图位置
         */
        public String getBannerDescribe(int currentDotPosition) {
            return "";
        }

        /**
         * 如果是自定义提示点需要覆写改方法
         */
        public View setDotHintView() {
            return null;
        }

        /**
         * 获取当前选中的位置
         */
        public void getPageSelect(int position) {
        }
    }
    ```
    
    - 在布局中加入自定义ViewPager，并设置适配器
    
    ```
    final List<String> ss = new ArrayList<>();
        ss.add("http://bbs.everychina.com/data/attachment/forum/201312/04/102637cwnwrywccfxwab22.jpg");
        ss.add("http://img3.100bt.com/upload/ttq/20131121/1385034610130_middle.jpg");
        ss.add("http://pic25.nipic.com/20121123/9830190_172507668164_2.jpg");
        ss.add("http://img5q.duitang.com/uploads/item/201504/24/20150424H4855_LfPvj.jpeg");
        ss.add("http://bcs.91.com/rbpiczy/Wallpaper/2014/11/17/91496cd61ea94d1598345b94c6d246f7-9.jpg");
        mBannerViewPager.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position) {
                Log.d(TAG, "getView: "+position);
                int p = position;
                if (p >= ss.size()) {
                    p = ss.size() - 1;
                }
                ImageView iv = new ImageView(MainActivity.this);
                Glide.with(MainActivity.this)
                        .load(ss.get(p))
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                        .into(iv);
                return iv;
            }
        });
    ```
    
2. 实现无限循环效果  
    实现自动轮播的方式
    - Timer 写一个定时器
    - Handler发送消息(Handle可能出现内存泄漏问题，activity生命周期没有handle生命周期大)
    - start Thread() 开一个子线程
    
    **我们这里采用Handle的方式：**
    
    ```
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //每隔多少秒后切换到下一页
            setCurrentItem(getCurrentItem() + 1);
            //不断执行
            startRoll();
        }
    };
    ```
    
    ```
    /**
     * 实现自动轮播
     */
    public void startRoll() {
        //清除消息，防止多次发送
        mHandler.removeMessages(SCROLL_MSG);

        //发送延迟空消息，实现自动轮播
        mHandler.sendEmptyMessageDelayed(SCROLL_MSG, mScrollNextTime);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        //销毁Handle 停止发送，解决内存泄漏
        mHandler.removeMessages(SCROLL_MSG);
        mHandler = null;
    }
    ```
    
3. 改变切换的速率  
    在setCurrentItem源码中我们可以看到下面一段代码  
    
    ```
    private Scroller mScroller;
    mScroller.startScroll(sx, sy, dx, dy, duration);
    ```
    而改变速率只有这一个方式，因此我们需要改变duration的时间但是duration是一个局部变量无法改变，因此我们只能改变mScroller(通过反射方法)。
    
    - 自定义Scroller方法
    
    ```
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
            mScrol  lDuration = scrollDuration;
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
    ```
    
    - 添加反射获取到mScroller并重新赋值成我们自定义的Scroller方法  
    
    在构造方法下添加下面代码：
    
    ```
    try {
            //改变ViewPager的速率
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            //设置参数
            mScroller.setAccessible(true);
            ／／第二个参数为插值器，需要的可以添加第二个参数
            mBannerScroller = new BannerScroller(context);
            mScroller.set(this, mBannerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    ```
    
    设置持续时间
    
    ```
    /**
     * 设置页面切换动画持续时间
     */
    public void setScrollDuration(int scrollDuration) {
        mBannerScroller.setScrollDuration(scrollDuration);
    }
    ```
    
    
4. 自定义BannerView  
    > 构建自定义BannerView里面包含：轮播图、当前轮播图描述、轮播图点
    
    ```
    /**
     * 类描述：自定义banner <br/>
     * 创建人：吴冬冬<br/>
     * 创建时间：2018/3/2 10:45 <br/>
     */
    public class BannerView extends RelativeLayout{
    
        /**
         * 轮播图
         */
        private BannerViewPager mBannerVp;
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
    
        public BannerView(Context context) {
            this(context, null);
        }
    
        public BannerView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }
    
        public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
    
            //加载布局进当前view
            View view = inflate(context, R.layout.banner_layout, this);
            //初始化View
            initView(view);
    
        }
    
        /**
         * 初始化View
         */
        private void initView(View view) {
            mBannerVp = (BannerViewPager) view.findViewById(R.id.banner_vp);
            mBannerDescribeTv = (TextView) view.findViewById(R.id.banner_describe_tv);
            mBannerDotLl = (LinearLayout) view.findViewById(R.id.banner_dot_ll);
        }
    
        /**
         * 设置适配器
         */
        public void setAdapter(BannerAdapter adapter) {
            mBannerAdapter = adapter;
            mBannerVp.setAdapter(adapter);
        }
    
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
    }
    ```
    
    
    ```
    <?xml version="1.0" encoding="utf-8"?>
    <RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent">
    
        <!--滚动条-->
        <com.dongdongwu.test.banner.BannerViewPager
            android:id="@+id/banner_vp"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    
    
        <!--广告描述和小点布局-->
        <RelativeLayout
            android:paddingBottom="5dp"
            android:paddingTop="5dp"
            android:paddingStart="10dp"
            android:paddingEnd="10dp"
            android:background="#66000000"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentBottom="true">
    
            <!--当前广告的描述-->
            <TextView
                android:id="@+id/banner_describe_tv"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#fff"
                android:text="广告的描述"
                android:textSize="12dp" />
    
            <!--点布局-->
            <LinearLayout
                android:id="@+id/banner_dot_ll"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:orientation="horizontal"
                android:layout_centerVertical="true">
    
            </LinearLayout>
        </RelativeLayout>
    
    </RelativeLayout>
    ```

5. 初始化点的指示器和广告位
    
    ```
    /**
     * 设置适配器
     */
    public void setAdapter(BannerAdapter adapter) {
        mBannerAdapter = adapter;
        if (mDotMarginTop > 0) {
            RelativeLayout.LayoutParams bannerVpLayoutParams = (LayoutParams) mBannerVp.getLayoutParams();
            bannerVpLayoutParams.setMargins(0, 0, 0, (int) mDotMarginTop);
            mBannerVp.setLayoutParams(bannerVpLayoutParams);
        }
        mBannerVp.setAdapter(adapter);

        //初始化点的指示器
        initDotIndicator();

        //设置初始化的第一条广告
        mBannerDescribeTv.setText(mBannerAdapter.getBannerDescribe(mCurrentDotPosition));

        //设置轮播后广告和小点选中
        mBannerVp.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                //监听当前选中的位置，并改变小点状态
                pageSelect(position % mBannerAdapter.getCount());
            }
        });
    }

    /**
     * 监听当前选中的位置，并改变小点状态
     * 设置广告描述
     */
    private void pageSelect(int position) {
        //把之前的点变成未选中状态
        DotIndicatorView oldDotView = (DotIndicatorView) mBannerDotLl.getChildAt(mCurrentDotPosition);
        oldDotView.setDrawable(mDotIndicatorNoSelectDrawable);

        //把position位置的点变成选中状态
        mCurrentDotPosition = position;
        DotIndicatorView nowDotView = (DotIndicatorView) mBannerDotLl.getChildAt(mCurrentDotPosition);
        nowDotView.setDrawable(mDotIndicatorSelectDrawable);

        //设置广告
        mBannerDescribeTv.setText(mBannerAdapter.getBannerDescribe(mCurrentDotPosition));
    }

    /**
     * 初始化点的指示器
     */
    private void initDotIndicator() {
        //获取轮播图数量，也就是要添加点的数量
        int dotCount = mBannerAdapter.getCount();

        //让点的位置在右边
        mBannerDotLl.setGravity(Gravity.RIGHT);

        for (int i = 0; i < dotCount; i++) {
            //不断往点的指示器中添加点
            DotIndicatorView dotIndicatorView = new DotIndicatorView(mContext);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp2px(8), dp2px(8));
            //设置左右间距
            params.leftMargin = params.rightMargin = dp2px(3);
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
    ```
    
6. 自定义属性

    ```
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <declare-styleable name="BannerView">
            <!--是否允许无限轮回-->
            <attr name="enableUnlimitedScroll" format="boolean"/>
            <!--是否允许自动滚动-->
            <attr name="enableAutoScroll" format="boolean"/>
            <!--提示点类型-->
            <attr name="dotHintType" format="enum">
                <enum name="dot" value="0"/>
                <enum name="number" value="1"/>
            </attr>
            <!--点距离滚动条目距离-->
            <attr name="dotMarginTop" format="dimension"/>
            <!--点选中的颜色-->
            <attr name="dotIndicatorSelectColor" format="color|reference"/>
            <!--点未选中的颜色-->
            <attr name="dotIndicatorNoSelectColor" format="color|reference"/>
            <!--点的大小-->
            <attr name="dotSize" format="dimension"/>
            <!--点的间距-->
            <attr name="dotDistance" format="dimension"/>
            <!--点的位置-->
            <attr name="dotGravity" format="enum">
                <enum name="center" value="0"/>
                <enum name="right" value="1"/>
                <enum name="left" value="-1"/>
            </attr>
            <!--底部条颜色-->
            <attr name="bottomColor" format="color"/>
            <!--宽高比-->
            <attr name="wideProportion" format="float" />
            <attr name="heightProportion" format="float" />
        </declare-styleable>
    </resources>
    ```
    
7. 初始化自定义属性

    ```
    /**
     * 初始化自定义属性
     */
    private void initAttribute(AttributeSet attrs) {
        TypedArray array = mContext.obtainStyledAttributes(attrs, R.styleable.BannerView);

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
    ```
    
# 参考
[Android无限广告轮播 - 自定义BannerView](https://www.jianshu.com/p/90d2aa937088)