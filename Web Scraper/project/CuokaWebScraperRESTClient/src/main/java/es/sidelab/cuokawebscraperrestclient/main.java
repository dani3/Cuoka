package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class main 
{
    public static void main( String[] args ) throws Exception
    {  
        RestTemplate restClient = new RestTemplate();
        URL serverUrl = new URL( "http://192.168.33.10:8080/get" );
        
        Shop[] shops = restClient.getForObject( serverUrl.toString() , Shop[].class );
                
        for ( Shop shop : shops )
        {
            GenericScraper spf = ScraperManager.getScraper( shop );
            List<Product> list = spf.scrap( shop.getURL() , shop.getSections().get(0).getURL() );
            System.out.println( "List productos:" + list.size() );
        }       
    }    
}
