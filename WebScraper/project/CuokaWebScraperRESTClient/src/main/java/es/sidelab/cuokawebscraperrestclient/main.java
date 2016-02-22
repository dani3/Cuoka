package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.MultithreadManager;
import es.sidelab.cuokawebscraperrestclient.utils.ScraperManager;
import java.io.File;
import java.util.List;
import org.apache.log4j.Logger;

public class main 
{
    private static final Logger LOG = Logger.getLogger( main.class );   
    
    public static void main( String[] args ) throws Exception
    {       
        Process p = Runtime.getRuntime().exec( "python "
                + Properties.RENDER_SCRIPT + "renderPages.py" );
        
        File file = new File( Properties.DONE_FILE_PYTHON );
        while ( ! file.exists() ) 
        {
            file = new File( Properties.DONE_FILE_PYTHON );
        }
        
        file.delete();
        
        LOG.info( "Buscamos la lista de tiendas" );
        
        // Sacamos la lista de tiendas
        List<Shop> shops = ScraperManager.getArrayOfShops();        
        
        LOG.info( "Se han encontrado " + shops.size() + " tiendas" );
        
        // Ejecutamos concurrentemente los scrapers
        MultithreadManager.parallelScrap( shops );
        
    } // main    
}
