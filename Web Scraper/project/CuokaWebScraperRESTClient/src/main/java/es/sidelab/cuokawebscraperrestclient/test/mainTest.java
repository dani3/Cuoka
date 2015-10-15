/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
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
        List<Product> productList = new ArrayList<Product>();
        
        Document document = Jsoup.connect( "http://www2.hm.com/es_es/hombre/compra-por-producto/camisas/_jcr_content/main/productlisting.display.html?category=&sort=stock&sale=false&offset=0&page-size=1000" ).get();
            
        Elements elements = document.select( "h3.product-item-headline > a" );
        System.out.println( elements.size() );
        
        for ( Element element : elements )
        {
            document = Jsoup.connect( "http://www2.hm.com/" 
                            + element.attr( "href" ).toString() ).timeout( 20000 ).ignoreHttpErrors( true ).get();
            
            Element name = document.select( "h1.product-item-headline" ).first(); 
            Element price = document.select( "div.product-item-price span" ).first();
            Element image = document.select( "div.product-detail-main-image-container img" ).first();
            
            System.out.println( name.ownText() );
            System.out.println( price.ownText() );
            System.out.println( image.attr( "src" ) );
        }
        
    }
    
}
