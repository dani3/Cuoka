package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.IOException;
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
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException 
    {      
        // Obtener el HTML
        Document document = Jsoup.connect( section.getURL().toString() )
                                    .timeout( Properties.TIMEOUT ).get();
          
        // Obtener los links a todos los productos
        Elements elements = document.select( "h3.product-item-headline > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : elements )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( shop.getURL().toString()
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT )
                                                      .ignoreHttpErrors( true ).get();

            // Obtener los atributos del producto
            String link = shop.getURL().toString() + element.attr( "href" );
            Element name = document.select( "h1.product-item-headline" ).first(); 
            Element price = document.select( "div.product-item-price span" ).first();
            Element image = document.select( "div.product-detail-main-image-container img" ).first();

            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
              
            // Creamos y añadimos el producto a la lista concurrente               
            productList.add( new Product( Double.parseDouble( price.ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim() )
                                    , name.ownText()
                                    , shop.getName()
                                    , section.getName()
                                    , fixURL( image.attr( "src" ) )
                                    , link ) );
        }
            
        return productList;
    }
    
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url.replace( " " , "%20" );
    }    
}
