package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Interfaz de la que implementaran los scrapers especificos de cada tienda.
 * @author Daniel Mancebo Aldea
 */

public interface Scraper 
{
    /**
     * Metodo que scrapea una seccion de una tienda.
     * @param shop: tienda.
     * @param section: seccion.
     * @return lista con los productos scrapeados.
     * @throws IOException 
     */
    public Map<String, Object> scrap(Shop shop, Section section) throws IOException;  
    
    /**
     * Metodo que corrige una url si es necesario.
     * @param url: url a corregir.
     * @return url corregida.
     */
    public String fixURL(String url);
}
