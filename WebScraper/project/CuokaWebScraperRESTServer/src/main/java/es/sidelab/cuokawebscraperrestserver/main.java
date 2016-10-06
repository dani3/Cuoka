package es.sidelab.cuokawebscraperrestserver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Clase principal que arranca Spring y configura todo el entorno
 * @author Daniel Mancebo Aldea
 */

@EnableCaching
@ComponentScan
@EnableAutoConfiguration
public class main 
{
    private static final Log LOG = LogFactory.getLog(main.class);
    
    public static void main(String[] args) 
    {
        SpringApplication.run(main.class, args);
    }    
    
    @Bean
    public CacheManager cacheManager() 
    {
        LOG.info("Activando cache...");
    	return new ConcurrentMapCacheManager("products");
    }
}
