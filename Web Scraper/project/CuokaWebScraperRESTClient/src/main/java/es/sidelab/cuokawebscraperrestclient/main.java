package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.web.client.RestTemplate;

public class main 
{
    public static void main( String[] args ) throws Exception
    {   
        // Creamos un cliente REST y configuramos la URL del servidor
        RestTemplate restClient = new RestTemplate();
        URL serverUrl = new URL( "http://192.168.33.10:8080/get" );
        
        // Sacamos la lista de tiendas de la BD (tiene que ser final para poder usarlo en los Runnable)
        final Shop[] shops = restClient.getForObject( serverUrl.toString() , Shop[].class );
        
        // Creamos un executor que creara un thread por cada tienda que haya.
        ExecutorService executorShops = Executors.newFixedThreadPool( shops.length );
        
        for ( int i = 0; i < shops.length; i++ )
        {
            final Shop shop = shops[i];
            Runnable task = new Runnable() 
            {
                @Override
                public void run() 
                {
                    // Sacamos el scraper especifico de la tienda
                    GenericScraper scraper = ScraperManager.getScraper( shop );
                    
                    // Creamos un executor que creara tantos threads como secciones tenga la tienda
                    ExecutorService executorSections = Executors.newFixedThreadPool( shop.getSections().size() );
                    
                    for ( int j = 0; j < shop.getSections().size(); j++ )
                    {
                        final Section section = shop.getSections().get( j );
                        
                        Runnable taskSection = new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                scraper.scrap( shop.getURL() , section.getURL() );
                            }
                        };
                        
                        executorSections.execute( taskSection );
                    } // for
                }
            };
            
            executorShops.execute( task );
        } // for 
        
        executorShops.shutdown();
    } // main    
}
