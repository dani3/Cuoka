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
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Clase que gestiona todas las tareas que se realicen en paralelo.
 * @author Daniel Mancebo Aldea
 */

public class MultithreadManager 
{
    private static final Logger LOG = Logger.getLogger(MultithreadManager.class); 
    
    private static CountDownLatch countDownLatch;
    
    /**
     * Metodo que paraleliza el proceso de scraping.
     * @param shops: lista de tiendas a scrapear.
     */
    public static void parallelScrap(List<Shop> shops)
    {
        LOG.info("Iniciando proceso de scraping concurrentemente...");
        
        // Se crea un executor que creara un thread por cada tienda que haya.
        ExecutorService executorShops = Executors.newFixedThreadPool(Properties.MAX_THREADS_SHOP);
        
        countDownLatch = new CountDownLatch(shops.size());
        
        for (int i = 0; i < shops.size(); i++)
        {            
            final Shop shop = shops.get(i);
            Runnable task = () -> {
                // Se saca el scraper especifico de la tienda
                LOG.info("Llamamos al ScraperManager para obtener el scraper de " + shop.getName());
                Scraper scraper = ScraperManager.getScraper(shop);
                
                if (scraper != null)
                {
                    LOG.info("Scraper de " + shop.getName() + " obtenido");

                    // Se crea un executor que creara tantos threads como secciones tenga la tienda
                    ExecutorService executorSections;                    
                    if (shop.getName().equalsIgnoreCase("Zara"))
                    {
                        executorSections = Executors.newFixedThreadPool(Properties.ZARA_THREADS);
                    } else {
                        executorSections = Executors.newFixedThreadPool(Properties.MAX_THREADS_SECTIONS);
                    }
                    
                    // Se crea la lista donde se van a volcar todos los tasks.
                    List<Callable<Map<String, Object>>> listOfTasks = new ArrayList<>();    

                    for (int j = 0; j < shop.getSections().size(); j++)
                    {
                        final Section section = shop.getSections().get(j);

                        // Tarea de cada scraper
                        Callable<Map<String, Object>> taskSection = () -> scraper.scrap(shop, section);

                        listOfTasks.add(taskSection);                    
                    }

                    LOG.info("Se han iniciado todos los threads de la tienda " + shop.getName());
                    LOG.info("A la espera de que acaben todos los threads.");

                    try 
                    {
                        // Creamos la lista de futures donde se van a volcar todos los resultados.
                        List<Future<Map<String, Object>>> listOfFutures = executorSections.invokeAll(listOfTasks);
                        // El ultimo future tendra la lista con todos los productos.
                        List<Product> productList = 
                            (List<Product>)listOfFutures.get(shop.getSections().size() - 1).get().get(Properties.KEY_LIST);
                        List<ScrapingAnalyzer> analyzerList = 
                            (List<ScrapingAnalyzer>)listOfFutures.get(shop.getSections().size() - 1).get().get(Properties.KEY_ANALYZER);

                        LOG.info("Todos los threads de " + shop.getName() + " han acabado");
                        LOG.info("Han sacado un total de " + productList.size() + " productos");

                        LOG.info("Llamando al servidor REST de DEV para almacenar los productos");
                        LOG.info("URL del servidor REST: " + Properties.SERVER_DEV);

                        // Se envian los productos a los servidores.
                        RestClient restClient = new RestClient(new URL(Properties.SERVER_DEV));                            
                        restClient.saveProducts(productList, shop);
                        
                        if (!Properties.DEV)
                        {
                            LOG.info("Llamando al servidor REST de PROD para almacenar los productos");
                            LOG.info("URL del servidor REST: " + Properties.SERVER_PROD);
                        
                            restClient = new RestClient(new URL(Properties.SERVER_PROD));                            
                            restClient.saveProducts(productList, shop);
                        }
                            
                        // Se envia el correo con las estadisticas del proceso de scraping.
                        LOG.info("Enviando el correo con las estadísticas del proceso de scraping.");
                        MailSender.sendEmail(analyzerList, shop);

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
                }
            };
            
            LOG.info("El thread " + i + "(" + shop.getName() + ") ha empezado.");
            executorShops.execute(task);            
        } 
        
        try
        {
            LOG.info("MAIN THREAD : Esperando a que acaben todas las tiendas.");
            countDownLatch.await();
            LOG.info("MAIN THREAD : Me despierto.");
            
        } catch (InterruptedException ex) {
            LOG.error("ERROR: Se ha producido un error con el CountDownLatch");   
            LOG.error(ex.getMessage());
        }
          
        // Detenemos el executor
        executorShops.shutdown();       
    }
}
