package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.io.IOException;
import java.util.List;

/**
 * @interface Interfaz de la que implementaran los scrapers especificos de cada tienda.
 * @author Daniel Mancebo Aldea
 */

public interface GenericScraper 
{
    public List<Product> scrap( Shop shop, Section section ) throws IOException;
}
