package com.dongdongwu.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dongdongwu.mycustombannerview.BannerAdapter;
import com.dongdongwu.mycustombannerview.BannerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "123===";
    private BannerView mBannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBannerView = (BannerView) findViewById(R.id.bv);


        initVp();
    }

    private void initVp() {
        final List<String> ss = new ArrayList<>();
        ss.add("http://bbs.everychina.com/data/attachment/forum/201312/04/102637cwnwrywccfxwab22.jpg");
        ss.add("http://img3.100bt.com/upload/ttq/20131121/1385034610130_middle.jpg");
        ss.add("http://pic25.nipic.com/20121123/9830190_172507668164_2.jpg");
        ss.add("http://img5q.duitang.com/uploads/item/201504/24/20150424H4855_LfPvj.jpeg");
        ss.add("http://bcs.91.com/rbpiczy/Wallpaper/2014/11/17/91496cd61ea94d1598345b94c6d246f7-9.jpg");

        final List<String> ss1 = new ArrayList<>();
        ss1.add("http://bbs.everychina.com/data/attachment/forum/201312/04/102637cwnwrywccfxwab22.jpg");
        ss1.add("http://img3.100bt.com/upload/ttq/20131121/1385034610130_middle.jpg");
        ss1.add("http://pic25.nipic.com/20121123/9830190_172507668164_2.jpg");
        ss1.add("http://img5q.duitang.com/uploads/item/201504/24/20150424H4855_LfPvj.jpeg");
        ss1.add("http://bcs.91.com/rbpiczy/Wallpaper/2014/11/17/91496cd61ea94d1598345b94c6d246f7-9.jpg");

        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View reuseView) {
//                Log.d(TAG, "MainActivity--->>>getView: "+position);
                ImageView iv = null;
                if (reuseView == null) {
                    iv = new ImageView(MainActivity.this);
                } else {
                    iv = (ImageView) reuseView;
                    Log.d(TAG, "getView: 界面复用View"+reuseView);
                }
                Glide.with(MainActivity.this)
                        .load(ss.get(position))
                        .apply(new RequestOptions().placeholder(R.mipmap.ic_launcher))
                        .into(iv);
                return iv;
            }

            @Override
            public int getCount() {
                return ss.size();
            }

            @Override
            public String getBannerDescribe(int currentDotPosition) {
                return ss1.get(currentDotPosition);
            }
        });
    }
}
