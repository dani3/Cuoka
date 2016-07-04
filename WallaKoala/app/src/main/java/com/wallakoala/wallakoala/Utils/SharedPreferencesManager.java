package com.wallakoala.wallakoala.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wallakoala.wallakoala.Beans.User;

/**
 * @class Clase que gestionara el fichero de preferencias.
 * Created by Daniel Mancebo Aldea on 11/01/2016.
 */

public class SharedPreferencesManager
{
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_USER_ACTIVITY = "user_activity";

    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor mEditor;

    private static Context mContext;

    public SharedPreferencesManager(Context context)
    {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences("cuoka_preferences", Context.MODE_PRIVATE);
    }

    /**
     * Metodo que inserta si el usuario esta logeado.
     * @param loggedIn: true si el usuario esta logeado
     * @return true si se ha insertado correctamente.
     */
    public boolean insertLoggedIn(boolean loggedIn)
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
    public boolean insertUser(User user)
    {
        mEditor = mSharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(user);

        mEditor.putString(KEY_USER_ACTIVITY, json);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve un usuario.
     * @return usuario.
     */
    public User retreiveUser()
    {
        Gson gson = new Gson();

        String json = mSharedPreferences.getString(KEY_USER_ACTIVITY, null);

        return gson.fromJson(json, User.class);
    }
}
