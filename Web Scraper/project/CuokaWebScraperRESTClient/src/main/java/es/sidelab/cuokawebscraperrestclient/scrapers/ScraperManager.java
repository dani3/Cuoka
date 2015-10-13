package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @Class Clase que gestiona los distintos scrapers, actua de middleware.
 * @author Daniel Mancebo Aldea
 */

public class ScraperManager 
{
    public static GenericScraper getScraper( Shop shop ) 
    {
        try {
            return ( GenericScraper ) Class.forName( "es.sidelab.cuokawebscraperrestclient.scrapers." 
                                        + shop.getName() + "Scraper" ).newInstance();
            
        } catch ( ClassNotFoundException ex ) {
            Logger.getLogger(ScraperManager.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( InstantiationException ex ) {
            Logger.getLogger(ScraperManager.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch ( IllegalAccessException ex ) {
            Logger.getLogger(ScraperManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // No debería llegar aquí
        return null;
    }
}
