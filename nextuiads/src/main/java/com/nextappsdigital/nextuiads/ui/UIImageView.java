package com.nextappsdigital.nextuiads.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.nextappsdigital.nextuiads.R;
import com.nextappsdigital.nextuiads.utils.CornersF;
import com.nextappsdigital.nextuiads.utils.UICornersHelper;


public class UIImageView extends AppCompatImageView {

    private final CornersF cornerRadii;
    private final UICornersHelper cornersHelper;
    public UIImageView(Context context) {
        this(context, null);
    }

    public UIImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cornersHelper = new UICornersHelper();
        cornerRadii = new CornersF();
        initializeAttributes(context, attrs, defStyleAttr);
    }
    private void initializeAttributes(Context context, AttributeSet attrs, int defStyleAttr) {
        @SuppressLint("CustomViewStyleable") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NextView, defStyleAttr, 0);

        float defaultRadius = typedArray.getDimension(R.styleable.NextView_android_radius, 0.0f);
        cornerRadii.topLeft = typedArray.getDimension(R.styleable.NextView_android_topLeftRadius, 0.0f);
        cornerRadii.topRight = typedArray.getDimension(R.styleable.NextView_android_topRightRadius, 0.0f);
        cornerRadii.bottomLeft = typedArray.getDimension(R.styleable.NextView_android_bottomLeftRadius, 0.0f);
        cornerRadii.bottomRight = typedArray.getDimension(R.styleable.NextView_android_bottomRightRadius, 0.0f);

        cornersHelper.setView(this);
        cornersHelper.setCorners(defaultRadius, cornerRadii);

        float strokeWidth = typedArray.getDimension(R.styleable.NextView_uiStrokeWidth, 0.0f);
        int strokeColor = typedArray.getColor(R.styleable.NextView_uiStrokeColor, 0);
        cornersHelper.setStrokeWidth(strokeWidth, strokeColor);

        float dashWidth = typedArray.getDimension(R.styleable.NextView_uiDashWidth, 0.0f);
        float dashGap = typedArray.getDimension(R.styleable.NextView_uiDashGap, 0.0f);
        cornersHelper.setDashPathEffect(dashWidth, dashGap);

        int fillColor = typedArray.getColor(R.styleable.NextView_uiFillColor, 0);
        cornersHelper.setFillColor(fillColor);

        if (typedArray.hasValue(R.styleable.NextView_willNotDraw)) {
            setWillNotDraw(typedArray.getBoolean(R.styleable.NextView_willNotDraw, false));
        }

        typedArray.recycle();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        cornersHelper.startClipping(canvas);
        cornersHelper.drawFillColor(canvas);
        super.draw(canvas);
        cornersHelper.drawStrokeWidth(canvas);
        cornersHelper.endDrawing(canvas);
    }

    public CornersF getCornerRadius() {
        return cornerRadii;
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        cornersHelper.onLayoutChanged(changed, left, top, right, bottom);
    }

    public void setCorners(@NonNull CornersF corners) {
        this.cornerRadii.set(corners);
        cornersHelper.setCorners(corners);
        invalidate();
    }

    public void setDashPathEffect(float dashWidth, float dashGap) {
        cornersHelper.setDashPathEffect(dashWidth, dashGap);
        invalidate();
    }

    public void setFillColor(@ColorInt int color) {
        cornersHelper.setFillColor(color);
        invalidate();
    }

    public void setStrokeWidth(float width, @ColorInt int color) {
        cornersHelper.setStrokeWidth(width, color);
        invalidate();
    }
}
