package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.activity.SectionActivityStats;
import es.sidelab.cuokawebscraperrestclient.activity.ShopActivityStats;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.scrapers.PdHScraper;
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
        try {
            if (shop.getName().equals("Pedro Del Hierro"))
                return (Scraper) new PdHScraper();
            else
                return (Scraper) Class.forName("es.sidelab.cuokawebscraperrestclient.scrapers." 
                                            + shop.getName() + "Scraper").newInstance();
            
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
                List<SectionActivityStats> sectionsActivityList = new ArrayList<>();

                // Sacamos el nombre de la tienda ('tienda_online')
                String folderName = folder.getName();

                ShopActivityStats shopActivity = new ShopActivityStats(
                                                            folderName.substring(0, folderName.indexOf("_")));
                shopActivity.setOnline(folderName.contains("true"));

                if (folderName.contains("true"))
                {               
                    LOG.info(folderName.replace("_true" , "") + " esta ONLINE");
                    // Recorremos hombre y mujer si esta online
                    File[] subFolders = new File(Properties.SHOPS_PATH + "\\" + folderName).listFiles();
                    List<Section> sectionsList = new ArrayList<>();

                    for(File subFolder : subFolders)
                    {
                        if (subFolder.isDirectory())
                        {
                            // Sacamos si es hombre o mujer
                            String man = subFolder.getName();

                            if (man.equals("true"))
                                shopActivity.setMan(true);

                            if (man.equals("false"))
                                shopActivity.setWoman(true);

                            // Recorremos los txt dentro
                            File[] sections = new File(Properties.SHOPS_PATH + "\\" 
                                                    + folderName + "\\" + man).listFiles();

                            for (File section : sections)
                            {      
                                if (section.getName().contains(".txt") && !section.getName().contains("links_error"))
                                {                             
                                    Section s = new Section();
                                    String sectionName = section.getName().replace(".txt", "");

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

                        shops.add(new Shop(folderName.substring(0, folderName.indexOf("_"))
                                        , new URL(url)
                                        , sectionsList
                                        , true));

                        shopActivity.setUrl(url);

                    } catch (FileNotFoundException ex) {
                        LOG.error("Error abriendo el fichero de 'url.txt'");

                    } catch (IOException ex) {
                        LOG.error("Error leyendo el fichero 'url.txt' o formando la URL");
                        shopActivity.setUrl(ex.getMessage());

                    } finally {
                    }     

                } else {
                    shopActivity.setOnline(false);
                }
            }
            
        } // for shops
        
        return shops;
    }
}
