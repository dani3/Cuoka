package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.IOException;
import java.util.ArrayList;
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
            
            // Obtener todos los atributos propios del producto
            String link = element.attr( "href" );
            String name = document.select( "div.product-name span" ).first().ownText().toUpperCase(); 
            String price = document.select( "span.regular-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "#reference span" ).first().ownText();
            
            // Obtenemos los colores del producto
            boolean first = true;
            List<ColorVariant> variants = new ArrayList<>();
            Elements colors = document.select( "ul.super-attribute-select-custom li span img" );
            for ( Element color : colors )
            {
                List<Image> imagesURL = null;
                
                String colorName = color.attr( "title" ).toUpperCase();
                String colorURL = fixURL( color.attr( "src" ) );
                
                // De Blanco no podemos acceder a las imagenes de los colores alternativos, solo las del color principal
                if ( first )
                {
                    Elements images = document.select( "div.product-image-gallery img" );
                    imagesURL = new ArrayList<>();
                    for ( Element img : images )
                        imagesURL.add( new Image( fixURL( img.attr( "src" ) ) ) );
                    
                    first = false;
                }
                
                variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
            }
            
            // Creamos y aÃ±adimos el producto a la lista concurrente               
            productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , shop.getName()
                                    , section.getName()
                                    , link
                                    , section.isMan()
                                    , variants ) );
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
