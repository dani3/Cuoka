package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.utils.MultithreadManager;
import es.sidelab.cuokawebscraperrestclient.utils.RestClient;
import java.net.URL;

public class main 
{
    private static final String URL = "http://192.168.33.10:8080";
    
    public static void main( String[] args ) throws Exception
    {   
        // Creamos un cliente REST y configuramos la URL del servidor
        RestClient restClient = new RestClient( new URL( URL ) );
        
        // Sacamos la lista de tiendas de la BD (tiene que ser final para poder usarlo en los Runnable)
        Shop[] shops = restClient.getArrayOfShops();        
        
        // Ejecutamos concurrentemente los scrapers
        MultithreadManager.parallelScrap( shops );
        
    } // main    
}
