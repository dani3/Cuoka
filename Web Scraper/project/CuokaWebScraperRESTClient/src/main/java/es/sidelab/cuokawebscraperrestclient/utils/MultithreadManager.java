package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;

/**
 * @class Clase que gestiona todas las tareas que se realicen en paralelo.
 * @author Daniel Mancebo Aldea
 */

public class MultithreadManager 
{
    private static final Logger LOG = Logger.getLogger( MultithreadManager.class );
    
    /*
     * Metodo que crea los threads necesarios para cada tienda
     */
    public static void parallelScrap( Shop[] shops )
    {
        LOG.info( "Iniciando proceso de scraping concurrentemente..." );
        
        // Creamos un executor que creara un thread por cada tienda que haya.
        ExecutorService executorShops = Executors.newFixedThreadPool( shops.length );
        
        for ( int i = 0; i < shops.length; i++ )
        {
            final int k = i;
            
            final Shop shop = shops[i];
            Runnable task = () -> {
                // Sacamos el scraper especifico de la tienda
                LOG.info( "Llamamos al ScraperManager para obtener el scraper de " + shop.getName() );
                GenericScraper scraper = ScraperManager.getScraper( shop );
                LOG.info( "Scraper de " + shop.getName() + " obtenido" );
                 
                // Creamos un executor que creara tantos threads como secciones tenga la tienda
                ExecutorService executorSections = Executors.newFixedThreadPool( shop.getSections().size() );
                CompletionService< List<Product> > completionSections =
                        new ExecutorCompletionService<> ( executorSections );
                
                // Vector de booleanos en el que cada thread actualiza su posicion cuando haya terminado
                boolean[] finishedSections = new boolean[ shop.getSections().size() ];
                
                for ( int j = 0; j < shop.getSections().size(); j++ )
                {
                    final Section section = shop.getSections().get( j );
                    
                    // Tarea de cada scraper
                    Callable< List<Product> > taskSection = () -> scraper.scrap( shop.getURL(), section.getURL() );
                    
                    // Ejecucion de cada tarea
                    LOG.info( "Se inicia el scraping de la seccion " 
                            + section.getName() + " de la tienda " + shop.getName() );
                    completionSections.submit( taskSection );
                    
                } // for
                
                LOG.info( "Se han iniciado todos los threads de la tienda " + shop.getName() );
                
                // Esperamos a que terminen los threads
                for ( int j = 0; j < shop.getSections().size(); j++ )
                {
                    try
                    {
                        LOG.info( "A la espera de que acabe un thread..." );
                        Future< List<Product> > future = completionSections.take();
                        List<Product> productList = future.get();  
                        LOG.info( "Ha acabado un thread de " + shop.getName() 
                                + "... Ha sacado " + productList.size() + " productos!" );
                        
                        // Ponemos nuestra posicion a true indicando que hemos terminado
                        finishedSections[ j ] = true;                        
                        LOG.info( "Se marca el thread como finalizado... El estado de los threads de " 
                                + shop.getName() + " es: " );
                                       
                        LOG.info( threadStatus( finishedSections ) );
                        
                        // Comprobamos si somos el ultimo thread, en tal caso, inserto la lista en BD
                        if ( hasEveryoneFinished( finishedSections ) ) 
                        {
                            LOG.info( "Todos los threads de " + shop.getName() + " han acabado" );
                            // Insertar en BD
                            
                            // Finalizamos el executor de secciones cuando el ultimo haya terminado
                            LOG.info( "Finalizamos el executor de secciones de la tienda " + shop.getName() );
                            executorSections.shutdown();
                        }                        
                        
                    } catch ( InterruptedException | ExecutionException ex ) {
                        LOG.info( "ERROR: Se ha producido un error en un thread" );
                        LOG.info( ex.getMessage() );
                        
                        finishedSections[ j ] = true;                        
                    }                    
                }
            };
            
            LOG.info( "El thread " + i + " de " + shop.getName() + " ha empezado..." );
            executorShops.execute( task );
        } // for 
        
        // Detenemos el executor
        executorShops.shutdown();
    }
    
    /*
     * Metodo que comprueba si todos los threads han acabado
     */
    private static boolean hasEveryoneFinished( boolean[] finishedSections )
    {
        int i = 0;
        while( i < finishedSections.length )
            if ( ! finishedSections[ i++ ] )
                return false;
        
        return true;
    }
    
    /*
     * Metodo que devuelve el estado de los threads
     */
    private static String threadStatus( boolean[] finishedSections )
    {
        String state = "";
        for ( int n = 0; n < finishedSections.length; n++ )
            state = state.concat( "| " + finishedSections[ n ] + " | " );
        
        return state;
    }
}
