package com.wallakoala.wallakoala.Properties;

import com.android.volley.toolbox.StringRequest;

/**
 * @class Clase que todos las constantes necesarias.
 * Created by Daniel Mancebo Aldea on 18/04/2016.
 */

public class Properties
{
    public static final String TAG = "CUOKA";
    public static final String PACKAGE = "com.wallakoala.wallakoala";

    public static final String SERVER_URL = "http://cuoka-ws.cloudapp.net";
    public static final String SERVER_SPRING_PORT = "8080";

    public static final String IMAGES_PATH = "/images/products/";
    public static final String PREDEFINED_ICONS_PATH = "/images/colors/";
    public static final String ICONS_PATH = "/images/products/";

    public static final String ALREADY_EXISTS  = "USER_ALREADY_EXISTS";
    public static final String REGISTRATION_OK = "USER_REGISTRATION_OK";
    public static final String LOGIN_OK        = "USER_LOGGED_IN";
    public static final String INCORRECT_LOGIN = "USER_INCORRECT_LOGIN";

    public static final int REQUEST_TIMEOUT = 30000;

    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MIN_AGE = 12;
    public static final int MAX_AGE = 110;
    public static final int POSTAL_CODE_LENGHT = 5;
}
