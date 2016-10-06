package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Clase que se encarga del tema de ficheros.
 * @author Daniel Mancebo Aldea
 */

public class FileManager 
{
    private static final Logger LOG = Logger.getLogger(FileManager.class);  
    
    /**
     * Metodo que crea un fichero de texto con todos los links recibidos.
     * @param listOfLinks: lista de links a escribir.
     * @param section: seccion a la que pertenecen los productos.
     */
    public static void writeLinksToFile(List<String> listOfLinks, Section section)
    {
        BufferedWriter bw = null;
        
        try {            
            File file = new File(section.getPath() + section.getName() + "_LINKS.txt");
            
            LOG.info("Escribiendo fichero de links: '" + section.getPath() + section.getName() + "_LINKS.txt'");
            
            if(! file.exists())
                file.createNewFile();
            
            // El segundo parametro debe estar a false para que se sobreescriba el contenido
            FileWriter fw = new FileWriter(file, false);
            bw = new BufferedWriter(fw);
            for(int i = 0; i < listOfLinks.size(); i++)
            {
                // Comprobamos que no escribimos repetido el link (NECESARIO para los colores)
                if((i == 0) || (! listOfLinks.get(i - 1).equals(listOfLinks.get(i))))
                {
                    bw.write(listOfLinks.get(i));
                    
                    // Evitamos escribir un salto de linea si es el ultimo producto
                    if(i != (listOfLinks.size() - 1))
                        bw.write("\n");
                }              
                
            }
            
            LOG.info("Fichero de links: '" + section.getPath() + section.getName() + "_LINKS.txt" 
                    + "' escrito correctamente");
            
        } catch (IOException ex) {
            LOG.error("Error escribiendo el fichero '" + section.getPath() + section.getName() + "_LINKS.txt'");
            LOG.error(ex.getMessage());
            
        } finally {
            try {
                if(bw != null)
                    bw.close();
                
            } catch (IOException ex) {
                LOG.error("Error cerrando el fichero '" + section.getPath() + section.getName() + "_LINKS.txt'");
                LOG.error(ex.getMessage());
            }
        }
    }
    
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
