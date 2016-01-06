package es.sidelab.cuokawebscraperrestclient.test;

/**
 *
 * @author Dani
 */

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mainMassimoDutti 
{
    public static void main(String[] args) throws Exception 
    {
        Document document = Jsoup.connect( "http://www.massimodutti.com/es/es/men/chaquetas-c680512.html" )
                                   .timeout( Properties.TIMEOUT ).get();
        
        System.out.println( document.html() );
        
        Elements products = document.select( "#product-list.clearfix li" );
        
        System.out.println( products.size() );
    }
}
