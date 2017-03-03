package com.cuoka.cuoka.Singletons;

import android.content.Context;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Clase que crea un unico objeto de la fuente personalizada.
 * Created by Daniel Mancebo Aldea on 14/10/2016.
 */

public class TypeFaceSingleton
{
    public static final String TYPEFACE_FOLDER = "fonts";

    private static Hashtable<String, Typeface> sTypeFaces = new Hashtable<>(1);

    public static Typeface getTypeFace(Context context, String fileName)
    {
        Typeface tempTypeface = sTypeFaces.get(fileName);

        if (tempTypeface == null)
        {
            String fontPath = TYPEFACE_FOLDER + '/' + fileName;

            tempTypeface = Typeface.createFromAsset(context.getAssets(), fontPath);
            sTypeFaces.put(fileName, tempTypeface);
        }

        return tempTypeface;
    }
}
