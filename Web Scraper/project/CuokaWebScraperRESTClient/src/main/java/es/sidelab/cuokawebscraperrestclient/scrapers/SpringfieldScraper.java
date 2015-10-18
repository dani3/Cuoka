package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<Product>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException
    {        
        // Obtener el HTML
        Document document = Jsoup.connect( section.getURL().toString() ).timeout( TIMEOUT ).get();
            
        // Obtener el link de 'Ver todos'
        Element seeAll = document.select( "div.pagination a" ).last();
            
        // Comprobar que existe el link de 'Ver todos'
        if ( seeAll != null )
            document = Jsoup.connect( shop.getURL().toString() 
                           + seeAll.attr( "href" ) ).timeout( TIMEOUT ).get();            
            
        // Obtener el campo info de todos los productos
        Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
        int i = 0;
        for ( Element element : products )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( shop.getURL().toString() 
                            + element.attr( "href" ) ).timeout( TIMEOUT ).ignoreHttpErrors( true ).get();
        
            // Obtener los atributos
            Element name = document.select( "h1" ).first();
            Element price = document.select( "div.product-price-block strong" ).first();
            Element image = document.select( "#image_preview img" ).first();
              
            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
              
            // Creamos y añadimos el producto a la lista concurrente               
            productList.add( new Product( Double.parseDouble( price.ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim() )
                                    , name.ownText()
                                    , shop.getName()
                                    , section.getName() ) );
        }
            
        return productList;
    }
}
