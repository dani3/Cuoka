package com.wallakoala.wallakoala.Decorators;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @class Decorator para añadir los margenes a los items del grid de productos
 * Created by Daniel Mancebo Aldea on 14/11/2015.
 */

public class ProductDecorator extends RecyclerView.ItemDecoration
{
    private int verticalSpacing;
    private int horizontalSpacing;

    public ProductDecorator( int verticalSpacing, int horizontalSpacing )
    {
        this.horizontalSpacing = horizontalSpacing;
        this.verticalSpacing = verticalSpacing;
    }

    @Override
    public void getItemOffsets( Rect outRect
                        , View view
                        , RecyclerView parent
                        , RecyclerView.State state )
    {
        outRect.bottom = verticalSpacing;
        outRect.left = horizontalSpacing;
        outRect.right = horizontalSpacing;

        if ( parent.getChildLayoutPosition( view ) > 1 )
            outRect.top = verticalSpacing;
    }
}
