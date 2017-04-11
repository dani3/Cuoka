package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import org.apache.log4j.Logger;
import es.sidelab.cuokawebscraperrestclient.scrapers.Scraper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase que gestiona los distintos scrapers, actua de middleware.
 * @author Daniel Mancebo Aldea
 */

public class ScraperManager 
{
    private static final Logger LOG = Logger.getLogger(ScraperManager.class);
    
    /**
     * Metodo que dada una tienda devuelve su scraper.
     * @param shop: Nombre de la tienda.
     * @return Scraper especifico de la tienda.
     */
    public static Scraper getScraper(Shop shop) 
    {
        try 
        {
            String className;
            
            if (shop.getName().equalsIgnoreCase("Pedro Del Hierro"))
            {
                className = "PdH";
            } else if (shop.getName().equalsIgnoreCase("Massimo Dutti")) {
                className = "MassimoDutti";                
            } else if (shop.getName().equalsIgnoreCase("Bordeaux the Brand")) {
                className = "BtB";     
            } else {
                className = shop.getName();
            }
            
            return (Scraper) Class.forName("es.sidelab.cuokawebscraperrestclient.scrapers." 
                                            + className + "Scraper").newInstance();
            
        } catch (ClassNotFoundException ex) {
            LOG.error("ERROR: No se encontro la clase");
            LOG.error(ex.getMessage());
            
        } catch (InstantiationException ex) {
            LOG.error("ERROR: No se pudo instanciar la clase");
            LOG.error(ex.getMessage());
            
        } catch (IllegalAccessException ex) {
            LOG.error("ERROR: Acceso no permitido");
            LOG.error(ex.getMessage());
        }
        
        // No debería llegar aquí
        return null;
    }
    
    /**
     * Metodo que crea los objetos Shop y Section.
     * @return Lista de tiendas encontradas.
     */
    public static List<Shop> getArrayOfShops()
    {
        LOG.info("Buscando tiendas online...");
        
        List<Shop> shops = new ArrayList<>();
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
                    LOG.info(folderName.replace("_true" , "").replace("Descubre_", "") + " esta ONLINE");
                    // Recorremos hombre y mujer si esta online
                    File[] subFolders = new File(Properties.SHOPS_PATH + "\\" + folderName).listFiles();
                    List<Section> sectionsList = new ArrayList<>();

                    for (File subFolder : subFolders)
                    {
                        if (subFolder.isDirectory())
                        {
                            // Sacamos si es hombre o mujer
                            String man = subFolder.getName();

                            // Recorremos los txt dentro
                            File[] sections = new File(Properties.SHOPS_PATH + "\\" 
                                                    + folderName + "\\" + man).listFiles();

                            for (File section : sections)
                            {      
                                if (section.getName().contains(".txt") && !section.getName().contains("Error"))
                                {                             
                                    Section s = new Section();
                                    String sectionName = section.getName().replace(".txt", "").replace("Seccion_", "");

                                    s.setMan(Boolean.valueOf(man));
                                    s.setName(sectionName);
                                    s.setPath(section.getAbsolutePath().replace(section.getName(), ""));

                                    sectionsList.add(s);
                                } 
                            } // for txts    
                        }                   
                    } // for hombre/mujer

                    try 
                    {                                        
                        // Una vez leidas todas las secciones, leemos la URL de la tienda
                        BufferedReader br = new BufferedReader(new FileReader(new File(folder + "\\url.txt")));
                        String url = br.readLine();

                        String name = (folderName.contains("Descubre")) ? 
                            folderName.substring(folderName.indexOf("_") + 1, folderName.lastIndexOf("_")) :
                            folderName.substring(0, folderName.indexOf("_"));
                        
                        shops.add(new Shop(name
                            , new URL(url)
                            , sectionsList
                            , true
                            , folderName.contains("Descubre")));

                    } catch (FileNotFoundException ex) {
                        LOG.error("Error abriendo el fichero de 'url.txt'");
                    } catch (IOException ex) {
                        LOG.error("Error leyendo el fichero 'url.txt' o formando la URL");
                    } 
                }
            }
            
        } // for shops
        
        return shops;
    }
}
