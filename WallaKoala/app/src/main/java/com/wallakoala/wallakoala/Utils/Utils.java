package com.wallakoala.wallakoala.Utils;

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
}
