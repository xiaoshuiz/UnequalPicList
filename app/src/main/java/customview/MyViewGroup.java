package customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shuiz on 2017/7/5.
 */

public class MyViewGroup extends ViewGroup {

    private static final String TAG = "ScrollView";

    private Scroller mScroller;
    private int mScreenHeight; // 窗口高度
    private int mLastY;
    private int mStart;
    private int mEnd;


    /**
     * 自定义ViewGroup的宽
     */
    private int mWidth;
    /**
     * 自定义ViewGroup的高
     */
    private int mHeight;


    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    public MyViewGroup(Context context) {
        this(context, null);
    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        // 获取屏幕高度
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenHeight = metrics.heightPixels;
        Log.d(TAG, "ScrollViewGroup: ScreenHeight " + mScreenHeight);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        Log.e(TAG, sizeWidth + "," + sizeHeight);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;

        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin
                    + lp.bottomMargin;
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，

                // 开启记录下一行的高度
                lineHeight = childHeight;
            } else
            // 否则累加值lineWidth,lineHeight取最大高度
            {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);

            }


        }
        int leftheight = 0;
        int rightheight = 0;
        View child;
        for (int i = 0; i < getChildCount(); i++) {
            if (leftheight <= rightheight) {
                child = getChildAt(i);
                leftheight += child.getMeasuredHeight();
            } else {
                child = getChildAt(i);
                rightheight += child.getMeasuredHeight();
            }
        }
        mHeight = Math.max(rightheight, leftheight);

        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth
                : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight
                : mHeight);

    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {


        int leftheight = 0;
        int rightheight = 0;
        int count = getChildCount();

        View child;
        Log.e("ri", count + "");
        for (int i = 0; i < count; i++) {
            if (leftheight <= rightheight) {
                child = getChildAt(i);
                child.layout(0, leftheight, child.getMeasuredWidth(), leftheight + child.getMeasuredHeight());
                leftheight += child.getMeasuredHeight();
            } else {
                child = getChildAt(i);
                child.layout(child.getMeasuredWidth(), rightheight, 2 * child.getMeasuredWidth(), rightheight + child.getMeasuredHeight());
                rightheight += child.getMeasuredHeight();
            }


        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = (int) event.getY();
                mStart = getScrollY();
                return true;
            case MotionEvent.ACTION_MOVE:
                if (!mScroller.isFinished()) {
                    // 终止滑动
                    mScroller.abortAnimation();
                }
                int offsetY = (int) (mLastY - event.getY());
                Log.d(TAG, "onTouchEvent: getScrollY: " + getScrollY());
                Log.d(TAG, "onTouchEvent: offsetY " + offsetY);
                // 到达顶部,使用offset判断方向
                if (getScrollY() + offsetY < 0) { // 当前已经滑动的 Y 位置
                    offsetY = 0;
                }
                // 到达底部
                if (getScrollY() > getHeight() - mScreenHeight && offsetY > 0) {
                    offsetY = 0;
                }
                scrollBy(0, offsetY);
                // 滑动完成后,重新设置LastY位置
                mLastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mEnd = getScrollY();
                int distance = mEnd - mStart;
                if (distance>mScreenHeight *2/ 3) {
                    //滑到底且有过头效果
                    mScroller.startScroll(0,getScrollY(),0,mHeight-mScreenHeight-getScrollY()+200);
                    mScroller.startScroll(0,mHeight-mScreenHeight+200,0,-200);
                }
                else if (-distance>mScreenHeight*2/3){
                    //滑倒头且有过头效果
                    mScroller.startScroll(0,getScrollY(),0,-getScrollY()-200);
                    mScroller.startScroll(0,-200,0,200);
                }
                else {
                    mScroller.startScroll(0, getScrollY(), 0, 0);
                }

//                  分页滑动
//                if (distance > 0) { // 向上滑动
//                    if (distance < mScreenHeight / 3) {
//                        Log.d(TAG, "onTouchEvent: distance < screen/3");
//                        // 回到原来位置
//
//                        mScroller.startScroll(0, getScrollY(), 0, 0);
//                    } else {
//                        // 滚到屏幕的剩余位置
//                        mScroller.startScroll(0, getScrollY(), 0,0);
//                    }
//                } else {             // 向下滑动
//                    if (-distance < mScreenHeight / 3) {
//                        mScroller.startScroll(0, getScrollY(), 0, 0);
//                    } else {
//                        mScroller.startScroll(0, getScrollY(), 0,0);
//                    }
//                }
                postInvalidate();
        }
        return super.onTouchEvent(event);
    }


    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }
}
