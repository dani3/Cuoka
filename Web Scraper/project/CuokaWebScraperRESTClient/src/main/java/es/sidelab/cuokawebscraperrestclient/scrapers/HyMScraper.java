package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @class Scraper especifico para HyM, la pagina esta desarrollada con AJAX
 * @author Daniel Mancebo
 */

public class HyMScraper implements GenericScraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<Product>();
    
    @Override
    public List<Product> scrap( URL urlShop, URL urlSection ) throws IOException 
    {      
        // Obtener el HTML
        Document document = Jsoup.connect( urlSection.toString() ).timeout( TIMEOUT ).get();
          
        // Obtener los links a todos los productos
        Elements elements = document.select( "h3.product-item-headline > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : elements )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( urlShop.toString()
                            + element.attr( "href" ) ).timeout( TIMEOUT ).ignoreHttpErrors( true ).get();

            // Obtener los atributos del producto
            Element name = document.select( "h1.product-item-headline" ).first(); 
            Element price = document.select( "div.product-item-price span" ).first();
            Element image = document.select( "div.product-detail-main-image-container img" ).first();

            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
              
            // Creamos y añadimos el producto a la lista concurrente               
            productList.add( new Product( Double.parseDouble( price.ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim() )
                                    , name.ownText()
                                    , image.attr( "src" ) ) );
        }
            
        return productList;
    }
    
}
