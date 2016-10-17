package es.sidelab.cuokawebscraperrestclient.properties;

/**
 * Clase con todas las constantes necesarias.
 * @author Daniel Mancebo Aldea
 */

public class Properties 
{    
    public static final int MAX_THREADS_SHOP = 4;
    public static final int MAX_THREADS_SECTIONS = 2;
    
    public static final String SERVER = "http://cuoka-ws.cloudapp.net:8080";
    public static final int TIMEOUT = 60000;   
    
    public static final String SHOPS_PATH = "C:\\Users\\Dani\\Documents\\shops\\";
    public static final String CHROME_DRIVER = "C:\\Users\\Dani\\Documents\\chromedriver";
    
    //public static final String SHOPS_PATH = "C:\\Users\\lux_f\\OneDrive\\Documentos\\shops\\";
    //public static final String CHROME_DRIVER = "C:\\Users\\lux_f\\Documents\\chromedriver";

    public static final String DONE_FILE_PYTHON = SHOPS_PATH + "done.dat";
}
