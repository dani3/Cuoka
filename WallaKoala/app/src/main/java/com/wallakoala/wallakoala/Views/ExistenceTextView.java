package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;

/**
 * TextView con la fuente actualizada.
 * Created by Daniel Mancebo Aldea on 13/10/2016.
 */
public class ExistenceTextView extends TextView
{
    public ExistenceTextView(Context context)
    {
        super(context);
    }

    public ExistenceTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ExistenceTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ExistenceTextView);
        String customFont = a.getString(R.styleable.ExistenceTextView_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset)
    {
        setTypeface(TypeFaceSingleton.getTypeFace(ctx, asset));

        return true;
    }
}
