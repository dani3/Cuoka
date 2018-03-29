package com.cuoka.cuoka.Utils;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Util class for converting between dp, px and other magical pixel units.
 */

public class PixelUtil
{
    private PixelUtil() {}

    public static int dpToPx(Context context, int dp)
    {
        return Math.round(dp * getPixelScaleFactor(context));
    }

    private static float getPixelScaleFactor(Context context)
    {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}
