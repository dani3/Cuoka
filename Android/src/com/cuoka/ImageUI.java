package com.cuoka;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageUI extends ImageView
{
    public ImageUI( Context context )
    {
        super( context );
    }

    public ImageUI( Context context, AttributeSet attrs )
    {
        super( context, attrs );
    }

    public ImageUI( Context context, AttributeSet attrs, int defStyle )
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
