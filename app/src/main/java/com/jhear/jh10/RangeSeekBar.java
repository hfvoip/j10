package com.jhear.jh10;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * RangeSeekBar is an extension {@link FrameLayout} that adds two draggable thumbs.
 * The user can touch the thumbs and drag left or right to set the start and the end progress level.
 * Clients of the {@link RangeSeekBar} can attach a {@link OnRangeSeekBarListener} to be notified of the user's actions.
 */
public class RangeSeekBar extends FrameLayout {

    private Thumb thumbA,thumbB,thumbC;
    private float progress1 = 0;
    private float progress2 = 50;
    private float progress3 = 80;

    private FrameLayout container;

    private int minDifference = 20;

    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int rangeColor;

    private Paint trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int trackColor;

    private OnRangeSeekBarListener callback;

    private LayoutParams containerLayoutParams;

    private int maxProgress = 100;

    public RangeSeekBar(Context context) {
        this(context, null);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RangeSeekBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        int containerHeight = getResources().getDimensionPixelSize(R.dimen.container_height);
        int containerMargin = getResources().getDimensionPixelSize(R.dimen.container_margin);

        container = new FrameLayout(context);
        containerLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, containerHeight);
        containerLayoutParams.gravity = Gravity.CENTER;
        containerLayoutParams.leftMargin = containerMargin;
        containerLayoutParams.rightMargin = containerMargin;
        addView(container, containerLayoutParams);


        thumbA= new Thumb(context);
        LayoutParams thumbALayoutParams = new LayoutParams(thumbA.getThumbWidth(), thumbA.getThumbWidth());
        thumbALayoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(thumbA, thumbALayoutParams);
        thumbA.setOnTouchListener(thumb1Touch);

        thumbB= new Thumb(context);
        LayoutParams thumbBLayoutParams = new LayoutParams(thumbB.getThumbWidth(), thumbB.getThumbWidth());
        thumbBLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(thumbB, thumbBLayoutParams);
        thumbB.setOnTouchListener(thumb2Touch);

        thumbC = new Thumb(context);
        LayoutParams thumbCLayoutParams = new LayoutParams(thumbC.getThumbWidth(), thumbC.getThumbWidth());
        thumbCLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        container.addView(thumbC, thumbCLayoutParams);
        thumbC.setOnTouchListener(thumb3Touch);

        rangeColor = ContextCompat.getColor(context, R.color.colorAccent);
        rangePaint.setColor(rangeColor);
        rangePaint.setStyle(Paint.Style.STROKE);
        rangePaint.setStrokeWidth(context.getResources().getDimension(R.dimen.line));

        trackColor = ContextCompat.getColor(context, android.R.color.darker_gray);
        trackPaint.setColor(trackColor);
        trackPaint.setStyle(Paint.Style.STROKE);
        trackPaint.setStrokeWidth(context.getResources().getDimension(R.dimen.line));
        trackPaint.setAlpha(130);

