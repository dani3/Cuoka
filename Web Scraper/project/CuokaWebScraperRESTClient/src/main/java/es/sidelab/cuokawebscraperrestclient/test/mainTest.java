/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dani
 */
public class mainTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        Runtime.getRuntime().exec( "sudo /usr/bin/python resize.py" );
        
        
        /*List<Product> productList = new ArrayList<Product>();
        
        Document document = Jsoup.connect( "http://www.suiteblanco.com/es/es_es/vestidos.html" ).get();
            
        Elements elements = document.select( "h2.product-name > a" );
        System.out.println( elements.size() );
        
        for ( Element element : elements )
        {
            document = Jsoup.connect( element.attr( "href" ) )
                    .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
            
            Element name = document.select( "div.product-name span" ).first(); 
            Element price = document.select( "span.regular-price span" ).first();
            Element image = document.select( "div.product-image-gallery img" ).first();
            
            System.out.println( element.attr( "href" ) );
            System.out.println( name.ownText() );
            System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
        }*/
        
    }
    
}
