package com.nextappsdigital.nextuiads.utils;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;

public class UICornersHelper {

    private Paint backgroundPaint;
    private Paint borderPaint;
    private float dashGap;
    private float dashWidth;
    private boolean isClipEnabled = false;
    private boolean isRtlLayout;
    private Path cornerPath;
    private float[] cornerRadii;
    private RectF viewRect;

    public UICornersHelper() {
        // Initialize paint objects
        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setDither(true);
        backgroundPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setDither(true);
        borderPaint.setStyle(Paint.Style.STROKE);
    }

    private void initializeParameters() {
        if (cornerPath == null) {
            cornerPath = new Path();
        }
        if (cornerRadii == null) {
            cornerRadii = new float[8];
        }
        if (viewRect == null) {
            viewRect = new RectF();
        }
    }

    public void drawFillColor(Canvas canvas) {
        if (cornerPath != null && backgroundPaint != null) {
            try {
                canvas.drawPath(cornerPath, backgroundPaint);
            } catch (Throwable ignored) {
            }
        }
    }

    public void drawStrokeWidth(Canvas canvas) {
        if (cornerPath != null && borderPaint != null) {
            try {
                canvas.drawPath(cornerPath, borderPaint);
            } catch (Throwable ignored) {
            }
        }
    }

    public void endDrawing(Canvas canvas) {
        if (isClipEnabled) {
            isClipEnabled = false;
            try {
                canvas.restore();
            } catch (Throwable ignored) {
            }
        }
    }

    public void onLayoutChanged(boolean isChanged, int left, int top, int right, int bottom) {
        if (isChanged) {
            initializeParameters();
            try {
                viewRect.set(0.0f, 0.0f, (float) (right - left), (float) (bottom - top));
                cornerPath.reset();
                cornerPath.addRoundRect(viewRect, cornerRadii, Path.Direction.CW);
            } catch (Throwable ignored) {
            }
        }
    }

    public void setCorners(@NonNull CornersF radii) {
        setCorners(0.0f, radii);
    }

    public void setDashPathEffect(float dashWidth, float dashGap) {
        this.dashWidth = dashWidth;
        this.dashGap = dashGap;
        if (borderPaint == null) {
            return;
        }
        if (dashWidth <= 0.0f || dashGap <= 0.0f) {
            borderPaint.setPathEffect(null);
        } else {
            borderPaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0.0f));
        }
    }

    public void setFillColor(@ColorInt int color) {
        if (color != 0) {
            backgroundPaint.setColor(color);
        } else {
            backgroundPaint = null;
        }
    }

    public void setStrokeWidth(float strokeWidth, @ColorInt int color) {
        if (strokeWidth > 0.0f) {
            borderPaint.setColor(color);
            borderPaint.setStrokeWidth(strokeWidth * 2.0f);

            if (dashWidth > 0.0f && dashGap > 0.0f) {
                borderPaint.setPathEffect(new DashPathEffect(new float[]{dashWidth, dashGap}, 0.0f));
            }
        } else {
            borderPaint = null;
        }
    }

    public void setView(@Nullable View view) {
        if (view != null) {
            this.isRtlLayout = view.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
        }
    }

    public void startClipping(Canvas canvas) {
        if (cornerPath != null) {
            try {
                canvas.save();
                isClipEnabled = true;
                canvas.clipPath(cornerPath);
            } catch (Throwable ignored) {
            }
        }
    }

    public void setCorners(float defaultRadius, @NonNull CornersF radii) {
        initializeParameters();
        float topLeft = radii.topLeft;
        float topRight = radii.topRight;
        float bottomLeft = radii.bottomLeft;
        float bottomRight = radii.bottomRight;

        Arrays.fill(cornerRadii, 0.0f);
        if (defaultRadius > 0.0f) {
            Arrays.fill(cornerRadii, defaultRadius);
        } else {
            if (topLeft > 0.0f) {
                cornerRadii[isRtlLayout ? 2 : 0] = topLeft;
                cornerRadii[isRtlLayout ? 3 : 1] = topLeft;
            }
            if (topRight > 0.0f) {
                cornerRadii[isRtlLayout ? 0 : 2] = topRight;
                cornerRadii[isRtlLayout ? 1 : 3] = topRight;
            }
            if (bottomRight > 0.0f) {
                cornerRadii[isRtlLayout ? 6 : 4] = bottomRight;
                cornerRadii[isRtlLayout ? 7 : 5] = bottomRight;
            }
            if (bottomLeft > 0.0f) {
                cornerRadii[isRtlLayout ? 4 : 6] = bottomLeft;
                cornerRadii[isRtlLayout ? 5 : 7] = bottomLeft;
            }
        }

        if (viewRect != null && !viewRect.isEmpty()) {
            try {
                cornerPath.reset();
                cornerPath.addRoundRect(viewRect, cornerRadii, Path.Direction.CW);
            } catch (Throwable ignored) {
            }
        }
    }
}
