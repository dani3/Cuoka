package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.MultithreadManager;
import es.sidelab.cuokawebscraperrestclient.utils.RestClient;
import java.net.URL;
import org.apache.log4j.Logger;

public class main 
{
    private static final Logger LOG = Logger.getLogger( main.class );   
    
    public static void main( String[] args ) throws Exception
    {   
        LOG.info( "Conectando al servidor REST a la URL: " + Properties.SERVER );
        
        // Creamos un cliente REST y configuramos la URL del servidor
        RestClient restClient = new RestClient( new URL( Properties.SERVER ) );
        
        // Sacamos la lista de tiendas utilizando un cliente REST
        Shop[] shops = restClient.getArrayOfShops();        
        
        LOG.info( "Lista de tiendas recibida y creada correctamente" );
        LOG.info( "Se han recibido " + shops.length + " tiendas" );
        
        // Ejecutamos concurrentemente los scrapers
        MultithreadManager.parallelScrap( shops );
    } // main    
}
