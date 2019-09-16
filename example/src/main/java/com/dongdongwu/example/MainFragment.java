package com.dongdongwu.example;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dongdongwu.mycustombannerview.BannerAdapter;
import com.dongdongwu.mycustombannerview.BannerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：//TODO:(这里用一句话描述这个方法的作用)    <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2019/9/12 16:56 <br/>
 */
public class MainFragment extends Fragment {
    private static final String TAG = "123===";
    private BannerView mBannerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBannerView = (BannerView) view.findViewById(R.id.bv);
        TextView tv = view.findViewById(R.id.tv);

        initVp();
    }

    private void initVp() {
        final List<String> ss = new ArrayList<>();
        ss.add("https://bbs.everychina.com/data/attachment/forum/201312/04/102637cwnwrywccfxwab22.jpg");
        ss.add("https://img3.100bt.com/upload/ttq/20131121/1385034610130_middle.jpg");
//        ss.add("https://pic25.nipic.com/20121123/9830190_172507668164_2.jpg");
        ss.add("https://img5q.duitang.com/uploads/item/201504/24/20150424H4855_LfPvj.jpeg");
        ss.add("https://bcs.91.com/rbpiczy/Wallpaper/2014/11/17/91496cd61ea94d1598345b94c6d246f7-9.jpg");

        final List<String> ss1 = new ArrayList<>();
        ss1.add("https://bbs.everychina.com/data/attachment/forum/201312/04/102637cwnwrywccfxwab22.jpg");
        ss1.add("https://img3.100bt.com/upload/ttq/20131121/1385034610130_middle.jpg");
//        ss1.add("https://pic25.nipic.com/20121123/9830190_172507668164_2.jpg");
        ss1.add("https://img5q.duitang.com/uploads/item/201504/24/20150424H4855_LfPvj.jpeg");
        ss1.add("https://bcs.91.com/rbpiczy/Wallpaper/2014/11/17/91496cd61ea94d1598345b94c6d246f7-9.jpg");

        mBannerView.setAdapter(new BannerAdapter() {
            @Override
            public View getView(int position, View reuseView) {
//                Log.d(TAG, "MainActivity--->>>getView: "+position);
                ImageView iv = null;
                if (reuseView == null) {
                    iv = new ImageView(getActivity());
                    iv.setScaleType(ImageView.ScaleType.FIT_XY);
                } else {
                    iv = (ImageView) reuseView;
                    Log.d(TAG, "getView: 界面复用View" + reuseView);
                }
                Glide.with(getActivity())
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
