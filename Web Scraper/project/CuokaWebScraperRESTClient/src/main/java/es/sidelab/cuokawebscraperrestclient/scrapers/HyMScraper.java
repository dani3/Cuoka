package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.main;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dani
 */

public class HyMScraper implements GenericScraper
{
    private static List<Product> productList = new CopyOnWriteArrayList<Product>();
    
    @Override
    public List<Product> scrap( URL urlShop, URL urlSection ) 
    {      
        try {
            // Obtener el HTML
            Document document = Jsoup.connect( urlSection.toString() ).timeout( 20000 ).get();
            
            Elements elements = document.select( "h3.product-item-headline > a" );
            
            for ( Element element : elements )
            {
                document = Jsoup.connect( urlShop.toString()
                                + element.attr( "href" ).toString() ).timeout( 20000 ).ignoreHttpErrors( true ).get();

                // Obtener los atributos del producto
                Element name = document.select( "h1.product-item-headline" ).first(); 
                Element price = document.select( "div.product-item-price span" ).first();
                Element image = document.select( "div.product-detail-main-image-container img" ).first();

                System.out.println( name.ownText() );
                System.out.println( price.ownText() );
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
