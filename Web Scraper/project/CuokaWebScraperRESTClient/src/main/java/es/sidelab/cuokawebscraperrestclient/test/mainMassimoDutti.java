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
import java.io.File;
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
        File html = new File( "C:\\Python27/dutti.html" );
        
        Document document = Jsoup.parse( html, "UTF-8" );
        
        System.out.println( document.html() );
        
        Elements products = document.select( "#product-list.clearfix li" );
        
        for ( Element product : products )
        {
            document = Jsoup.connect( product.attr("href") )
                                   .timeout( Properties.TIMEOUT ).get();
        }
    }
}
