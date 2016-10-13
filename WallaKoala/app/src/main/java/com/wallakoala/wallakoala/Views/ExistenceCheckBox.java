package com.wallakoala.wallakoala.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.wallakoala.wallakoala.Properties.Properties;
import com.wallakoala.wallakoala.R;

/**
 * CheckBox con la fuente actualizada.
 * Created by Dani on 13/10/2016.
 */
public class ExistenceCheckBox extends AppCompatCheckBox
{
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

    public ExistenceCheckBox(Context context)
    {
        super(context);
    }

    private void setCustomFont(Context ctx, AttributeSet attrs)
    {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.ExistenceCheckBox);
        String customFont = a.getString(R.styleable.ExistenceCheckBox_customFontCh);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(ctx.getAssets(), "fonts/" + asset);
        } catch (Exception e) {
            Log.e(Properties.TAG, "Could not get typeface: "+e.getMessage());
            return false;
        }

        setTypeface(tf);
        return true;
    }
}
