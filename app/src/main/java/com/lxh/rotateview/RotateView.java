package com.lxh.rotateview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;

import java.util.ArrayList;

/**
 * Created by lxh on 2015/10/26.
 */
public class RotateView extends View {

    private static final int DURATION = 1000;

    private ArrayList<BitmapPoint> dataList;

    private ArrayList<Bitmap> unusedData;

    private float mValue;

    private Paint mPaint;

    private ValueAnimator animator;

    public RotateView(Context context) {
        super(context);
        init(context);
    }

    public RotateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RotateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        dataList = new ArrayList<BitmapPoint>();
        unusedData = new ArrayList<Bitmap>();
    }

    public void setData(ArrayList<Bitmap> datas) {
        if (datas == null || datas.size() < 4) {
            throw new RuntimeException("data size must be more than four");
        }
        dataList.add(new BitmapPoint(datas.get(0), BitmapPoint.BEFORE_RIGHT));
        dataList.add(new BitmapPoint(datas.get(1), BitmapPoint.BEFORE_LEFT));
        dataList.add(new BitmapPoint(datas.get(2), BitmapPoint.AFTER_LEFT));
        dataList.add(new BitmapPoint(datas.get(3), BitmapPoint.AFTER_RIGHT));
        int size = datas.size();
        for (int i = 4; 3 < i && i < size; i++) {
            unusedData.add(datas.get(i));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(dataList != null){
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                BitmapPoint track = dataList.get(i);
                track.handleStatus();
                mPaint.setAlpha(track.getAlpha());
                canvas.drawBitmap(track.getmBitmap(), track.getMatrix(), mPaint);
            }
        }
    }

    /**
     * 此方法释放对数据的引用，不过调用之后需要重新setData之后才可以start
     */
    public void destory() {
        animator.cancel();
        animator = null;
        dataList.clear();
        dataList = null;
        unusedData.clear();
        unusedData = null;
    }


    public void start() {
        if (dataList.size() == 0) {
            throw new RuntimeException("before start must set data");
        }
        animator = ValueAnimator.ofFloat(1.0F, 0.5F);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(DURATION).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (Float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                int size = dataList.size();
                for (int i = 0; i < size; i++) {
                    BitmapPoint point = dataList.get(i);
                    switch (point.getDirection()) {
                        case 0:
                            if (unusedData.size() > 0) {
                                unusedData.add(point.getmBitmap());
                                point.setmBitmap(unusedData.get(0));
                                unusedData.remove(0);
                            }
                            point.setDirection(BitmapPoint.AFTER_RIGHT);
                            break;
                        case 1:
                            point.setDirection(BitmapPoint.BEFORE_RIGHT);
                            break;
                        case 2:
                            point.setDirection(BitmapPoint.BEFORE_LEFT);
                            break;
                        case 3:
                            point.setDirection(BitmapPoint.AFTER_LEFT);
                            break;

                    }
                }
            }
        });
    }


    private class BitmapPoint {

        public static final int BEFORE_RIGHT = 0;
        public static final int BEFORE_LEFT = 1;
        public static final int AFTER_LEFT = 2;
        public static final int AFTER_RIGHT = 3;


        private Bitmap mBitmap;

        private int direction; //表示当前移动路径

        private Matrix matrix = new Matrix();//当前缩放偏移矩阵

        private int alpha = 255;//当前透明度

        public BitmapPoint(Bitmap bitmap, int direction) {
            this.mBitmap = bitmap;
            this.direction = direction;
        }

        public void handleStatus() {
            float translateX = 0;
            float translateY = 0;
            matrix.reset();
            switch (direction) {
                case BEFORE_RIGHT://前右
                    matrix.postScale(mValue, mValue);
                    translateX = getWidth() / 2 - mBitmap.getWidth() / 2 + getWidth() * (1 - mValue);
                    translateY = getHeight() / 2 - mBitmap.getHeight() / 2 * mValue;
                    matrix.postTranslate(translateX, translateY);
                    alpha = (int) (mValue * 255);
                    break;
                case BEFORE_LEFT: //前左
                    matrix.postScale(1.5f - mValue, 1.5f - mValue);
                    translateX = (getWidth() / 2 - mBitmap.getWidth() / 2) * (1 - mValue) * 2;
                    translateY = getHeight() / 2 - mBitmap.getHeight() / 2 * (1.5f - mValue);
                    matrix.postTranslate(translateX, translateY);
                    alpha = (int) ((1.5 - mValue) * 255);
                    break;
                case AFTER_LEFT: //后左
                    matrix.postScale(1 - mValue, 1 - mValue);
                    translateX = getWidth() / 2 - getWidth() / 2 * (1.0f - mValue) * 2;
                    translateY = getHeight() / 2 - mBitmap.getHeight() / 2 * (1.0f - mValue);
                    matrix.postTranslate(translateX, translateY);
                    alpha = (int) ((1 - mValue) * 255 * 0.5);
                    break;
                case AFTER_RIGHT://后右
                    matrix.postScale(mValue - 0.5f, mValue - 0.5f);
                    translateX = getWidth() - mBitmap.getWidth() / 2 - (getWidth() / 2 - mBitmap.getWidth() / 2) * (1 - mValue) * 2;
                    translateY = getHeight() / 2 - mBitmap.getHeight() / 2 * (mValue - 0.5f);
                    matrix.postTranslate(translateX, translateY);
                    alpha = (int) (mValue * 255 * 0.5);
                    break;
            }
        }


        public Matrix getMatrix() {
            return matrix;
        }

        public int getAlpha() {
            return alpha;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public void setmBitmap(Bitmap mBitmap) {
            this.mBitmap = mBitmap;
        }

        public Bitmap getmBitmap() {
            return mBitmap;
        }
    }

}
