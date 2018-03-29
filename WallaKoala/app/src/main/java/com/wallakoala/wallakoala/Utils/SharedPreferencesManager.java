package com.wallakoala.wallakoala.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wallakoala.wallakoala.Beans.User;

/**
 * Clase que gestionara el fichero de preferencias.
 * Created by Daniel Mancebo Aldea on 11/01/2016.
 */

public class SharedPreferencesManager
{
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER      = "user_activity";
    private static final String KEY_FIRST_USE = "first_use";

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    public SharedPreferencesManager(Context context)
    {
        mSharedPreferences = context.getSharedPreferences("cuoka_preferences", Context.MODE_PRIVATE);
    }

    /**
     * Metodo que elimina las preferencias.
     * @return true si se han borrado correctamentes
     */
    public boolean clear()
    {
        return mEditor.clear().commit();
    }

    /**
     * Metodo que inserta si el usuario esta logeado.
     * @param loggedIn: true si el usuario esta logeado
     * @return true si se ha insertado correctamente.
     */
    public synchronized boolean insertLoggedIn(final boolean loggedIn)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(KEY_LOGGED_IN, loggedIn);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve si el usuario esta logeado.
     * @return true si el usuario esta logeado.
     */
    public boolean retreiveLoggedIn()
    {
        return mSharedPreferences.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Metodo que inserta el usuario, hay que convertirlo a JSON.
     * @param user: actividad del usuario.
     * @return true si se ha insertado correctamente.
     */
    public synchronized boolean insertUser(final User user)
    {
        mEditor = mSharedPreferences.edit();

        final Gson gson = new Gson();
        final String json = gson.toJson(user);

        mEditor.putString(KEY_USER, json);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve un usuario.
     * @return usuario.
     */
    public User retrieveUser()
    {
        final Gson gson = new Gson();
        final String json = mSharedPreferences.getString(KEY_USER, null);

        return gson.fromJson(json, User.class);
    }
}
