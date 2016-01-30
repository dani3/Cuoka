package com.wallakoala.wallakoala.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * @class Clase que gestionara el fichero de preferencias.
 * Created by Daniel Mancebo Aldea on 11/01/2016.
 */

public class SharedPreferencesManager
{
    private static final String KEY_SHOPS = "shops";
    private static final String KEY_MAN = "man";
    private static final String KEY_NEWNESS = "newness";

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private static Context mContext;

    public SharedPreferencesManager( Context context )
    {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("cuoka_preferences", Context.MODE_PRIVATE);
}

    /**
     * Metodo que inserta un conjunto de tiendas en las preferencias.
     * @param shops: conjunto de tiendas.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertShops( Set<String> shops )
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putStringSet( KEY_SHOPS, shops );

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve el conjunto de tienda.
     * @return conjunto de tiendas.
     */
    public Set<String> retreiveShops()
    {
        return mSharedPreferences.getStringSet(KEY_SHOPS, null);
    }

    /**
     * Metodo que inserta si es hombre o mujer.
     * @param man: true si es hombre.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertMan( boolean man )
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean( KEY_MAN, man );

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve si es hombre o mujer.
     * @return true si es hombre.
     */
    public boolean retreiveMan()
    {
        return mSharedPreferences.getBoolean( KEY_MAN, false );
    }

    /**
     * Metodo que inserta si el filtro de novedades.
     * @param newness: true si se quiere las novedades.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertNewness( boolean newness )
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean( KEY_NEWNESS, newness );

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve el estado del filtro de novedades.
     * @return: true si el filtro de novedades esta activo.
     */
    public boolean retreiveNewness()
    {
        return mSharedPreferences.getBoolean( KEY_NEWNESS, false );
    }

}
