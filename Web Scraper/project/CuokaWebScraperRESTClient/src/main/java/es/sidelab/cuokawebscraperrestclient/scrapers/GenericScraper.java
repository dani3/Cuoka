package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import java.net.URL;
import java.util.List;

/**
 * @interface Interfaz de la que implementaran los scrapers especificos de cada tienda.
 * @author Daniel Mancebo Aldea
 */

public interface GenericScraper 
{
    public List<Product> scrap( URL urlShop, URL urlSection );
}
