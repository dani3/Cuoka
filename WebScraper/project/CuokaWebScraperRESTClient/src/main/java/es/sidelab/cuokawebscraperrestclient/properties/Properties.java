package es.sidelab.cuokawebscraperrestclient.properties;

/**
 * Clase con todas las constantes necesarias.
 * @author Daniel Mancebo Aldea
 */

public class Properties 
{    
    public static final boolean DEBUG = true;
    public static final boolean DEV = true;
    
    public static final int MAX_THREADS_SHOP = 2;
    public static final int MAX_THREADS_SECTIONS = 2;
    
    public static final int ZARA_THREADS = 1;
    
    public static final String SERVER_PROD = "http://46.183.117.22:8080";
    public static final String SERVER_DEV = "http://46.183.116.208:8080";
    public static final int TIMEOUT = 60000;   
    
    public static final String TEMP_PATH = "C:\\Users\\Dani\\AppData\\Local\\Temp\\";
    public static final String SHOPS_PATH = "C:\\Users\\Dani\\Documents\\shops\\";
    public static final String CHROME_DRIVER = "C:\\Users\\Dani\\Documents\\chromedriver";
    
    public static final String NAME_NOT_FOUND = "Productos sin nombre";
    public static final String PRICE_NOT_FOUND = "Productos sin precio";
    public static final String DESCRIPTION_NOT_FOUND = "Productos sin descripcion";
    public static final String COLOR_NAME_NOT_FOUND = "Colores sin nombre";
    public static final String REFERENCE_NOT_FOUND = "Colores sin referencia";
    public static final String NO_COLORS = "Productos sin ningun color";
    public static final String IMAGE_NOT_FOUND = "Imagenes vacias";
    
    public static final String FROM = "soporte.cuoka@gmail.com";
    public static final String PASSWORD = "Millonarios2017";
    public static final String HOST = "smtp.gmail.com";
    public static final String PORT = "587";
    
    public static final String KEY_LIST = "KEY_LIST";
    public static final String KEY_ANALYZER = "KEY_ANALYZER";
    
    //public static final String SHOPS_PATH = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\";
    //public static final String CHROME_DRIVER = "C:\\Users\\lux_f\\Documents\\chromedriver";
}
