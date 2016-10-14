package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.wallakoala.wallakoala.R;
import com.wallakoala.wallakoala.Singletons.TypeFaceSingleton;

/**
 * EditText con la fuente actualizada.
 * Created by Daniel Mancebo Aldea on 14/10/2016.
 */
public class ExistenceEditText extends EditText
{
    public ExistenceEditText(Context context)
    {
        super(context);
    }

    public ExistenceEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ExistenceEditText(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ExistenceEditText);
        String customFont = a.getString(R.styleable.ExistenceEditText_customFontEd);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset)
    {
        setTypeface(TypeFaceSingleton.getTypeFace(ctx, asset));

        return true;
    }
}
