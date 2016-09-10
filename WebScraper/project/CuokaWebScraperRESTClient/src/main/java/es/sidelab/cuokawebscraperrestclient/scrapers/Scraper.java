package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.io.IOException;
import java.util.List;

/**
 * Interfaz de la que implementaran los scrapers especificos de cada tienda.
 * @author Daniel Mancebo Aldea
 */

public interface Scraper 
{
    public List<Product> scrap( Shop shop, Section section ) throws IOException;    
    public String fixURL( String url );
    public List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException;
}
