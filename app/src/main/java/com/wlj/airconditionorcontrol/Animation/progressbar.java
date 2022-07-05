package com.wlj.airconditionorcontrol.Animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.wlj.airconditionorcontrol.R;

//自定义进度条控件

public class progressbar extends View{
    private Paint paint;//画笔
    private Paint shaderPaint;//彩色画笔
    private Paint bitmapPaint;//图片画笔
    private Paint textPaint;//文字画笔

    private int width;  //控件宽度
    private int height;  //控件高度
    private int radius;  //bar圆半径
    private float outerArcWidth;    //外圆弧宽度
    private float insideArcWidth;    //内圆弧宽度
    private float spaceWidth;    //圆弧中间距离
    private float percentTextSize;   //中间间距
    private float scrollCircleRadius;  //滑动小球半径
    private int pinkColor;     //圆弧底色
    private int yellowColor;
    private int pinkRedColor;
    private int redColor;
    private int lightgray;
    private int grayColor;
    private double spaceAngle = 22.5;  //圆角弧度
    private double floatAngel = 50;    //两条圆弧的起始角度 ，起始角度控制半圆开口的大小，数值越小开口越大，数值越大开口越小

    private Bitmap mBitmap;

    private Canvas mCanvas;      //自定义的画布，目的是为了能画出重叠的效果

    private double mAngel;

    private float insideArcRadius;
    private double aimPercent = 0;
    private float[] pos;                // 当前点的实际位置
    private float[] tan;                // 当前点的tangent值,用于计算图片所需旋转的角度
    private Bitmap mBitmapBackDeepRed;  // 箭头图片
    private Bitmap mBitmapBackYellow;  // 箭头图片
    private Bitmap mBitmapBackPink;  // 箭头图片
    private Bitmap mBitmapBackRed;  // 箭头图片
    private Matrix mMatrix;             // 矩阵,用于对图片进行一些操作

    private RectF insideArea;            //内圆的矩形

    private Bitmap mBitmapBack;
    // 动效过程监听器
    private ValueAnimator.AnimatorUpdateListener mUpdateListener;
    private Animator.AnimatorListener mAnimatorListener;
    //过程动画
    private ValueAnimator mValueAnimator;
    // 用于控制动画状态转换
    private Handler mAnimatorHandler;
    // 默认的动效周期 2s
    private int defaultDuration = 2;


    public progressbar(Context context) {
        super(context);
        initView(context);

    }

    public progressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public progressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    private void initView(Context context) {
        shaderPaint = new Paint();
        textPaint = new Paint();

        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);

        bitmapPaint = new Paint();
        bitmapPaint.setStyle(Paint.Style.FILL);
        bitmapPaint.setAntiAlias(true);

        outerArcWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp2);
        insideArcWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp12);
        spaceWidth = context.getResources().getDimensionPixelOffset(R.dimen.dp12);
        scrollCircleRadius = context.getResources().getDimensionPixelOffset(R.dimen.dp4);
        percentTextSize = context.getResources().getDimensionPixelOffset(R.dimen.dp8);
        pinkColor = context.getResources().getColor(R.color.glay3);
        yellowColor = context.getResources().getColor(R.color.orange);
        pinkRedColor = context.getResources().getColor(R.color.timeCome1);
        redColor = context.getResources().getColor(R.color.crimson);
        lightgray = context.getResources().getColor(R.color.lightgray);
        grayColor = context.getResources().getColor(R.color.content_background_up_view);


        pos = new float[2];
        tan = new float[2];
        mBitmapBackDeepRed = BitmapFactory.decodeResource(context.getResources(), R.mipmap.order_dot);
        mBitmapBackRed = BitmapFactory.decodeResource(context.getResources(), R.mipmap.order_dot);
        mBitmapBackPink = BitmapFactory.decodeResource(context.getResources(), R.mipmap.order_dot);
        mBitmapBackYellow = BitmapFactory.decodeResource(context.getResources(), R.mipmap.order_dot);
        mMatrix = new Matrix();


    }

    private int count = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i("PercentVIew", "开始绘制" + count);
        long startTime = System.currentTimeMillis();
        count++;
        width = getWidth(); //获取宽度
        height = getHeight();//获取高度
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        radius = height / 2;
        insideArcRadius = radius - scrollCircleRadius - spaceWidth;//内弧半径
