package es.sidelab.cuokawebscraperrestserver.properties;

/**
 * Clase que proporciona todas las constantes necesarias
 * @author Daniel Mancebo Aldea
 */

public class Properties 
{        
    /* Version */
    public static final String VERSION = "1.1.1";
    
    /* Posibles respuestas del servidor */
    public static final String ALREADY_EXISTS    = "USER_ALREADY_EXISTS";
    public static final String INCORRECT_LOGIN   = "USER_INCORRECT_LOGIN";
    public static final String USER_NOT_FOUND    = "USER_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String INCORRECT_ACTION  = "INCORRECT_ACTION";
    public static final String ACCEPTED          = "ACCEPTED";
    public static final String NEW_NOTIFICATIONS = "NEW_NOTIFICATIONS";
    public static final String NO_NOTIFICATIONS  = "NO_NOTIFICATIONS";
    
    /* Paths */
    public static final String PRODUCTS               = "/images/products/";
    public static final String PREDEFINED_COLORS      = "/images/colors/";
    public static final String IMAGE_PATH             = "/var/www/html/images/products/";
    public static final String COLOR_PATH             = "/var/www/html/images/products/";
    public static final String PREDEFINED_COLORS_PATH = "/var/www/html/images/colors/";    
    public static final String MAIL_PATH              = "/root/cuoka_01/mail/";
    public static final String SCRIPTS_PATH           = "/root/cuoka_01/scripts/";
    public static final String PROPERTIES_PATH        = "/root/cuoka_01/properties/";
    public static final String PYTHON_PATH            = "/usr/bin/python";
    
    public static final String SECTIONS_FILE                = "secciones.txt";
    public static final String COLORS_FILE                  = "colores.txt";
    public static final String SUGGESTED_MALE_COLORS_FILE   = "sugerencias_colores_male.txt";
    public static final String SUGGESTED_FEMALE_COLORS_FILE = "sugerencias_colores_female.txt";
    public static final String SUGGESTED_SECTIONS_FILE      = "sugerencias_secciones.txt";
    public static final String SECTIONS_MALE_FILE           = "secciones_male.txt";
    
    /* Files */
    public static final String WELCOME_EMAIL_NAME      = "bienvenida.html";
    public static final String REMEMBER_PWD_EMAIL_NAME = "remember_password.html";
    
    /* Tamaños de las imagenes en HQ y SQ */
    public static final int IMAGE_HEIGHT_S = 400;  
    public static final int IMAGE_HEIGHT_L = 850;
    
    /* Tamaño de los iconos */
    public static final int ICON_WIDTH  = 50;
    public static final int ICON_HEIGHT = 50;
    
    /* Aspect ratios de las imagenes de cada tienda */
    public static final float PDH_ASPECT_RATIO          = 1.5f;
    public static final float SPRINGFIELD_ASPECT_RATIO  = 1.12f;
    public static final float HYM_ASPECT_RATIO          = 1.5f;
    public static final float ZARA_ASPECT_RATIO         = 1.24f;
    public static final float BERSHKA_ASPECT_RATIO      = 1.28f;
    public static final float MASSIMO_DUTTI_ASPECT_RATIO = 1.33f;
    
    /* Parametros para las busquedas */
    public static final double MAX_SIMILARITY_THRESHOLD    = 0.98f;
    public static final double MEDIUM_SIMILARITY_THRESHOLD = 0.925f;
    
    /* Numero maximo de sugerencias */
    public static final short MAX_SUGGESTIONS = 4;
    
    /* Numero de dias que una notificacion puede estar activa */
    public static final int NOTIFICATION_LIFESPAN = 14;
    
    /* Numero maximo de productos que se devolveran en los filtros */
    public static final int MAX_FILTERED_PRODUCTS = 5000;
    
    /* Lista de acciones posibles sobre un producto */
    public static final short ACTION_VIEWED        = 0;
    public static final short ACTION_FAVORITE      = 1;
    public static final short ACTION_VISITED       = 2;
    public static final short ACTION_ADDED_TO_CART = 3;
    
    /* Lista de tipos de notificaciones */
    public static final short NEW_SHOP_NOTIFICATION      = 0;
    public static final short SALES_NOTIFICATION         = 1;
    public static final short SHOP_DISCOUNT_NOTIFICATION = 2;
    public static final short UPDATE_NOTIFICATION        = 3;
    public static final short RECOMMENDED_NOTIFICATION   = 4;
    
    /* Configuracion email */
    public static final String WELCOME_EMAIL_SUBJECT          = "CUOKA te da la bienvenida a la nueva red de moda";
    public static final String WELCOME_EMAIL_FROM             = "bienvenido@cuoka.es";
    public static final String RECOVER_PASSWORD_EMAIL_SUBJECT = "Tu contraseña de CUOKA";
    public static final String FEEDBACK_EMAIL_SUBJECT         = "Nueva valoración de ?1 estrallas";
    public static final String FEEDBACK_EMAIL_FROM            = "elena.fernandez.guzman@cuoka.es";
    public static final String SHOP_SUGGESTION_EMAIL_SUBJECT  = "Nueva tienda sugerida";
    public static final String SHOP_SUGGESTION_EMAIL_FROM     = "elena.fernandez.guzman@cuoka.es";
    public static final String SCRAPING_STATS_EMAIL_SUBJECT   = "[SCRAPER_STATS] ?1";
    public static final String SCRAPING_STATS_EMAIL_FROM      = "soporte.cuoka@gmail.com";
}
