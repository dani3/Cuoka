package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.web.client.RestTemplate;

public class main 
{
    public static void main( String[] args ) throws Exception
    {  
        RestTemplate restClient = new RestTemplate();
        URL serverUrl = new URL( "http://192.168.33.10:8080/get" );
        
        final Shop[] shops = restClient.getForObject( serverUrl.toString() , Shop[].class );
        
        Executor executorShops = Executors.newFixedThreadPool( shops.length );
        
        for ( int i = 0; i < shops.length; i++ )
        {
            final Shop shop = shops[i];
            Runnable task = new Runnable() 
            {
                @Override
                public void run() 
                {
                    GenericScraper scraper = ScraperManager.getScraper( shop );
                    
                    Executor executorSections = Executors.newFixedThreadPool( shop.getSections().size() );
                    
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
                        
                        System.out.println( "Ejecutando seccion: " + ( j + 1 ) );
                        executorSections.execute( taskSection );
                    } // for
                }
            };
            
            System.out.println( "Ejecutando tienda: " + ( i + 1 ) );
            executorShops.execute( task );
        } 
    }    
}
