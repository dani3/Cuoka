package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.web.client.RestTemplate;

/**
 * @class Clase que gestiona la comunicacion con el servidor REST
 * @author Daniel Mancebo Aldea
 */

public class RestClient 
{
    private static final Logger LOG = Logger.getLogger( RestClient.class );
    
    private RestTemplate restClient;
    private final URL SERVER;
    
    public RestClient( URL server ) 
    {
        this.restClient = new RestTemplate();
        this.SERVER = server;
    }
    
    /*
     * Metodo que conecta con el servidor REST y devuelve la lista de tiendas online
     */
    public Shop[] getArrayOfShops()
    {
        LOG.info( "Obteniendo lista de tiendas del servidor..." );
        return deleteShopsOffline( restClient.getForObject( SERVER.toString() + "/getShops" , Shop[].class ) );
    } 
    
    /*
     * Metodo que envia una lista de productos al servidor REST
     */
    public void saveProducts( List<Product> products, Shop shop )
    {
        LOG.info( "Enviando lista de productos al servidor..." );
        restClient.postForObject( SERVER.toString() 
                + "/addProducts/" + shop.getName(), products.toArray(), Product[].class );
        LOG.info( "Procuctos enviados correctamente" );
    }
    
    /*
     * Metodo que elimina del vector todas las tiendas que no esten online
     */
    private static Shop[] deleteShopsOffline( Shop[] shops )
    {
        LOG.info( "Filtramos las tiendas que no estan disponibles" );
        
        int cont = 0;
        for ( Shop shop : shops )
            if ( ! shop.isOffline() )
                cont++;
        
        Shop[] aux = new Shop[ cont ];
        
        int j = 0;
        for ( Shop shop : shops )
        {
            if ( ! shop.isOffline() )
            {
                LOG.info( "La tienda " + shop.getName() + " esta ONLINE" );
                aux[ j++ ] = shop;
                
            } else {
                LOG.info( "La tienda " + shop.getName() + " NO esta online" );
            }
        }
        
        LOG.info( "Filtro completado" );
        
        return aux;
    }
}
