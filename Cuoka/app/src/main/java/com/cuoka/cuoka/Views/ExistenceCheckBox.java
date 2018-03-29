package com.cuoka.cuoka.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.cuoka.cuoka.R;
import com.cuoka.cuoka.Singletons.TypeFaceSingleton;

/**
 * CheckBox con la fuente personalizada.
 * Created by Daniel Mancebo Aldea on 13/10/2016.
 */
public class ExistenceCheckBox extends AppCompatCheckBox
{
    public ExistenceCheckBox(Context context)
    {
        super(context);
    }

    public ExistenceCheckBox(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setCustomFont(context, attrs);
    }

    public ExistenceCheckBox(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        setCustomFont(context, attrs);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ExistenceCheckBox);
        String customFont = a.getString(R.styleable.ExistenceCheckBox_customFontCh);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset)
    {
        setTypeface(TypeFaceSingleton.getTypeFace(ctx, asset));

        return true;
    }
}
