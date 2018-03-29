package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.commons.io.FileUtils;

/**
 * Clase que se encarga del tema de ficheros.
 * @author Daniel Mancebo Aldea
 */

public class FileManager 
{
    private static final Logger LOG = Logger.getLogger(FileManager.class);  
    
    /**
     * Metodo que elimina un fichero dado.
     * @param path: ruta del fichero.
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteFile(String path)
    {        
        LOG.info("Borrando fichero: " + path);
        
        if (new File(path).delete())
        {
            LOG.info("Fichero " + path + " borrado correctamente");
            
            return true;
        }
        
        LOG.error("No ha sido posible borrar el fichero: " + path);
        
        return false;
    }
    
    /**
     * Metodo que elimina los ficheros dentro de la carpeta de Temp.
     */
    public static void cleanTemporalDirectory()
    {
        try 
        {
            FileUtils.cleanDirectory(new File(Properties.TEMP_PATH));
            
        } catch (IOException ex) {
            LOG.error("No ha sido posible borrar el fichero: " + Properties.TEMP_PATH);
            LOG.error(ex.toString());
        }
    }
}
