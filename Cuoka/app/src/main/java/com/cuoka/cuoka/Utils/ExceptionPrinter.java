package com.cuoka.cuoka.Utils;

import android.util.Log;

import com.cuoka.cuoka.Properties.Properties;

import java.util.Arrays;

/**
 * Clase que muestar por pantalla los detalles de una excepcion.
 * Created by Daniel Mancebo Aldea on 11/12/2016.
 */

public class ExceptionPrinter
{
    /**
     * Metodo que muesta por pantalla los detalles de una excepcion.
     * @param tag: tag que precede a los mensajes.
     * @param exception: excepcion producida.
     */
    public static void printException(String tag, Exception exception)
    {
        Log.e(Properties.TAG, "[" + tag + "] " + exception.getMessage());
        Log.e(Properties.TAG, "[" + tag + "] " + exception.getLocalizedMessage());
        Log.e(Properties.TAG, "[" + tag + "] " + exception.getCause());
        Log.e(Properties.TAG, "[" + tag + "] " + Arrays.toString(exception.getStackTrace()));
    }
}
