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
 * @class Scraper especifico para SuiteBlanco
 * @author Daniel Mancebo Aldea
 */

public class BlancoScraper implements GenericScraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException
    {        
        // Obtener el HTML
        Document document = Jsoup.connect( section.getURL().toString() )
                                    .timeout( Properties.TIMEOUT ).get();
        
        // Guardamos los links de los productos
        Elements elements = document.select( "h2.product-name > a" );
            
        for ( Element element : elements )
        {
            document = Jsoup.connect( element.attr( "href" ) )
                               .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
            
            // Obtener todos los atributos del producto
            String link = element.attr( "href" );
            Element name = document.select( "div.product-name span" ).first(); 
            Element price = document.select( "span.regular-price span" ).first();
            Element image = document.select( "div.product-image-gallery img" ).first();
            
            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
            
            // Creamos y añadimos el producto a la lista concurrente               
            productList.add( new Product( Double.parseDouble( price.ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim() )
                                    , name.ownText()
                                    , shop.getName()
                                    , section.getName()
                                    , fixURL( image.attr( "src" ) )
                                    , link
                                    , section.isMan() ) );
        }
            
        return productList;
    }
    
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    } 
}
