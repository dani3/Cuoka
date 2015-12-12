package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import org.apache.log4j.Logger;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperInterface;

/**
 * @Class Clase que gestiona los distintos scrapers, actua de middleware.
 * @author Daniel Mancebo Aldea
 */

public class ScraperManager 
{
    private static final Logger LOG = Logger.getLogger( ScraperManager.class );
    
    /*
     * Metodo que dada una tienda devuelve su scraper.
     */
    public static ScraperInterface getScraper( Shop shop ) 
    {
        try {
            return ( ScraperInterface ) Class.forName( "es.sidelab.cuokawebscraperrestclient.scrapers." 
                                        + shop.getName() + "Scraper" ).newInstance();
            
        } catch ( ClassNotFoundException ex ) {
            LOG.error( "ERROR: No se encontro la clase" );
            LOG.error( ex.getMessage() );
            
        } catch ( InstantiationException ex ) {
            LOG.error( "ERROR: No se pudo instanciar la clase" );
            LOG.error( ex.getMessage() );
            
        } catch ( IllegalAccessException ex ) {
            LOG.error( "ERROR: Acceso no permitido" );
            LOG.error( ex.getMessage() );
        }
        
        // No debería llegar aquí
        return null;
    }
}
