package com.example.mei.mdapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView
{

    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    //private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_4444;
    private static final int COLORDRAWABLE_DIMENSION = 1;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;
    private int mBitmapWidth;
    private int mBitmapHeight;

    private float mDrawableRadius;
    private float mBorderRadius;

    private boolean mReady;
    private boolean mSetupPending;

    public CircleImageView(Context context)
    {
        super(context);

        init();
    }

    public CircleImageView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

        mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_border_width, DEFAULT_BORDER_WIDTH);
        mBorderColor = a.getColor(R.styleable.CircleImageView_border_color, DEFAULT_BORDER_COLOR);

        a.recycle();

        init();
    }

    private void init()
    {
        super.setScaleType(SCALE_TYPE);
        mReady = true;

        if (mSetupPending)
        {
            setup();
            mSetupPending = false;
        }
    }

    @Override
    public ScaleType getScaleType()
    {
        return SCALE_TYPE;
    }

    @Override
    public void setScaleType(ScaleType scaleType)
    {
        if (scaleType != SCALE_TYPE)
        {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if (getDrawable() == null)
        {
            return;
        }

        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mDrawableRadius, mBitmapPaint);          //绘制圆形图片
        if (mBorderWidth != 0)
        {
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mBorderRadius, mBorderPaint);          //绘制圆形边框
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        setup();
    }

    public int getBorderColor()
    {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor)
    {
        if (borderColor == mBorderColor)
        {
            return;
        }

        mBorderColor = borderColor;
        mBorderPaint.setColor(mBorderColor);
        invalidate();
    }

    public int getBorderWidth()
    {
        return mBorderWidth;
    }

    public void setBorderWidth(int borderWidth)
    {
        if (borderWidth == mBorderWidth)
        {
            return;
        }

        mBorderWidth = borderWidth;
        setup();
    }

    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setup();
    }

    @Override
    public void setImageDrawable(Drawable drawable)
    {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFromDrawable(drawable);
        setup();
    }

    @Override
    public void setImageResource(int resId)
    {
        super.setImageResource(resId);
        mBitmap = getBitmapFromDrawable(getDrawable());          //获取ImageView里面的Drawable图片并转化为Bitmap
        setup();          //配置画笔
    }

    @Override
    public void setImageURI(Uri uri)
    {
        super.setImageURI(uri);
        mBitmap = getBitmapFromDrawable(getDrawable());
        setup();
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable)
    {
        if (drawable == null)
        {
            return null;
        }

        if (drawable instanceof BitmapDrawable)
        {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try
        {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable)
            {
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION, COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
            }
            else
            {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        catch (OutOfMemoryError e)
        {
            return null;
        }
    }

    private void setup()
    {
        if (!mReady)
        {
            mSetupPending = true;
            return;
        }

        if (mBitmap == null)
        {
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);          //创建画笔着色器

        mBitmapPaint.setAntiAlias(true);                                //抗锯齿
        mBitmapPaint.setShader(mBitmapShader);               //设置图片画笔的着色器

        mBorderPaint.setStyle(Paint.Style.STROKE);             //设置描边画笔为边框风格
        mBorderPaint.setAntiAlias(true);                                 //设置描边画笔抗锯齿
        mBorderPaint.setColor(mBorderColor);                     //设置描边画笔边框颜色
        mBorderPaint.setStrokeWidth(mBorderWidth);         //设置描边画笔边框宽度

        mBitmapHeight = mBitmap.getHeight();                      //获取原始图片高度
        mBitmapWidth = mBitmap.getWidth();                       //获取原始图片宽度

        mBorderRect.set(0, 0, getWidth(), getHeight());            //圆形边框的外界矩形长宽为ImageView的长宽
        mBorderRadius = Math.min((mBorderRect.height() - mBorderWidth) / 2, (mBorderRect.width() - mBorderWidth) / 2);            //设置圆形边框半径

        //圆形绘图区的外界矩形长宽为mBorderRect减去边框宽度
        mDrawableRect.set(mBorderWidth, mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
        mDrawableRadius = Math.min(mDrawableRect.height() / 2, mDrawableRect.width() / 2);            //设置圆形绘图区半径

        updateShaderMatrix();          //配置着色器的图形矩阵
        invalidate();                           //重绘界面
    }

    private void updateShaderMatrix()
    {
        float scale;                          //缩放比
        float dx = 0;                       //横向偏移量
        float dy = 0;                       //纵向偏移量

        mShaderMatrix.set(null);          //清空矩阵参数

        //计算缩放比及偏移量
        if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight)
        {
            scale = mDrawableRect.height() / (float) mBitmapHeight;                     //将原图放至短边与半径相等的大小，则圆形区域正好可以截取中间部分来显示
            dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;            //原图的宽度\高度与圆形直径差值的一半即为偏移量，即通过偏移可以将画笔绘制的中心点移到圆心处
        }
        else
        {
            scale = mDrawableRect.width() / (float) mBitmapWidth;                     //将原图放至短边与半径相等的大小，则圆形区域正好可以截取中间部分来显示
            dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;         //原图的宽度\高度与圆形直径差值的一半即为偏移量，即通过偏移可以将画笔绘制的中心点移到圆心处
        }

        //根据计算的缩放比及偏移量调整原图大小
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);

        mBitmapShader.setLocalMatrix(mShaderMatrix);          //给着色器设置计算好的矩阵
    }

}