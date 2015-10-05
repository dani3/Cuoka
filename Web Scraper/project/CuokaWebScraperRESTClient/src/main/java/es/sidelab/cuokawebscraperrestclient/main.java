package es.sidelab.cuokawebscraperrestclient;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.scrapers.GenericScraper;
import es.sidelab.cuokawebscraperrestclient.scrapers.ScraperManager;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class main 
{
    public static void main( String[] args ) throws Exception
    {            
        List<Section> aux = new ArrayList<Section>();
        Section section = new Section( "Abrigos", new URL( "http://spf.com/es/tienda/man/abrigos" ) );
        aux.add( section );
        Shop shop = new Shop( "Springfield", new URL( "http://spf.com/" ), aux );
                
        GenericScraper spf = ScraperManager.getScraper( shop );
        List<Product> list = spf.scrap( shop.getURL() , shop.getSections().get(0).getURL() );
        System.out.println( "List productos:" + list.size() );
    }    
}
