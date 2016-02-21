package com.wallakoala.wallakoala.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @class Clase con metodos varios.
 * Created by Daniel Mancebo Aldea on 02/02/2016.
 */

public class Utils
{
    /**
     * Metodo que codifica una URL.
     * @param url: URL a codificar.
     * @return URL codificada.
     */
    public static String fixUrl(String url)
    {
        return url.replaceAll(" ", "%20");
    }

    /**
     * Metodo que da formato al precio.
     * @param price: double con el precio.
     * @return SpannableString con el precio formateado.
     */
    public static SpannableString priceToString(double price)
    {
        String sPrice = (String.format("%.2f", price) + "€").replaceAll(",00", "");
        SpannableString sS = new SpannableString(sPrice);

        if (sPrice.contains(","))
            sS.setSpan(new RelativeSizeSpan(0.65f)
                    , sPrice.indexOf(",")
                    , sPrice.indexOf("€") + 1
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        else
            sS.setSpan(new RelativeSizeSpan(0.65f)
                    , sPrice.indexOf("€")
                    , sPrice.indexOf("€") + 1
                    , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sS;
    }

    @Nullable
    public static String saveImage(Context context, Bitmap bitmap, int pos, String TAG)
    {
        String fileName = "thumbnail_" + pos + ".png";

        try
        {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            FileOutputStream fileOutStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutStream.write(byteArray);

            fileOutStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
            Log.e(TAG, "Error guardando la imagen");

            return null;
        }

        return fileName;
    }
}
