package com.wallakoala.wallakoala.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;

import com.wallakoala.wallakoala.Properties.Properties;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Clase con metodos varios.
 * Created by Daniel Mancebo Aldea on 02/02/2016.
 */

public class Utils
{
    /**
     * Metodo que codifica una URL.
     * @param url: URL a codificar.
     * @return URL codificada.
     */
    public static String fixUrl(final String url)
    {
        return url.replaceAll(" ", "%20");
    }

    /**
     * Metodo que da formato al precio.
     * @param price: double con el precio.
     * @return SpannableString con el precio formateado.
     */
    public static SpannableString priceToString(final double price)
    {
        final String sPrice = (String.format("%.2f", price) + "€").replaceAll(",00", "");
        final SpannableString sS = new SpannableString(sPrice);

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

    /**
     * Metodo que comprueba si un string contiene solo letras.
     * @param query: string a comprobar.
     * @return true si el string es correcto.
     */
    public static boolean isQueryOk(final String query)
    {
        final char[] chars = query.toCharArray();

        for (char c : chars)
            if (!Character.isLetter(c) && !Character.isSpaceChar(c))
                return false;

        return true;
    }

    @Nullable
    public static String saveImage(final Context context, final Bitmap bitmap, final int pos, final String TAG)
    {
        final String fileName = "thumbnail_" + pos + ".png";

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

    public static double mapValueFromRangeToRange(double value
                                    , double fromLow
                                    , double fromHigh
                                    , double toLow
                                    , double toHigh)
    {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high)
    {
        return Math.min(Math.max(value, low), high);
    }

    /**
     * Metodo que comprueba si el email es correcto.
     * @param email: email a comprobar.
     * @return true si el email es correcto.
     */
    public static boolean isValidEmail(final String email)
    {
        return (!TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    /**
     * Metodo que comprueba si la edad es correcta.
     * @param age: edad a comprobar.
     * @return true si la edad es correcta.
     */
    public static boolean isValidAge(final String age)
    {
        return (!age.isEmpty()) && (Integer.valueOf(age) >= Properties.MIN_AGE && Integer.valueOf(age) <= Properties.MAX_AGE);
    }

    /**
     * Metodo que comprueba si el CP es correcto.
     * @param postalCode: CP a comprobar.
     * @return true si el CP es correcto.
     */
    public static boolean isValidPostalCode(final String postalCode)
    {
        return (postalCode.length() == Properties.POSTAL_CODE_LENGHT);
    }

    /**
     * Metodo que comprueba si la contraseña es correcta.
     * @param password: contraseña a comprobar.
     * @return true si la contraseña es correcta.
     */
    public static boolean isValidPassword(final String password)
    {
        return (!password.trim().isEmpty() &&
                !(password.trim().length() < Properties.MIN_PASSWORD_LENGTH) &&
                !(password.toUpperCase().contains("SELECT")) && !(password.toUpperCase().contains("DROP")) &&
                !(password.toUpperCase().contains("DELETE")) && !(password.toUpperCase().contains("UPDATE")) &&
                !(password.contains("*")) && !(password.contains("/")) && !(password.contains("\\")) &&
                !(password.contains("=")) && !(password.contains("|")) && !(password.contains("&")) &&
                !(password.contains("'")) && !(password.contains("!")) && !(password.contains(";")));
    }

    /**
     * Metodo que comprueba si el nombre es correcto.
     * @param name: nombre a comprobar.
     * @return true si el nombre es correcto.
     */
    public static boolean isValidName(final String name)
    {
        return isQueryOk(name);
    }
}
