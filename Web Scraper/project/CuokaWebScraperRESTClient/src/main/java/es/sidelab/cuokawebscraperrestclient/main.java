package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import es.sidelab.cuokawebscraperrestclient.scrapers.SpringfieldScraper;
import java.net.URL;

public class main 
{
    public static void main( String[] args ) throws Exception
    {        
        GenericScraper spf = ScraperManager.getScraper( "Springfield" );
        spf.scrap( new URL( "http://spf.com/" ) , new URL( "http://spf.com/es/tienda/woman/kimonos-y-ponchos" ) );
    }    
}
