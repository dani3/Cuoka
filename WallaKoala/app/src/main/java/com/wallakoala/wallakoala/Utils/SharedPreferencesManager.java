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
    private static final String KEY_AGE = "age";
    private static final String KEY_POSTAL_CODE = "postal_code";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

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
}
