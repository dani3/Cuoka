package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @interface Interfaz de la que implementaran los scrapers especificos de cada tienda.
 * @author Daniel Mancebo Aldea
 */

public interface GenericScraper 
{
    final int TIMEOUT = 60000;
    
    public List<Product> scrap( URL urlShop, URL urlSection ) throws IOException;
}
