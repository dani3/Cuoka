package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * Clase que gestiona todas las llamadas a scripts de Python.
 * @author Daniel Mancebo Aldea
 */

public class PythonManager 
{
    private static final Logger LOG = Logger.getLogger(PythonManager.class);
    
    /**
     * Metodo que ejecuta el script 'renderSections.py'.
     * @return true si se ha ejecutado correctamente.
     * @throws IOException 
     * @throws java.lang.InterruptedException
     */
    public static boolean executeRenderSections() throws IOException, InterruptedException
    {
        File[] folders = new File(Properties.SHOPS_PATH).listFiles();
        
        // Recorremos las tiendas
        for (File folder : folders)
        {
            if (folder.isDirectory())
            {
                // Sacamos el nombre de la tienda ('tienda_online')
                String folderName = folder.getName();
                
                if (folderName.contains("true"))
                {  
                    // Recorremos hombre y mujer si esta online
                    File[] subFolders = new File(Properties.SHOPS_PATH + "\\" + folderName).listFiles();
                    
                    for(File subFolder : subFolders)
                    {
                        // Ignoramos el fichero 'url.txt'
                        if (subFolder.isDirectory())
                        {
                            // Sacamos si es hombre o mujer
                            String man = subFolder.getName();                            
                            String path = Properties.SHOPS_PATH + folderName + "\\" + man + "\\";
                            
                            // Borramos los txt con los links generados de una ejecucion anterior
                            for (File file : subFolder.listFiles())
                            {
                                if (file.getName().contains(".txt") || file.getName().contains(".dat"))
                                {
                                    file.delete();
                                }
                            }
                            
                            // Encapsulamos el path entre comillas para que los espacios no afecten                            
                            LOG.info(
                                "Ejecutando: python "+ "\"" + path + "renderSections.py" + "\" " + Properties.CHROME_DRIVER + " \"" + path + "\"");
                            
                            // Ejecutamos el script de la tienda
                            Runtime.getRuntime().exec(new String[] { "python"
                                        , path + "renderSections.py"
                                        , Properties.CHROME_DRIVER
                                        , path });
                            
                            // Nos quedamos esperando hasta que termine
                            File file = new File(path + "done.dat");
                            while (! file.exists()) 
                            {
                                file = new File(path + "done.dat");
                            }
                            
                            file.delete();
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Metodo que llama al script de python 'renderProducts'
     * @param section: seccion a la que pertenecen los productos.
     * @throws IOException 
     */
    public static void executeRenderProducts(final Section section) throws IOException
    {
        Runtime.getRuntime().exec(new String[]{ "python"
                    , section.getPath() + "renderProducts.py"
                    , Properties.CHROME_DRIVER
                    , section.getName()
                    , section.getPath()
                    , section.getPath() + section.getName() + "_LINKS.txt" }); 
    }
    
    /**
     * Metodo que llama al script de python 'renderColors'
     * @param section: seccion a la que pertenecen los productos.
     * @throws IOException 
     */
    public static void executeRenderColors(final Section section) throws IOException
    {
        Runtime.getRuntime().exec(new String[]{ "python"
                    , section.getPath() + "renderColors.py"
                    , Properties.CHROME_DRIVER
                    , section.getName()
                    , section.getPath()
                    , section.getPath() + section.getName() + "_LINKS.txt" }); 
    }
}
