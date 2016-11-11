package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;

import com.wallakoala.wallakoala.Properties.Properties;

/**
 * An extension of RecyclerView, focused more on resembling a GridView.
 * Unlike {@link android.support.v7.widget.RecyclerView}, this view can handle
 * {@code <gridLayoutAnimation>} as long as you provide it a
 * {@link android.support.v7.widget.GridLayoutManager} in
 * {@code setLayoutManager(LayoutManager layout)}.
 *
 * Created by Freddie (Musenkishi) Lust-Hed.
 */
public class StaggeredRecyclerView extends RecyclerView
{
    public StaggeredRecyclerView(Context context)
    {
        super(context);
    }

    public StaggeredRecyclerView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public StaggeredRecyclerView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void setLayoutManager(LayoutManager layout)
    {
        if (layout instanceof StaggeredGridLayoutManager || layout instanceof GridLayoutManager)
        {
            super.setLayoutManager(layout);

        } else {
            throw new ClassCastException("You should only use a GridLayoutManager with GridRecyclerView.");
        }
    }

    @Override
    protected void attachLayoutAnimationParameters(View child, ViewGroup.LayoutParams params, int index, int count) {

        if (getAdapter() != null && getLayoutManager() instanceof StaggeredGridLayoutManager || getLayoutManager() instanceof GridLayoutManager)
        {
            GridLayoutAnimationController.AnimationParameters animationParams =
                    (GridLayoutAnimationController.AnimationParameters) params.layoutAnimationParameters;

            if (animationParams == null)
            {
                animationParams = new GridLayoutAnimationController.AnimationParameters();
                params.layoutAnimationParameters = animationParams;
            }

            int columns;

            if (getLayoutManager() instanceof StaggeredGridLayoutManager)
            {
                columns = ((StaggeredGridLayoutManager) getLayoutManager()).getSpanCount();
            } else {
                columns = ((GridLayoutManager) getLayoutManager()).getSpanCount();
            }

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
