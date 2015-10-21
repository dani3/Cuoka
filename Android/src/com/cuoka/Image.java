package com.cuoka;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class Image extends ImageView
{
    public Image( Context context )
    {
        super( context );
    }

    public Image( Context context, AttributeSet attrs )
    {
        super( context, attrs );
    }

    public Image( Context context, AttributeSet attrs, int defStyle )
    {
        super( context, attrs, defStyle );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        setMeasuredDimension( getMeasuredWidth(), 600 ); 
    }
}
