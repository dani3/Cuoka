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

public class mainTest {

    public static void main(String[] args) throws Exception {
        
        // Obtener el HTML
        Document document = Jsoup.connect( "http://spf.com/es/tienda/man/abrigos" )
                                    .timeout( Properties.TIMEOUT ).get();
            
        // Obtener el link de 'Ver todos'
        Element seeAll = document.select( "div.pagination a" ).last();
            
        // Comprobar que existe el link de 'Ver todos'
        if ( seeAll != null )
            document = Jsoup.connect( "http://spf.com" 
                           + seeAll.attr( "href" ) ).timeout( Properties.TIMEOUT ).get();            
            
        // Obtener el campo info de todos los productos
        Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
        int i = 0;
        for ( Element element : products )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( "http://spf.com"
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
        
            // Obtener los atributos del producto
            String link = "http://spf.com" + element.attr( "href" );
            Element name = document.select( "h1" ).first();
            Element price = document.select( "div.product-price-block strong" ).first();
            Element image = document.select( "#image_preview img" ).first();
            Element ref = document.select( "span.patron" ).first();
            
            
            // Obtener todas las imagenes
            Elements images = document.select( "#product_image_list a" );
            List<String> imagesURL = new ArrayList<>();
            for ( Element img : images )
            {
               imagesURL.add( fixURL( img.attr( "href" ) ) );
               System.out.println( fixURL( img.attr( "href" ) ) );
            }
              
            
            
            //System.out.println( link );
            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
            
            
            // Referencia
            System.out.println( ref.ownText().replaceAll( "Ref: " , "" ) );
        }
        
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }     
}
