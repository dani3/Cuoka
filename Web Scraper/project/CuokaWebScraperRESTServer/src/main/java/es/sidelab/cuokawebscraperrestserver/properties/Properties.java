package es.sidelab.cuokawebscraperrestserver.properties;

/**
 * @class Clase que proporciona todas las constantes necesarios
 * @author Daniel Mancebo Aldea
 */

public class Properties 
{
    public static final String IMAGE_PATH = System.getProperty( "user.dir" ) + "/images/";
    public static final String RESIZED_IMAGE_PATH = "/var/www/html/images/";
    public static final String DEFAULT_IMAGE = System.getProperty( "user.dir" ) + "/images/default.jpg";
    
    public static final int IMAGE_WIDTH = 350;
    public static final int IMAGE_HEIGHT = 500;
}
