package com.wallakoala.wallakoala.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.wallakoala.wallakoala.Beans.UserActivity;

import java.util.Set;

/**
 * @class Clase que gestionara el fichero de preferencias.
 * Created by Daniel Mancebo Aldea on 11/01/2016.
 */

public class SharedPreferencesManager
{
    private static final String KEY_SHOPS = "shops";
    private static final String KEY_MAN = "man";
    private static final String KEY_AGE = "age";
    private static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_LOGGED_IN = "logged_in";
    private static final String KEY_OWN_REGISTER = "own_register";
    private static final String KEY_USER_ID = "user_id";
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
     * Metodo que inserta un conjunto de tiendas en las preferencias.
     * @param shops: conjunto de tiendas.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertShops(Set<String> shops)
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
    public boolean insertMan(boolean man)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(KEY_MAN, man);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve si es hombre o mujer.
     * @return true si es hombre.
     */
    public boolean retreiveMan()
    {
        return mSharedPreferences.getBoolean(KEY_MAN, false);
    }

    /**
     * Metodo que inserta la edad.
     * @param age: edad del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertAge(int age)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(KEY_AGE, age);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve la edad.
     * @return edad del usuario.
     */
    public int retreiveAge()
    {
        return mSharedPreferences.getInt(KEY_AGE, 0);
    }

    /**
     * Metodo que inserta el codigo postal.
     * @param postalCode: codigo postal del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertPostalCode(int postalCode)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putInt(KEY_POSTAL_CODE, postalCode);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve el codigo postal.
     * @return codigo postal del usuario.
     */
    public int retreivePostalCode()
    {
        return mSharedPreferences.getInt(KEY_POSTAL_CODE, 0);
    }

    /**
     * Metodo que inserta el email.
     * @param email: email del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertEmai(String email)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(KEY_EMAIL, email);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve el email.
     * @return email del usuario.
     */
    public String retreiveEmail()
    {
        return mSharedPreferences.getString(KEY_EMAIL, null);
    }

    /**
     * Metodo que inserta la contraseña.
     * @param password: contraseña del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertPassword(String password)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putString(KEY_PASSWORD, password);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve la contraseña.
     * @return contraseña del usuario.
     */
    public String retreivePassword()
    {
        return mSharedPreferences.getString(KEY_PASSWORD, null);
    }

    /**
     * Metodo que inserta si se ha registrado con el registro propio.
     * @param ownRegister: true si es registro propio.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertOwnRegister(boolean ownRegister)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean(KEY_OWN_REGISTER, ownRegister);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve si se ha registrado con el registro propio.
     * @return true si se ha registrado con el registro propio.
     */
    public boolean retreiveOwnRegister()
    {
        return mSharedPreferences.getBoolean(KEY_OWN_REGISTER, false);
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
     * Metodo que inserta el id del usuario.
     * @param id: id del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertUserId(long id)
    {
        mEditor = mSharedPreferences.edit();
        mEditor.putLong(KEY_USER_ID, id);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve el id del usuario.
     * @return id del usuario.
     */
    public long retreiveUserId()
    {
        return mSharedPreferences.getLong(KEY_USER_ID, -1);
    }

    /**
     * Metodo que inserta la actividad del usuario, hay que convertirlo a JSON.
     * @param userActivity: actividad del usuario.
     * @return true si se ha insertado correctamente.
     */
    public boolean insertUserActivity(UserActivity userActivity)
    {
        mEditor = mSharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(userActivity);

        mEditor.putString(KEY_USER_ACTIVITY, json);

        return mEditor.commit();
    }

    /**
     * Metodo que devuelve la actividad de un usuario.
     * @return actividad del usuario.
     */
    public UserActivity retreiveUserActivity()
    {
        Gson gson = new Gson();

        String json = mSharedPreferences.getString(KEY_USER_ACTIVITY, null);

        return gson.fromJson(json, UserActivity.class);
    }
}
