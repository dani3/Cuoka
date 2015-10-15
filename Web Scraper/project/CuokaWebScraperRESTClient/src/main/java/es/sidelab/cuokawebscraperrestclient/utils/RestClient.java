package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.net.URL;
import org.springframework.web.client.RestTemplate;

/**
 * @class Clase que gestiona la comunicacion con el servidor REST
 * @author Daniel Mancebo Aldea
 */

public class RestClient 
{
    private final RestTemplate restClient;
    private final URL serverIp;
    
    public RestClient( URL serverIp ) 
    {
        this.restClient = new RestTemplate();
        this.serverIp = serverIp;
    }
    
    /*
     * Metodo que conecta con el servidor REST y devuelve la lista de tiendas online
     */
    public Shop[] getArrayOfShops()
    {
        return deleteShopsOffline( restClient.getForObject( serverIp.toString() + "/get" , Shop[].class ) );
    } 
    
    /*
     * Metodo que elimina del vector todas las tiendas que no esten online
     */
    private static Shop[] deleteShopsOffline( Shop[] shops )
    {
        Shop[] aux = new Shop[ shops.length ];
        
        int j = 0;
        for ( Shop shop : shops )
            if ( ! shop.isOffline() )
                aux[ j++ ] = shop;
        
        return aux;
    }
}
