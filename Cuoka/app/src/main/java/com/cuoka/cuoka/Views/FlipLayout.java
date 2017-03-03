package com.cuoka.cuoka.Views;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class FlipLayout extends FrameLayout
{
    public static final int ANIM_DURATION_MILLIS = 500;
    private static final Interpolator fDefaultInterpolator = new OvershootInterpolator();
    private FlipAnimator animator;
    private boolean isFlipped;
    private Direction direction;
    private View frontView, backView;

    public FlipLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    public FlipLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public FlipLayout(Context context)
    {
        super(context);
        init();
    }

    private void init()
    {
        animator = new FlipAnimator();
        animator.setInterpolator(fDefaultInterpolator);
        animator.setDuration(ANIM_DURATION_MILLIS);
        direction = Direction.DOWN;
        setSoundEffectsEnabled(true);
    }

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();

        if (getChildCount() > 2)
        {
            throw new IllegalStateException("FlipLayout can host only two direct children");
        }

        frontView = getChildAt(0);
        backView = getChildAt(1);
        reset();
    }

    private void toggleView()
    {
        if (frontView == null || backView == null)
        {
            return;
        }

        if (!isFlipped)
        {
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.GONE);
        } else {
            frontView.setVisibility(View.GONE);
            backView.setVisibility(View.VISIBLE);
        }

        isFlipped = !isFlipped;
    }

    public void reset()
    {
        isFlipped = false;
        direction = Direction.DOWN;
        frontView.setVisibility(View.VISIBLE);
        backView.setVisibility(View.GONE);
    }

    public void toggleDown()
    {
        direction = Direction.DOWN;
        startAnimation();
    }

    public void startAnimation()
    {
        animator.setVisibilitySwapped();
        startAnimation(animator);
    }

    public void flip()
    {
        toggleDown();
    }

    public void setFlipped(boolean flipped)
    {
        if (flipped)
        {
            frontView.setVisibility(View.VISIBLE);
            backView.setVisibility(View.GONE);
        } else {
            frontView.setVisibility(View.GONE);
            backView.setVisibility(View.VISIBLE);
        }

        isFlipped = flipped;
    }

    private enum Direction
    {
        UP, DOWN
    }

    public class FlipAnimator extends Animation
    {
        private Camera camera;
        private float centerX;
        private float centerY;
        private boolean visibilitySwapped;

        public FlipAnimator() {
            setFillAfter(true);
        }

        public void setVisibilitySwapped() {
            visibilitySwapped = false;
        }

        @Override public void initialize(int width, int height, int parentWidth, int parentHeight)
        {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
            this.centerX = width / 2;
            this.centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t)
        {
            // Angle around the y-axis of the rotation at the given time. It is
            // calculated both in radians and in the equivalent degrees.
            final double radians = Math.PI * interpolatedTime;

            float degrees = (float) (180.0 * radians / Math.PI);

            if (direction == Direction.UP)
            {
                degrees = -degrees;
            }

            // Once we reach the midpoint in the animation, we need to hide the
            // source view and show the destination view. We also need to change
            // the angle by 180 degrees so that the destination does not come in
            // flipped around. This is the main problem with SDK sample, it does
            // not do this.
            if (interpolatedTime >= 0.5f)
            {
                if (direction == Direction.UP)
                {
                    degrees += 180.f;
                }

                if (direction == Direction.DOWN)
                {
                    degrees -= 180.f;
                }

                if (!visibilitySwapped)
                {
                    toggleView();
                    visibilitySwapped = true;
                }
            }

            final Matrix matrix = t.getMatrix();

            camera.save();
            camera.rotateX(degrees);
            camera.rotateY(0);
            camera.rotateZ(0);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
}
