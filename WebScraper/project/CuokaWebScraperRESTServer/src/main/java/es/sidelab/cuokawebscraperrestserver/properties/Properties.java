package es.sidelab.cuokawebscraperrestserver.properties;

/**
 * @class Clase que proporciona todas las constantes necesarias
 * @author Daniel Mancebo Aldea
 */

public class Properties 
{    
    /* Parametros de cifrado */
    public static final int IV_LENGTH = 16;
    public static final String KEY = "10485mvJdb3/8wmc";    
    public static final String HASH_ALGORITHM = "MD5";
    
    /* Posibles respuestas del servidor */
    public static final String ALREADY_EXISTS    = "USER_ALREADY_EXISTS";
    public static final String INCORRECT_LOGIN   = "USER_INCORRECT_LOGIN";
    public static final String USER_NOT_FOUND    = "USER_NOT_FOUND";
    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
    public static final String INCORRECT_ACTION  = "INCORRECT_ACTION";
    public static final String ACCEPTED          = "ACCEPTED";
    
    /* Paths */
    public static final String PATH                   = "/images/products/";
    public static final String PREDEFINED_COLORS      = "/images/colors/";
    public static final String IMAGE_PATH             = "/var/www/html/images/products/";
    public static final String COLOR_PATH             = "/var/www/html/images/products/";
    public static final String PREDEFINED_COLORS_PATH = "/var/www/html/images/colors/";
    
    /* Tamanos de las imagenes en HQ y SQ */
    public static final int IMAGE_HEIGHT_S = 400;  
    public static final int IMAGE_HEIGHT_L = 850;
    
    /* Tamano de los iconos */
    public static final int ICON_WIDTH  = 50;
    public static final int ICON_HEIGHT = 50;
    
    /* Aspect ratios de las imagenes de cada tienda */
    public static final double PDH_ASPECT_RATIO         = 1.5f;
    public static final double SPRINGFIELD_ASPECT_RATIO = 1.12f;
    public static final double HYM_ASPECT_RATIO         = 1.5f;
    public static final double BLANCO_ASPECT_RATIO      = 1.28f;
    public static final double ZARA_ASPECT_RATIO        = 1.24f;
    
    /* Parametros para las busquedas */
    public static final double MAX_SIMILARITY_THRESHOLD    = 0.98f;
    public static final double MEDIUM_SIMILARITY_THRESHOLD = 0.925f;
    
    public static final short MAX_SUGGESTIONS = 4;
    
    /* Lista de acciones posibles sobre un producto */
    public static final short ACTION_VIEWED        = 0;
    public static final short ACTION_FAVORITE      = 1;
    public static final short ACTION_VISITED       = 2;
    public static final short ACTION_ADDED_TO_CART = 3;
    public static final short ACTION_SHARED        = 4;
}