        thumbA.setDisableCircleColor(trackColor);
        thumbB.setDisableCircleColor(trackColor);
        thumbC.setDisableCircleColor(trackColor);
    }

    /**
     * Set the color of the two thumbs and the range line connecting the two thumbs.
     * Default color is {@code R.attr.colorControlActivated}
     *
     * @param color The color that replace default range color
     */
    public void setRangeColor(int color) {
        rangeColor = color;
        rangePaint.setColor(color);
        thumbA.setColor(color);
        thumbB.setColor(color);
        thumbC.setColor(color);
        invalidate();
    }

    /**
     * Set the color of the track.
     * Default color is {@code android.R.color.darker_gray} with alpha {@code 130/255 }
     * The color you replace will be fully opaque unless you set a color with an alpha component
     * <br>
     * <br> i.e. {@code Color.parseColor("#77000000")}
     *
     * @param color The color that replace default track color
     */
    public void setTrackColor(int color) {
        trackColor = color;
        trackPaint.setColor(color);
        thumbA.setDisableCircleColor(color);
        thumbB.setDisableCircleColor(color);
        invalidate();
    }

    public void setMax(int max) {
        maxProgress = max;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float dx1 = getDeltaX(thumbA, progress1);
        thumbA.setTranslationX(dx1);

        float dx2 = getDeltaX(thumbB, progress2);
        thumbB.setTranslationX(dx2);

        float dx3 = getDeltaX(thumbC, progress3);
        thumbC.setTranslationX(dx3);

        if (dx1 > (rangePaint.getStrokeWidth() * 3)) {
            canvas.drawLine(thumbA.getHalfThumbWidth() + containerLayoutParams.leftMargin,
                    getHeight() / 2,
                    dx1 + thumbA.getHalfThumbWidth() + containerLayoutParams.leftMargin - (rangePaint.getStrokeWidth() * 3),
                    getHeight() / 2,
                    trackPaint);
        }

        canvas.drawLine(dx1 + thumbA.getHalfThumbWidth() + containerLayoutParams.leftMargin + (rangePaint.getStrokeWidth() * 3),
                getHeight() / 2,
                dx2 + thumbB.getHalfThumbWidth() + containerLayoutParams.rightMargin - (rangePaint.getStrokeWidth() * 3),
                getHeight() / 2,
                trackPaint);

        if (container.getWidth() - containerLayoutParams.leftMargin - containerLayoutParams.rightMargin > dx2 + thumbB.getHalfThumbWidth() + containerLayoutParams.rightMargin + (rangePaint.getStrokeWidth() * 3)) {
            canvas.drawLine(dx2 + thumbB.getHalfThumbWidth() + containerLayoutParams.rightMargin + (rangePaint.getStrokeWidth() * 3),
                    getHeight() / 2,
                    container.getWidth() - containerLayoutParams.leftMargin - containerLayoutParams.rightMargin,
                    getHeight() / 2,
                    trackPaint);
        }

        // 这段是画 start,end 之家的强调的线，我们就用默认的灰色，这段就注释掉把
        /*
        canvas.drawLine(dx1 + thumbA.getHalfThumbWidth() + containerLayoutParams.leftMargin,
                getHeight() / 2,
                dx2 + thumbB.getHalfThumbWidth() + containerLayoutParams.rightMargin,
                getHeight() / 2,
                rangePaint);

         */
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        trackPaint.setAlpha(enabled ? 130 : 80);
        rangePaint.setAlpha(enabled ? 255 : 0);
        thumbA.setOnTouchListener(enabled ? thumb1Touch : null);
        thumbA.setEnabled(enabled);
        thumbB.setOnTouchListener(enabled ? thumb2Touch : null);
        thumbB.setEnabled(enabled);
    }

    private float getDeltaX(Thumb thumb, float progress) {
        return (container.getWidth() - thumb.getThumbWidth()) / (float) maxProgress * progress;
    }

    private OnTouchListener thumb1Touch = new OnTouchListener() {
        private float t1X;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case ACTION_UP:
                    if (thumbA.isPressed() && callback != null) {
                        callback.onRangeValues(RangeSeekBar.this, 1,(int) progress1 );
                    }
                    thumbA.setPressed(false);
                    view.performClick();
                    break;

                case ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    t1X = motionEvent.getX() - motionEvent.getRawX() + thumbA.getTranslationX();
                    thumbA.setPressed(true);
                    break;

                case ACTION_MOVE:
                    float dx = t1X + motionEvent.getRawX();
                    dx = Math.max(thumbA.getHalfThumbWidth(), dx);
                    progress1 = (((dx - (thumbA.getHalfThumbWidth())) / (container.getWidth() - thumbA.getThumbWidth())) * maxProgress);
                    if (progress1 >= progress2 - minDifference) {
                        progress1 = progress2 - minDifference;
                    }

                    invalidate();

                    break;

                default:
                    return false;

            }
            return true;
        }
    };

    private OnTouchListener thumb2Touch = new OnTouchListener() {
        private float t2X;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case ACTION_UP:

                    if (thumbB.isPressed() && callback != null) {
                        callback.onRangeValues(RangeSeekBar.this, 2, (int) progress2 );
                    }
                    thumbB.setPressed(false);

                    view.performClick();

                    break;

                case ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    t2X = motionEvent.getX() - motionEvent.getRawX() + thumbB.getTranslationX();
                    thumbB.setPressed(true);
                    break;

                case ACTION_MOVE:
                    float dx = t2X + motionEvent.getRawX();
                    dx = Math.min(dx, container.getWidth() - thumbB.getHalfThumbWidth());
                    progress2 = (((dx - (thumbB.getHalfThumbWidth())) / (container.getWidth() - thumbB.getThumbWidth())) * maxProgress);
                    if (progress2 <= progress1 + minDifference) {
                        progress2 = progress1 + minDifference;
                    }
                    if (progress2 >= progress3 - minDifference) {
                        progress2 = progress3 - minDifference;
                    }


                    invalidate();

                    break;

                default:
                    return false;

            }
            return true;
        }
    };
    private OnTouchListener thumb3Touch = new OnTouchListener() {
        private float t2X;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case ACTION_UP:

                    if (thumbC.isPressed() && callback != null) {
                        callback.onRangeValues(RangeSeekBar.this, 3,(int)progress3 );
                    }
                    thumbC.setPressed(false);
                    view.performClick();

                    break;

                case ACTION_DOWN:
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    t2X = motionEvent.getX() - motionEvent.getRawX() + thumbC.getTranslationX();
                    thumbC.setPressed(true);
                    break;

                case ACTION_MOVE:
                    float dx = t2X + motionEvent.getRawX();
                    dx = Math.min(dx, container.getWidth() - thumbC.getHalfThumbWidth());
                    progress3 = (((dx - (thumbC.getHalfThumbWidth())) / (container.getWidth() - thumbC.getThumbWidth())) * maxProgress);
                    if (progress3 <= progress2 + minDifference) {
                        progress3 = progress2 + minDifference;
                    }

                    invalidate();

                    break;

                default:
                    return false;

            }
            return true;
        }
    };



    public void setPosProgress(int pos,int progress) {
        if (pos ==0) progress1 = progress;
        if (pos ==1) progress2 = progress;
        if (pos ==2) progress3 = progress;

        invalidate();
    }



    /**
     * Set the min progress threshold between the two Thumbs
     *
     * @param difference the min progress threshold between the two Thumbs
     */
    public void setMinDifference(int difference) {
        minDifference = difference;
    }

    public void setOnRangeSeekBarListener(OnRangeSeekBarListener listener) {
        callback = listener;
    }

    public int getPosProgress(int pos ) {
        if (pos ==0)
            return (int) progress1;
        if (pos ==1)
            return (int) progress2;
        if (pos ==2)
            return (int) progress3;
        return 0;
    }



}
