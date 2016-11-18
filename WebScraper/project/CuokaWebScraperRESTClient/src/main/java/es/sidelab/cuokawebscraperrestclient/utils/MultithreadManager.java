package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.log4j.Logger;
import es.sidelab.cuokawebscraperrestclient.scrapers.Scraper;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * Clase que gestiona todas las tareas que se realicen en paralelo.
 * @author Daniel Mancebo Aldea
 */

public class MultithreadManager 
{
    private static final Logger LOG = Logger.getLogger(MultithreadManager.class); 
    
    private static CountDownLatch countDownLatch;
    
    /*
     * Metodo que crea los threads necesarios para cada tienda y envia los productos al servidor.
     */
    public static void parallelScrap(List<Shop> shops)
    {
        LOG.info("Iniciando proceso de scraping concurrentemente...");
        
        // Creamos un executor que creara un thread por cada tienda que haya.
        ExecutorService executorShops = Executors.newFixedThreadPool(Properties.MAX_THREADS_SHOP);
        
        countDownLatch = new CountDownLatch(shops.size());
        
        for (int i = 0; i < shops.size(); i++)
        {            
            final Shop shop = shops.get(i);
            Runnable task = () -> {
                // Sacamos el scraper especifico de la tienda
                LOG.info("Llamamos al ScraperManager para obtener el scraper de " + shop.getName());
                Scraper scraper = ScraperManager.getScraper(shop);
                LOG.info("Scraper de " + shop.getName() + " obtenido");
                 
                // Creamos un executor que creara tantos threads como secciones tenga la tienda
                ExecutorService executorSections = Executors.newFixedThreadPool(Properties.MAX_THREADS_SECTIONS);
                // Creamos la lista donde se van a volcar todos los tasks.
                Set<Callable<List<Product>>> listOfTasks = new HashSet<>();    
                
                for (int j = 0; j < shop.getSections().size(); j++)
                {
                    final Section section = shop.getSections().get(j);
                    
                    // Tarea de cada scraper
                    Callable<List<Product>> taskSection = () -> scraper.scrap(shop, section);
                    
                    listOfTasks.add(taskSection);                    
                }
                
                LOG.info("Se han iniciado todos los threads de la tienda " + shop.getName());
                LOG.info("A la espera de que acaben todos los threads...");
                
                try 
                {
                    // Creamos la lista de futures donde se van a volcar todos los resultados.
                    List<Future<List<Product>>> listOfFutures = executorSections.invokeAll(listOfTasks);
                    // El ultimo future tendra la lista con todos los productos.
                    List<Product> productList = listOfFutures.get(shop.getSections().size() - 1).get();
                    
                    LOG.info("Todos los threads de " + shop.getName() + " han acabado");
                    LOG.info("Han sacado un total de " + productList.size() + " productos");
                    
                    LOG.info("Llamando al servidor REST para almacenar los productos!");
                    LOG.info("URL del servidor REST: " + Properties.SERVER);
                    
                    // LLamamos al servidor para enviar los productos.
                    RestClient restClient = new RestClient(new URL(Properties.SERVER));                            
                    restClient.saveProducts(productList, shop);
                    
                } catch (InterruptedException | ExecutionException ex ) {
                    LOG.error("ERROR: Se ha producido un error en un thread");
                    LOG.error(ex.getMessage());
                    ex.printStackTrace();
                    
                } catch (MalformedURLException ex) {
                    LOG.error("ERROR: Error al formar la URL para contactar con el servidor REST");
                    LOG.error(ex.getMessage());
                    
                } finally {
                    countDownLatch.countDown();
                    
                    LOG.info("Finalizamos el executor de secciones de la tienda " + shop.getName());
                    executorSections.shutdown();
                }
            };
            
            LOG.info("El thread " + i + "(" + shop.getName() + ") ha empezado...");
            executorShops.execute(task);            
        } 
        
        try
        {
            LOG.info("MAIN THREAD : Esperando a que acaben todas las tiendas...");
            countDownLatch.await();
            LOG.info("MAIN THREAD : Me despierto...");
            
        } catch (InterruptedException ex) {
            LOG.error("ERROR: Se ha producido un error con el CountDownLatch");            
        }
        
        // Escribimos en fichero la info de como han ido los scrapers    
        // Detenemos el executor
        executorShops.shutdown();       
    }
}
