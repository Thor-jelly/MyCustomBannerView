package com.dongdongwu.mycustombannerview;

import android.view.View;

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
     * @param currentDotPosition 当前选中轮播图位置
     */
    public String getBannerDescribe(int currentDotPosition){
        return "";
    };
}
