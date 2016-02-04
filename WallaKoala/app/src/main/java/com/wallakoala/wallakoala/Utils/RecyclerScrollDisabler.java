package com.wallakoala.wallakoala.Utils;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

/**
 * Created by Daniel Mancebo Aldea on 04/02/2016.
 */

public class RecyclerScrollDisabler implements RecyclerView.OnItemTouchListener
{
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) { return true; }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}
}
