package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @class Clase que gestionara todo lo relacionado con directorios y ficheros
 * @author Daniel Mancebo Aldea
 */

public class FileManager 
{
    private static final Log LOG = LogFactory.getLog( FileManager.class );
    
    /*
     * Metodo que crea el directorio para almacenar los productos de una tienda
     */
    public static void createProductsDirectory( String shop )
    {
        LOG.info( "Comprobamos que el directorio '" + Properties.IMAGE_PATH + shop + "' existe" );
        File folder = new File( Properties.IMAGE_PATH + shop );
        
        if ( ! folder.exists() )
        {   
            LOG.info( "El directorio no existe, se crea" );
            folder.mkdirs();
            
        } else        
            LOG.info( "El directorio '" + Properties.IMAGE_PATH + shop + "' ya existe" );
    }
    
    /*
     * Metodo que crea el directorio para almacenar los iconos de los colores
     */
    public static void createColorsDirectory()
    {
        LOG.info( "Comprobamos que el directorio '" + Properties.COLOR_PATH + "' existe" );
        File folder = new File( Properties.COLOR_PATH );
        
        if ( ! folder.exists() )
        {   
            LOG.info( "El directorio no existe, se crea" );
            folder.mkdirs();
            
        } else        
            LOG.info( "El directorio '" + Properties.IMAGE_PATH + "' ya existe" );
    }
    
    /*
     * Metodo que comprueba si existe un fichero (NO FUNCIONA)
     */
    public static boolean existsFile( String file )
    {
        return ( new File( file ).exists() );
    }
}
