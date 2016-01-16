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
        File html = new File("C:\\Users\\Dani\\Dropbox\\Cuoka\\scrapers_files\\Massimo Dutti_false\\true\\Massimo Dutti_Camisas_true.html");
        Document document = Jsoup.parse(html, "UTF-8");
        
        Elements products = document.select( "#product-list > li > a" );
        
        for ( Element product : products )
        {
            document = Jsoup.connect( product.attr( "href" ) )
                                .timeout( Properties.TIMEOUT )
                                .ignoreHttpErrors( true ).get();
            
            String link = product.attr( "href" );
            //String name = document.select( "h1.product-name" ).first().ownText().toUpperCase();
            //String price = document.select( "div.prices p.currentPrice" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            //String reference = document.select( "div.product-main-info #product-ref" ).first().ownText().replaceAll( "Ref. " , "" ).replaceAll( "/" , "" ).trim();
        
            System.out.println( link );
            //System.out.println( name );
            //System.out.println( price );
            //System.out.println( reference );
        }
    }
}
