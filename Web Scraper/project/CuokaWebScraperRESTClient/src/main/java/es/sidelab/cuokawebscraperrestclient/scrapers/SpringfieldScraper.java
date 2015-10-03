/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.main;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @Class Scraper especifico para Springfield
 * @author Daniel Mancebo Aldea
 */
public class SpringfieldScraper implements GenericScraper 
{
    /*try {
            // Obtener el HTML
            Document document = Jsoup.connect( URL ).get();
            // Obtener el link de 'Ver todos'
            Element page = document.select( "div.pagination a" ).last();
            // Obtener el nuevo HTML con todos los productos
            document = Jsoup.connect( "http://spf.com" + page.attr( "href" ) ).get();
            
            // Obtener el campo info de todos los productos
            Elements products = document.select( "ul.product-listing li div div.product-main-info a" );
            
            for ( Element product : products )
                System.out.println( product.attr( "href" ) );
            
        } catch ( IOException ex ) {
            Logger.getLogger( main.class.getName()).log(Level.SEVERE, null, ex );
        }
    try {
            // Obtener el HTML
            Document document = Jsoup.connect( URL ).get();
            
            // Obtener el nombre
            Element name = document.select( "h1" ).first();
            System.out.println( name.ownText() );
            
            Element price = document.select( "div.product-price-block strong" ).first();
            System.out.println( price.ownText() );
            
            Element image = document.select( "#image_preview img" ).first();
            System.out.println( image.attr( "src" ) );
            
        } catch ( IOException ex ) {
            Logger.getLogger( main.class.getName()).log(Level.SEVERE, null, ex );
        }
    
    */
}
