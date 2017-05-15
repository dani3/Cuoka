package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.FileManager;
import es.sidelab.cuokawebscraperrestclient.utils.MultithreadManager;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
import es.sidelab.cuokawebscraperrestclient.utils.ScraperManager;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class main 
{
    private static final Logger LOG = Logger.getLogger(main.class);   
    
    public static void main(String[] args) throws Exception
    {    
        Runnable runnable = () -> {
            if (Properties.CLEAN)
            {
                FileManager.cleanTemporalDirectory();
            }
                
            LOG.info("Renderizando paginas");
            
            try
            {
                if (PythonManager.executeRenderSections())
                {
                    LOG.info("Buscamos la lista de tiendas");
                    
                    // Sacamos la lista de tiendas
                    List<Shop> shops = ScraperManager.getArrayOfShops();
                    
                    LOG.info("Se han encontrado " + shops.size() + " tiendas");
                    
                    // Ejecutamos concurrentemente los scrapers
                    MultithreadManager.parallelScrap(shops);
                }
                
            } catch(IOException | InterruptedException unused) {}
        };
        
        if (!Properties.DEBUG)
        {
            ZoneId currentZone = ZoneId.of("Europe/Madrid");
            ZonedDateTime zonedNow = ZonedDateTime.of(LocalDateTime.now(), currentZone);
            ZonedDateTime zonedNext = zonedNow.withHour(01).withMinute(00).withSecond(00);

            if(zonedNow.compareTo(zonedNext) > 0)
            {
                zonedNext = zonedNext.plusDays(1);
            }

            Duration duration = Duration.between(zonedNow, zonedNext);
            long initalDelay = duration.getSeconds();

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);            
            scheduler.scheduleAtFixedRate(runnable, initalDelay, 24*60*60, TimeUnit.SECONDS);
            
        } else {
            runnable.run();            
        }
    }    
}
