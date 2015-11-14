package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @class Vista que muestra la imagen del producto en el grid manteniendo su aspect ratio
 * Created by Daniel Mancebo Aldea on 11/11/2015.
 */

public class ImageGrid extends ImageView
{
    public ImageGrid( Context context )
    {
        super( context );
    }

    public ImageGrid( Context context, AttributeSet attrs )
    {
        super( context, attrs );
    }

    public ImageGrid( Context context, AttributeSet attrs, int defStyle )
    {
        super( context, attrs, defStyle );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
        setMeasuredDimension( getMeasuredWidth(), (int)(getMeasuredWidth() * 1.42f) );
    }
}
