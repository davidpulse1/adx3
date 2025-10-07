package com.adx.integration.ui.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Custom FloatingActionButton that displays a bubble reminder with ad count
 * Features animated badge and pulsing animation when new ads are available
 */
public class BubbleReminderView extends FloatingActionButton {

    private Paint badgePaint;
    private Paint badgeTextPaint;
    private Paint pulsePaint;
    
    private int badgeCount = 0;
    private boolean showBadge = false;
    private boolean isPulsing = false;
    
    private float pulseRadius = 0f;
    private float pulseAlpha = 255f;
    private ValueAnimator pulseAnimator;
    
    private RectF badgeRect;
    private String badgeText = "";
    private float badgeTextWidth;
    private float badgeTextHeight;

    public BubbleReminderView(Context context) {
        super(context);
        init();
    }

    public BubbleReminderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BubbleReminderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // Initialize badge paint
        badgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgePaint.setColor(Color.RED);
        badgePaint.setStyle(Paint.Style.FILL);
        
        // Initialize badge text paint
        badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        badgeTextPaint.setColor(Color.WHITE);
        badgeTextPaint.setTextSize(getResources().getDisplayMetrics().density * 12);
        badgeTextPaint.setTextAlign(Paint.Align.CENTER);
        
        // Initialize pulse paint
        pulsePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pulsePaint.setColor(getBackgroundTintList().getDefaultColor());
        pulsePaint.setStyle(Paint.Style.STROKE);
        pulsePaint.setStrokeWidth(getResources().getDisplayMetrics().density * 3);
        
        badgeRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        // Draw pulse animation
        if (isPulsing && pulseRadius > 0) {
            pulsePaint.setAlpha((int) pulseAlpha);
            canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, pulseRadius, pulsePaint);
        }
        
        // Draw badge
        if (showBadge && badgeCount > 0) {
            float centerX = getWidth() * 0.85f;
            float centerY = getHeight() * 0.15f;
            float radius = Math.max(20, Math.min(40, getWidth() * 0.25f));
            
            // Draw badge circle
            badgeRect.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawOval(badgeRect, badgePaint);
            
            // Draw badge text
            float textY = centerY + (badgeTextHeight / 2);
            canvas.drawText(badgeText, centerX, textY, badgeTextPaint);
        }
    }

    /**
     * Set the badge count and visibility
     */
    public void setBadgeCount(int count) {
        this.badgeCount = count;
        this.showBadge = count > 0;
        this.badgeText = String.valueOf(Math.min(count, 99)); // Cap at 99
        if (count > 99) {
            this.badgeText = "99+";
        }
        
        // Measure text
        if (badgeText.length() > 0) {
            badgeTextWidth = badgeTextPaint.measureText(badgeText);
            Paint.FontMetrics fm = badgeTextPaint.getFontMetrics();
            badgeTextHeight = fm.descent - fm.ascent;
        }
        
        invalidate();
        
        // Start pulse animation if count increased
        if (count > 0) {
            startPulseAnimation();
        }
    }

    /**
     * Get current badge count
     */
    public int getBadgeCount() {
        return badgeCount;
    }

    /**
     * Start pulse animation
     */
    public void startPulseAnimation() {
        if (pulseAnimator != null && pulseAnimator.isRunning()) {
            return;
        }
        
        isPulsing = true;
        
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f);
        pulseAnimator.setDuration(1500);
        pulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ValueAnimator.RESTART);
        pulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        pulseAnimator.addUpdateListener(animation -> {
            float progress = (float) animation.getAnimatedValue();
            
            // Expand radius
            pulseRadius = getWidth() * 0.5f * progress;
            
            // Fade out
            pulseAlpha = 255 * (1 - progress);
            
            invalidate();
        });
        
        pulseAnimator.start();
    }

    /**
     * Stop pulse animation
     */
    public void stopPulseAnimation() {
        isPulsing = false;
        
        if (pulseAnimator != null) {
            pulseAnimator.cancel();
            pulseAnimator = null;
        }
        
        pulseRadius = 0f;
        pulseAlpha = 255f;
        invalidate();
    }

    /**
     * Animate badge appearance
     */
    public void animateBadgeAppearance() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            setScaleX(scale);
            setScaleY(scale);
        });
        
        animator.start();
    }

    /**
     * Animate badge disappearance
     */
    public void animateBadgeDisappearance() {
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(300);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        
        animator.addUpdateListener(animation -> {
            float scale = (float) animation.getAnimatedValue();
            setScaleX(scale);
            setScaleY(scale);
        });
        
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setBadgeCount(0);
            }
        });
        
        animator.start();
    }

    /**
     * Reset badge with animation
     */
    public void resetBadge() {
        animateBadgeDisappearance();
        stopPulseAnimation();
    }

    /**
     * Check if badge is visible
     */
    public boolean isBadgeVisible() {
        return showBadge;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        // Ensure minimum size for badge
        int minSize = (int) (getResources().getDisplayMetrics().density * 56);
        int width = Math.max(getMeasuredWidth(), minSize);
        int height = Math.max(getMeasuredHeight(), minSize);
        
        setMeasuredDimension(width, height);
    }
}