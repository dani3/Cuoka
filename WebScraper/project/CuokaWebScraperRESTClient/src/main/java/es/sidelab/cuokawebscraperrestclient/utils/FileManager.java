package es.sidelab.cuokawebscraperrestclient.utils;

import java.io.File;
import org.apache.log4j.Logger;

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
}
