/*
 * Copyright 2015 Rudson Lima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.mei.mdapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {

    public RoundedImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getDrawable();

        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Bitmap.Config.ARGB_8888, true);

        int w = getWidth();

        Bitmap roundBitmap = getCroppedBitmap(bitmap, w);
        canvas.drawBitmap(roundBitmap, 0, 0, null);
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {

        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {          //将获取到的Bitmap剪裁为需要的宽度
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        }else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(),          //创建要输出的Bitmap对象，色彩格式为8888
                sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);          //创建canvas，传入要输出的Bitmap对象，对其进行操作

        final Paint paint = new Paint();          //创建Paint
        final Rect rect = new Rect(0, 0, sbmp.getWidth(), sbmp.getHeight());          //创建绘制区域的矩形

        paint.setAntiAlias(true);          //抗锯齿
        paint.setFilterBitmap(true);          //位图滤波
        paint.setDither(true);          //防抖动
        canvas.drawARGB(0, 0, 0, 0);          //设置canvas背景色为透明
        paint.setColor(Color.parseColor("#BAB399"));          //设置paint颜色
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.7f,          //绘制圆形边框，混合模式的Dst
                sbmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));          //设置混合模式SRC_IN
        canvas.drawBitmap(sbmp, rect, rect, paint);          //绘制原图，混合模式的Src，第二个rect参数表示剪裁区域，第三个表示绘制区域

        return output;
    }
}