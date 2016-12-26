package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

/**
 * Clase que gestiona la comunicacion con el servidor REST.
 * @author Daniel Mancebo Aldea
 */

public class RestClient 
{
    private static final Logger LOG = Logger.getLogger(RestClient.class);
    
    private final RestTemplate restClient;
    private final URL server;
    
    public RestClient(URL server) 
    {
        this.restClient = new RestTemplate();
        this.server = server;
    }
    
    /*
     * Metodo que envia una lista de productos al servidor REST.
     */
    public synchronized void saveProducts(List<Product> products, Shop shop)
    {
        LOG.info("Enviando lista de productos de " + shop.getName() + " al servidor...");
        LOG.info("Se envian " + products.size() + " products!");
        restClient.postForObject(server.toString() 
                + "/products/" + shop.getName(), products.toArray(), Product[].class);
        LOG.info("Procuctos enviados correctamente");
    }
}
