package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.client.RestTemplate;

public class main 
{
    private static Shop[] checkOffline( Shop[] shops )
    {
        return null;
    }
    
    private static boolean hasEveryoneFinished( boolean[] finishedSections )
    {
        return false;
    }
    
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
            final int k = i;
            
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
                    CompletionService< List<Product> > completionSections = 
                            new ExecutorCompletionService< List<Product> > ( executorSections );
                    
                    boolean[] finishedSections = new boolean[ shop.getSections().size() ];
                            
                    for ( int j = 0; j < shop.getSections().size(); j++ )
                    {
                        final Section section = shop.getSections().get( j );
                        
                        // Tarea de cada scraperr
                        Callable< List<Product> > taskSection = new Callable< List<Product> >()
                        {
                            @Override
                            public List<Product> call() throws IOException
                            {
                                return scraper.scrap( shop.getURL(), section.getURL() );
                            }
                        };                        
                        
                        // Ejecucion de cada tarea
                        completionSections.submit( taskSection );                        
                        
                    } // for
                    
                    // Esperamos a que terminen los threads
                    for ( int j = 0; j < shop.getSections().size(); j++ )
                    {
                        try 
                        {
                            Future< List<Product> > future;
                        
                            future = completionSections.take();
                            List<Product> productList = future.get();
                            
                            finishedSections[ j ] = true;
                            
                            if ( hasEveryoneFinished( finishedSections ) )
                            {
                                // Insertar en BD
                                
                                // Finalizamos el executor
                                executorSections.shutdown();
                            }
                            
                            
                        } catch ( InterruptedException ex ) {
                            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                            finishedSections[ j ] = true;
                                
                        } catch ( ExecutionException ex ) {
                            Logger.getLogger(main.class.getName()).log(Level.SEVERE, null, ex);
                            finishedSections[ j ] = true;
                            
                        }                        
                    }                    
                }
            };
            
            executorShops.execute( task );
        } // for 
        
        executorShops.shutdown();
    } // main    
}
