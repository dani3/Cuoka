package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Clase que gestionara todo lo relacionado con directorios y ficheros.
 * @author Daniel Mancebo Aldea
 */

public class FileManager 
{
    private static final Log LOG = LogFactory.getLog(FileManager.class);
    
    /**
     * Metodo que crea el directorio de una tienda si no existe.
     * @param shop: nombre de la tienda.
     */
    public static void createProductsDirectory(String shop)
    {
        LOG.info("Comprobamos que el directorio '" + Properties.IMAGE_PATH + shop + "' existe");
        File folder = new File(Properties.IMAGE_PATH + shop);
        
        if (!folder.exists())
        {   
            LOG.info("El directorio no existe, se crea");
            folder.mkdirs();
            
        } else        
            LOG.info("El directorio '" + Properties.IMAGE_PATH + shop + "' ya existe");
    }
    
    /**
     * Metodo que comprueba si existe un fichero.
     * @param file: nombre del fichero que se quiere comprobar.
     * @return true si el ficher existe.
     */
    public static boolean existsFile(String file)
    {
        return (new File(file).exists());
    }
    
    /**
     * Metodo que borra un fichero.
     * @param file: nombre del fichero a borrar.
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteFile(String file)
    {
        return (new File(file).delete());
    }
}
