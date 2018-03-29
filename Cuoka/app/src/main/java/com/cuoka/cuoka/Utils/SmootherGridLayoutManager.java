package com.cuoka.cuoka.Utils;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * LayoutManager que realiza un scroll mas suave.
 * Created by Daniel Mancebo Aldea on 03/12/2016.
 */

public class SmootherGridLayoutManager extends GridLayoutManager
{
    private static final float MILLISECONDS_PER_INCH = 100f;
    private Context mContext;

    public SmootherGridLayoutManager(Context context, int spanCount)
    {
        super(context, spanCount);

        mContext = context;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position)
    {
        // Create your RecyclerView.SmoothScroller instance? Check.
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(mContext)
        {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition)
            {
                return SmootherGridLayoutManager.this.computeScrollVectorForPosition(targetPosition);
            }

            // This returns the milliseconds it takes to
            // scroll one pixel.
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics)
            {
                return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
            }
        };

        // Docs do not tell us anything about this,
        // but we need to set the position we want to scroll to.
        smoothScroller.setTargetPosition(position);

        // Call startSmoothScroll(SmoothScroller)? Check.
        startSmoothScroll(smoothScroller);
    }
}