//        Log.i(TAG,"最外园半径"+radius+"\n高度为"+height);
//        Log.i(TAG,"最外园半径"+Math.sin(Math.toRadians(spaceAngle)));
        paintPercentBack(mCanvas);
        paintPercent(mAngel, aimPercent, mCanvas);
        canvas.drawBitmap(mBitmap, 0, 0, null);
        long endTime = System.currentTimeMillis();
        Log.i("PercentVIew", "绘制结束" + (endTime - startTime));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 画线的底色
     */
    private void paintPercentBack(Canvas canvas) {
        paint.setColor(grayColor);
        paint.setStrokeWidth(outerArcWidth);//outerArcWidth
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);//设置为圆角
        paint.setAntiAlias(true);
        //绘制里层大宽度弧形底色
        paint.setColor(pinkColor);
        paint.setStrokeWidth(insideArcWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(width / 2 - insideArcRadius, radius - insideArcRadius, width / 2 + insideArcRadius, radius + insideArcRadius),
                (float) (180 - floatAngel),
                (float) (180 + 2 * floatAngel), false, paint);

    }

    /***
     * 4个色值由浅到深分别是 ffd200 ff5656 fa4040 f60157
     * 等级划分：0-20%    21-60%    61-90%    90以上
     * 绘制颜色线条
     * 主要用到Xfermode的SRC_ATOP显示上层绘制
     * setStrokeCap   Paint.Cap.ROUND设置为圆角矩形
     */
    private void paintPercent(double percent, double aimPercent, Canvas canvas) {
        double roateAngel = percent * 0.01 * 225;
        shaderPaint.setColor(yellowColor);
        shaderPaint.setStrokeCap(Paint.Cap.ROUND);
        shaderPaint.setAntiAlias(true);
        shaderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));//shaderPaint.setColor(yellowColor);
        if (aimPercent >= 0 && aimPercent <= 20) {
            int colorSweep[] = { pinkRedColor,pinkRedColor};
            float position[]={0.2f,0.7f};
            SweepGradient sweepGradient=new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        } else if (aimPercent > 20 && aimPercent <= 60) {
            int colorSweep[] = {yellowColor, yellowColor};
            float position[] = {0.5f, 0.7f};
            SweepGradient sweepGradient = new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        } else if (aimPercent > 60 && aimPercent <= 90) {
            int colorSweep[] = {redColor, redColor, redColor, redColor, redColor};
            float position[] = {0.25f, 0.35f, 0.5f, 0.7f, 0.8f};
            SweepGradient sweepGradient = new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        } else if (aimPercent > 90) {
            int colorSweep[] = {lightgray, lightgray, lightgray, lightgray, lightgray, lightgray};
            float position[] = {0.2f, 0.4f, 0.5f, 0.7f, 0.9f, 1.0f};
            SweepGradient sweepGradient = new SweepGradient(width / 2, radius, colorSweep, position);
            shaderPaint.setShader(sweepGradient);
        }
        if (aimPercent >= 0 && aimPercent <= 20) {//目的是为了
            drawInsideArc((float) (180 - floatAngel), (float) roateAngel, canvas, mBitmapBack, pinkRedColor);//在这里改变指示圆点的颜色

        } else if (aimPercent > 20 && aimPercent <= 60) {
            drawInsideArc((float) (180 - floatAngel), (float) roateAngel, canvas, mBitmapBack, yellowColor);//在这里改变指示圆点的颜色

        } else if (aimPercent > 60 && aimPercent <= 90) {
            drawInsideArc((float) (180 - floatAngel), (float) (roateAngel - (spaceAngle - floatAngel)), canvas, mBitmapBack,redColor);

        } else if (aimPercent > 90) {
            drawInsideArc((float) (180 - floatAngel), (float) (roateAngel - (spaceAngle - floatAngel)), canvas, mBitmapBack, lightgray);

        }


    }

    /***
     * 画内部圆环渐变
     * @param formDegree 起始角度
     * @param toDegree 旋转角度
     * @param canvas 画布
     */
    private void drawInsideArc(float formDegree, float toDegree, Canvas canvas, Bitmap bitmap, int color) {
        shaderPaint.setStrokeWidth(insideArcWidth);
        shaderPaint.setStyle(Paint.Style.STROKE);
        //内弧半径
        insideArea = new RectF(width / 2 - insideArcRadius, radius - insideArcRadius, width / 2 + insideArcRadius, radius + insideArcRadius);

        canvas.drawArc(insideArea,
                formDegree,
                toDegree, false, shaderPaint);
        if (toDegree != 0) {
            Path orbit = new Path();
            //通过Path类画一个90度（180—270）的内切圆弧路径
            orbit.addArc(insideArea, formDegree, toDegree);
            // 创建 PathMeasure
            PathMeasure measure = new PathMeasure(orbit, false);
            measure.getPosTan(measure.getLength() * 1, pos, tan);
            mMatrix.reset();
            mMatrix.postTranslate(pos[0] - bitmap.getWidth() / 2, pos[1] - bitmap.getHeight() / 2);   // 将图片绘制中心调整到与当前点重合
            canvas.drawPath(orbit, shaderPaint);//绘制外层的线条
            canvas.drawBitmap(bitmap, mMatrix, bitmapPaint);//绘制
            bitmapPaint.setColor(color);
            //绘制实心小圆圈
            canvas.drawCircle(pos[0], pos[1], 30, bitmapPaint);
        }
    }


    /**
     * 设置角度变化，刷新界面
     *
     * @param aimPercent 目标百分比
     */
    public void setProgress(double aimPercent) {
        //两边监测
        if (aimPercent < 1) {
            aimPercent = 1;
        } else if (aimPercent > 99) {
            aimPercent = 100;
        }
        this.aimPercent = aimPercent;
        initListener();
        initHandler();
        initAnimator();
        mValueAnimator.start();

    }

    private void initListener() {
        mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAngel = (float) animation.getAnimatedValue() * aimPercent;
                if (mAngel >= 0 && mAngel <= 20) {
                    bitmapPaint.setColor(lightgray);
                    mBitmapBack = mBitmapBackYellow;
                } else if (mAngel > 20 && mAngel <= 60) {
                    bitmapPaint.setColor(redColor);
                    mBitmapBack = mBitmapBackPink;
                } else if (mAngel > 60 && mAngel <= 90) {
                    bitmapPaint.setColor(pinkRedColor);
                    mBitmapBack = mBitmapBackRed;
                } else {
                    bitmapPaint.setColor(yellowColor);
                    mBitmapBack = mBitmapBackDeepRed;
                }
//                Log.i("TAG", "mAnimatorValue="+mAnimatorValue);
                invalidate();
            }
        };

        mAnimatorListener = new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // getHandle发消息通知动画状态更新
                mAnimatorHandler.sendEmptyMessage(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
    }

    private void initHandler() {
        mAnimatorHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        mValueAnimator.removeAllUpdateListeners();
                        mValueAnimator.removeAllListeners();
                        break;
                    case 1:
                        invalidate();
                        break;
                }

            }
        };
    }

    private void initAnimator() {
        mValueAnimator = ValueAnimator.ofFloat(0, 1).setDuration(defaultDuration);//设置动画时间，这里设置为0，则看不出动画执行的过程

        mValueAnimator.addUpdateListener(mUpdateListener);

        mValueAnimator.addListener(mAnimatorListener);
    }




}

