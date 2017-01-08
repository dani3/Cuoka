package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
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
    
    /**
     * Metodo que crea el fichero de resultados.
     * @param shop: tienda.
     * @param day: dia del mes.
     * @param month: mes del año.
     * @param year: año.
     * @return File si se ha creado correctamente, null EOC.
     */
    @Nullable
    public static File createResultsFile(Shop shop, int day, int month, int year)
    {
        File file = new File(Properties.SCRAPING_RESULT_PATH + shop.getName() + "_" + day + "_" + month + "_" + year + ".txt");
        if (!file.exists())
        {
            try 
            {
                file.createNewFile();
                
            } catch (IOException ex) {
                LOG.error("Error creando el fichero '" 
                    + Properties.SCRAPING_RESULT_PATH + shop.getName() + "_" + day + "_" + month + "_" + year + ".txt");
                LOG.error(ex.getMessage());
                
                return null;
            }
        } 
        
        return file;
    }
}
