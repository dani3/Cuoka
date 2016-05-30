package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

/**
 * An extension of RecyclerView, focused more on resembling a GridView.
 * Unlike {@link android.support.v7.widget.RecyclerView}, this view can handle
 * {@code <grid_layout_animation>} as long as you provide it a
 * {@link android.support.v7.widget.StaggeredGridLayoutManager} in
 * {@code setLayoutManager(LayoutManager layout)}.
 * Created by Freddie (Musenkishi) Lust-Hed.
 */

public class GridRecyclerView extends RecyclerView
{
    public GridRecyclerView(Context context)
    {
        super(context);
    }

    public GridRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public GridRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout)
    {
        if (layout instanceof StaggeredGridLayoutManager)
            super.setLayoutManager(layout);
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, @NonNull ViewGroup.LayoutParams params, int index, int count)
    {
        if (getAdapter() != null && getLayoutManager() instanceof StaggeredGridLayoutManager)
        {
            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null)
            {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns = ((StaggeredGridLayoutManager) getLayoutManager()).getSpanCount();

            animationParams.count = count;
            animationParams.index = index;
            animationParams.columnsCount = columns;
            animationParams.rowsCount = count / columns;

            final int invertedIndex = count - 1 - index;
            animationParams.column = columns - 1 - (invertedIndex % columns);
            animationParams.row = animationParams.rowsCount - 1 - invertedIndex / columns;

        } else {
            super.attachLayoutAnimationParameters(child, params, index, count);

        }
    }
}
