package customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.example.shuiz.myview.R;

public class MmyView extends View {
    /**
     * �Զ���View�Ŀ�
     */
    private int mWidth;
    /**
     * �Զ���View�ĸ�
     */
    private int mHeight;
    /**
     * �Զ���View��ͼƬ
     */
    private Bitmap mImage;
    /**
     * ͼƬ������ģʽ
     */
    private int mImageScale;
    private static final int IMAGE_SCALE_FITXY = 0;
    private static final int IMAGE_SCALE_CENTER = 1;
    /**
     * ͼƬ�ı���
     */
    private String mTitle;
    /**
     * �������ɫ
     */
    private int mTextColor;
    /**
     * ����Ĵ�С
     */
    private int mTextSize;

    private Paint mPaint;
    /**
     * �ı��Ļ��Ʒ�Χ
     */
    private Rect mTextBound;
    /**
     * ��Ҫ���Ƶ��������η�Χ
     */
    private Rect rect;

    public MmyView(Context context) {
        this(context, null);
    }

    public MmyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    /**
     * ��ʼ���Զ�������
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public MmyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MmyView, defStyle, 0);

        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);

            switch (attr) {
                case R.styleable.MmyView_image:
                    mImage = BitmapFactory.decodeResource(getResources(), a.getResourceId(attr, 0));
                    break;
                case R.styleable.MmyView_imageScaleType:
                    mImageScale = a.getInt(attr, 0);
                    break;
                case R.styleable.MmyView_titleText:
                    mTitle = a.getString(attr);
                    break;
                case R.styleable.MmyView_titleTextColor:
                    mTextColor = a.getColor(attr, Color.BLACK);
                    break;
                case R.styleable.MmyView_titleTextSize:
                    mTextSize = a.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                            16, getResources().getDisplayMetrics()));
                    break;

            }
        }
        a.recycle();
        rect = new Rect();
        mPaint = new Paint();
        mTextBound = new Rect();
        mPaint.setTextSize(mTextSize);
        // 计算了描绘字体需要的范围
        mPaint.getTextBounds(mTitle, 0, mTitle.length(), mTextBound);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        /**
         * 设置宽度
         */

        int specSize = MeasureSpec.getSize(widthMeasureSpec);


        // 由图片决定的宽

        float newwidth = (float) ((specSize / 2) - getPaddingLeft() - getPaddingRight());
        float scale = (float) newwidth / mImage.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap newbm = Bitmap.createBitmap(mImage, 0, 0, mImage.getWidth(), mImage.getHeight(), matrix,
                true);

        mImage = newbm;


        int desireByImg = getPaddingLeft() + getPaddingRight() + mImage.getWidth();


        mWidth = desireByImg;
        Log.e("xxx", "AT_MOST");


        /***
         * 设置高度
         */





        int desire = getPaddingTop() + getPaddingBottom() + mImage.getHeight() + mTextBound.height();

        mHeight = desire;


        setMeasuredDimension(mWidth, mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        /**
         * 边框
         */
        mPaint.setStrokeWidth(4);
        mPaint.setStyle(Style.FILL);
        mPaint.setColor(Color.CYAN);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);

        rect.left = getPaddingLeft();
        rect.right = mWidth - getPaddingRight();
        rect.top = getPaddingTop();
        rect.bottom = mHeight - getPaddingBottom();

        mPaint.setColor(mTextColor);
        mPaint.setStyle(Style.FILL);
        /**
         * 当前设置的宽度小于字体需要的宽度，将字体改为xxx...
         */
        if (mTextBound.width() > mWidth) {
            TextPaint paint = new TextPaint(mPaint);
            String msg = TextUtils.ellipsize(mTitle, paint, (float) mWidth - getPaddingLeft() - getPaddingRight(),
                    TextUtils.TruncateAt.END).toString();
            canvas.drawText(msg, getPaddingLeft(), mHeight - getPaddingBottom(), mPaint);

        } else {
            //正常情况，将字体居中
            canvas.drawText(mTitle, mWidth / 2 - mTextBound.width() * 1.0f / 2, mHeight - getPaddingBottom(), mPaint);
        }

        //取消使用掉的快
        rect.bottom -= mTextBound.height();

        if (mImageScale == IMAGE_SCALE_FITXY) {
            canvas.drawBitmap(mImage, null, rect, mPaint);
        } else {
            //计算居中的矩形范围
            rect.left = mWidth / 2 - mImage.getWidth() / 2;
            rect.right = mWidth / 2 + mImage.getWidth() / 2;
            rect.top = (mHeight - mTextBound.height()) / 2 - mImage.getHeight() / 2;
            rect.bottom = (mHeight - mTextBound.height()) / 2 + mImage.getHeight() / 2;

            canvas.drawBitmap(mImage, null, rect, mPaint);
        }

    }

}
