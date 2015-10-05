package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.scrapers.SpringfieldScraper;
import java.net.URL;

public class main 
{
    public static void main( String[] args ) throws Exception
    {
        SpringfieldScraper spf = new SpringfieldScraper();
        spf.scrap( new URL( "http://spf.com/" ) , new URL( "http://spf.com/es/tienda/woman/kimonos-y-ponchos" ) );
    }    
}
