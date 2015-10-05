/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.main;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    @Override
    public List<Product> scrap( URL urlShop, URL urlSection ) 
    {
        List<Product> productList = new ArrayList<Product>();
        
        try {
            // Obtener el HTML
            Document document = Jsoup.connect( urlSection.toString() ).get();
            
            // Obtener el link de 'Ver todos'
            Element seeAll = document.select( "div.pagination a" ).last();
            
            // Comprobar que existe el link de 'Ver todos'
            if ( seeAll != null )
                document = Jsoup.connect( urlShop.toString() + seeAll.attr( "href" ) ).get();            
            
            // Obtener el campo info de todos los productos
            Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
            int i = 0;
            for ( Element element : products )
            {
                // Obtener el HTML del producto
                System.out.println( urlShop.toString() + element.attr( "href" ) );
                document = Jsoup.connect( urlShop.toString() 
                        + element.attr( "href" ) ).timeout( 20000 ).ignoreHttpErrors( true ).get();
            
                // Obtener el nombre
                Element name = document.select( "h1" ).first();
                System.out.println( name.ownText() );
                
                // Obtener el precio
                Element price = document.select( "div.product-price-block strong" ).first();
                System.out.println( price.ownText() );

                // Obtener la ruta de la imagen
                Element image = document.select( "#image_preview img" ).first();
                System.out.println( image.attr( "src" ) );
                
                // Creamos y añadimos el producto a una lista auxiliar                
                productList.add( new Product( Double.parseDouble( price.ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim() )
                                        , name.ownText()
                                        , image.attr( "src" ) ) );
            }
            
            return productList;
            
        } catch ( IOException ex ) {
            Logger.getLogger( main.class.getName()).log(Level.SEVERE, null, ex );
        } 
       
        // No debería llegar aquí
        return null;
    }
}
