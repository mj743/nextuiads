package com.nextappsdigital.nextuiads.ui;


import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextappsdigital.nextuiads.R;


public class UIShimmerLayout extends FrameLayout {
    private boolean isAnimationReversed;
    private boolean isShimmering;
    private int shimmerAngle;
    private int shimmerAnimationDuration;
    private int shimmerColor;
    private float maskWidth;
    private float gradientCenterColorWidth;
    private ViewTreeObserver.OnPreDrawListener preDrawListener;
    private int shimmerOffset;
    private Rect shimmerMaskRect;
    private Paint shimmerPaint;
    private ValueAnimator shimmerAnimator;
    private Bitmap shimmerMaskBitmap;
    private Bitmap shimmerBitmap;
    private Canvas shimmerCanvas;

    public UIShimmerLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initializeShimmer(context, attrs);
    }

    public UIShimmerLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeShimmer(context, attrs);
    }

    private void initializeShimmer(Context context, AttributeSet attrs) {
        if (attrs != null) {
            setWillNotDraw(false);
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NextView, 0, 0);
            shimmerAngle = typedArray.getInteger(R.styleable.NextView_uiShimmerAngle, 20);
            shimmerAnimationDuration = typedArray.getInteger(R.styleable.NextView_uiShimmerAnimationDuration, 1500);
            shimmerColor = typedArray.getColor(R.styleable.NextView_uiShimmerColor, context.getColor(R.color.default_shimmer_color));
            isAnimationReversed = typedArray.getBoolean(R.styleable.NextView_uiAnimationReversed, false);
            maskWidth = typedArray.getFloat(R.styleable.NextView_uiMaskWidth, 0.5f);
            gradientCenterColorWidth = typedArray.getFloat(R.styleable.NextView_uiGradientCenterColorWidth, 0.1f);
            setMaskWidth(maskWidth);
            setGradientCenterColorWidth(gradientCenterColorWidth);
            setShimmerAngle(shimmerAngle);
            if (typedArray.getBoolean(R.styleable.NextView_uiShimmer_auto_start, false) && getVisibility() == View.VISIBLE) {
                startShimmerAnimation();
            }
            typedArray.recycle();
        }
    }

    private class ShimmerPreDrawListener implements ViewTreeObserver.OnPreDrawListener {
        @Override
        public boolean onPreDraw() {
            getViewTreeObserver().removeOnPreDrawListener(this);
            startShimmerAnimation();
            return true;
        }
    }

    private class ShimmerAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final int initialOffset;
        private final int maskWidth;

        ShimmerAnimatorUpdateListener(int initialOffset, int maskWidth) {
            this.initialOffset = initialOffset;
            this.maskWidth = maskWidth;
        }

        @Override
        public void onAnimationUpdate(@NonNull ValueAnimator valueAnimator) {
            shimmerOffset = initialOffset + (Integer) valueAnimator.getAnimatedValue();
            if (shimmerOffset + maskWidth >= 0) {
                invalidate();
            }
        }
    }

    private Rect createShimmerMaskRect() {
        return new Rect(0, 0, calculateShimmerMaskWidth(), getHeight());
    }

    private int calculateShimmerMaskWidth() {
        return (int) ((((((double) getWidth()) / 2.0d) * ((double) maskWidth)) / Math.cos(Math.toRadians(Math.abs(shimmerAngle)))) + (((double) getHeight()) * Math.tan(Math.toRadians(Math.abs(shimmerAngle)))));
    }

    private Bitmap createBitmapForMask(int width, int height) {
        try {
            return Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        } catch (OutOfMemoryError e) {
            return null;
        }
    }

    private void initializeShimmerPaint() {
        if (shimmerPaint == null) {
            int transparentColor = getTransparentShimmerColor(shimmerColor);
            float maskWidthAdjusted = (((float) getWidth()) / 2.0f) * maskWidth;
            float height = shimmerAngle >= 0 ? (float) getHeight() : 0.0f;
            int shimmerColorValue = shimmerColor;
            LinearGradient linearGradient = new LinearGradient(0.0f, height, ((float) Math.cos(Math.toRadians(shimmerAngle))) * maskWidthAdjusted, height + (((float) Math.sin(Math.toRadians(shimmerAngle))) * maskWidthAdjusted), new int[]{transparentColor, shimmerColorValue, shimmerColorValue, transparentColor}, getGradientColorDistribution(), Shader.TileMode.CLAMP);
            Bitmap bitmap = shimmerMaskBitmap;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            ComposeShader composeShader = new ComposeShader(linearGradient, new BitmapShader(bitmap, tileMode, tileMode), PorterDuff.Mode.DST_IN);
            shimmerPaint = new Paint();
            shimmerPaint.setAntiAlias(true);
            shimmerPaint.setDither(true);
            shimmerPaint.setFilterBitmap(true);
            shimmerPaint.setShader(composeShader);
        }
    }

    private void dispatchShimmerDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Bitmap maskBitmap = getMaskBitmap();
        shimmerMaskBitmap = maskBitmap;
        if (maskBitmap != null) {
            if (shimmerCanvas == null) {
                shimmerCanvas = new Canvas(shimmerMaskBitmap);
            }
            shimmerCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
            shimmerCanvas.save();
            shimmerCanvas.translate((float) (-shimmerOffset), 0.0f);
            super.dispatchDraw(shimmerCanvas);
            shimmerCanvas.restore();
            drawShimmerEffect(canvas);
            shimmerMaskBitmap = null;
        }
    }

    private int getTransparentShimmerColor(int color) {
        return Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));
    }

    private void drawShimmerEffect(Canvas canvas) {
        initializeShimmerPaint();
        canvas.save();
        canvas.translate((float) shimmerOffset, 0.0f);
        Rect rect = shimmerMaskRect;
        canvas.drawRect((float) rect.left, 0.0f, (float) rect.width(), (float) shimmerMaskRect.height(), shimmerPaint);
        canvas.restore();
    }

    private void clearShimmerMaskResources() {
        shimmerCanvas = null;
        Bitmap bitmap = shimmerBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            shimmerBitmap = null;
        }
    }

    private void resetShimmerEffect() {
        if (isShimmering) {
            stopShimmerAnimation();
            startShimmerAnimation();
        }
    }

    private void stopShimmerAnimation() {
        if (shimmerAnimator != null) {
            shimmerAnimator.end();
            shimmerAnimator.removeAllUpdateListeners();
        }
        shimmerAnimator = null;
        shimmerPaint = null;
        isShimmering = false;
        clearShimmerMaskResources();
    }

    public void startShimmerAnimation() {
        if (!isShimmering) {
            if (getWidth() == 0) {
                preDrawListener = new ShimmerPreDrawListener();
                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
                return;
            }
            getShimmerAnimation().start();
            isShimmering = true;
        }
    }

    public void stopAndCleanupShimmer() {
        if (preDrawListener != null) {
            getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
        }
        stopShimmerAnimation();
    }

    public void setAnimationReversed(boolean reversed) {
        isAnimationReversed = reversed;
        resetShimmerEffect();
    }

    public void setGradientCenterColorWidth(float width) {
        if (width <= 0.0f || 1.0f <= width) {
            throw new IllegalArgumentException(String.format("gradientCenterColorWidth value must be higher than %d and less than %d", 0, 1));
        }
        gradientCenterColorWidth = width;
        resetShimmerEffect();
    }

    public void setMaskWidth(float width) {
        if (width <= 0.0f || 1.0f < width) {
            throw new IllegalArgumentException(String.format("maskWidth value must be higher than %d and less or equal to %d", 0, 1));
        }
        maskWidth = width;
        resetShimmerEffect();
    }

    public void setShimmerAngle(int angle) {
        if (angle < -45 || 45 < angle) {
            throw new IllegalArgumentException(String.format("shimmerAngle value must be between %d and %d", -45, 45));
        }
        shimmerAngle = angle;
        resetShimmerEffect();
    }

    public void setShimmerAnimationDuration(int duration) {
        shimmerAnimationDuration = duration;
        resetShimmerEffect();
    }

    public void setShimmerColor(int color) {
        shimmerColor = color;
        resetShimmerEffect();
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        if (!isShimmering || getWidth() <= 0 || getHeight() <= 0) {
            super.dispatchDraw(canvas);
        } else {
            dispatchShimmerDraw(canvas);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        stopShimmerAnimation();
        super.onDetachedFromWindow();
    }

    private Bitmap getMaskBitmap() {
        if (shimmerBitmap == null) {
            shimmerBitmap = createBitmapForMask(shimmerMaskRect.width(), getHeight());
        }
        return shimmerBitmap;
    }

    private Animator getShimmerAnimation() {
        if (shimmerAnimator != null) {
            return shimmerAnimator;
        }

        if (shimmerMaskRect == null) {
            shimmerMaskRect = createShimmerMaskRect();
        }

        int width = getWidth();
        int initialOffset = width > shimmerMaskRect.width() ? -width : -shimmerMaskRect.width();
        int maskWidth = shimmerMaskRect.width();
        int finalOffset = width - initialOffset;

        ValueAnimator animator = ValueAnimator.ofInt(isAnimationReversed ? finalOffset : 0, isAnimationReversed ? 0 : finalOffset);
        shimmerAnimator = animator;
        animator.setDuration(shimmerAnimationDuration);
        shimmerAnimator.setRepeatCount(ValueAnimator.INFINITE);
        shimmerAnimator.addUpdateListener(new ShimmerAnimatorUpdateListener(initialOffset, maskWidth));

        return shimmerAnimator;
    }

    private float[] getGradientColorDistribution() {
        float[] distribution = new float[4];
        distribution[0] = 0.0f;
        distribution[3] = 1.0f;
        float centerWidth = gradientCenterColorWidth;
        distribution[1] = 0.5f - (centerWidth / 2.0f);
        distribution[2] = (centerWidth / 2.0f) + 0.5f;
        return distribution;
    }
}

