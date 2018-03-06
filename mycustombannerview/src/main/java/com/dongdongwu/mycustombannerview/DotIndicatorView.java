package com.dongdongwu.mycustombannerview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 类描述：点<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/3/2 11:16 <br/>
 */

public class DotIndicatorView extends View {
    private Drawable mDrawable;

    public DotIndicatorView(Context context) {
        this(context, null);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DotIndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawable != null) {
            /*mDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            mDrawable.draw(canvas);*/

            //画圆
            Bitmap bitmap = drawableBitmap(mDrawable);

            //把bitmap变为圆的
            Bitmap circleBitmap = getCircleBitmap(bitmap);

            //把圆形的bitmap绘制到画布上
            canvas.drawBitmap(circleBitmap, 0, 0, null);

            circleBitmap.recycle();
            circleBitmap = null;
        }
    }

    /**
     * 把bitmap变为圆的
     */
    private Bitmap getCircleBitmap(Bitmap bitmap) {
        //创建一个什么都没有的bitmap
        Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        //创建一个画布
        Canvas canvas = new Canvas(outBitmap);
        //在画布上画一个圆
        Paint paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        //仿抖动
        paint.setDither(true);
        //画圆
        canvas.drawCircle(getMeasuredWidth()/2, getMeasuredHeight()/2, getMeasuredWidth()/2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //再把原来的bitmap绘制到新的圆上
        canvas.drawBitmap(bitmap, 0, 0, paint);

        //bitmap使用完了 必须回收
        bitmap.recycle();
        bitmap = null;

        return outBitmap;
    }

    /**
     * 从drawable中获取bitmap
     */
    private Bitmap drawableBitmap(Drawable drawable) {
        if (drawable != null) {
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else {
                //创建一个什么都没有的bitmap
                Bitmap outBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
                //创建一个画布
                Canvas canvas = new Canvas(outBitmap);
                //把drawable画到bitmap上
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                drawable.draw(canvas);
                return outBitmap;
            }
        }
        return null;
    }

    /**
     * 设置drawable并重新绘制点
     */
    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
        //重新绘制点view
        invalidate();
    }
}
