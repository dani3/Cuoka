package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
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

public class BlancoScraper implements Scraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section, String htmlPath ) throws IOException
    {        
        File html = new File( htmlPath );
        Document document = Jsoup.parse( html, "UTF-8" );
        
        // Guardamos los links de los productos
        Elements products = document.select( "div.cell-1 a.cell-link" );
            
        for ( Element element : products )
        {
            try 
            {
                document = Jsoup.connect( shop.getURL().toString() +  element.attr( "href" ) )
                                    .header( "Accept-Language", "es" )
                                    .timeout( Properties.TIMEOUT )
                                    .ignoreHttpErrors( true ).get();

                // Obtener todos los atributos propios del producto
                String link = shop.getURL().toString() + element.attr( "href" );
                String name = document.select( "h1.product-name" ).first().ownText().toUpperCase(); 
                String price = document.select( "p.product-price" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = document.select( "p.product-number" ).first().ownText().replaceAll( "Product: ", "" );

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
                            if ( ! img.attr( "id" ).equals( "image-main" ) )
                                imagesURL.add( new Image( fixURL( img.attr( "src" ) ) ) );

                        first = false;
                    }

                    variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                }

                if ( ! colors.isEmpty() )
                {
                    productList.add( new Product( Double.parseDouble( price )
                                            , name
                                            , shop.getName()
                                            , section.getName()
                                            , link 
                                            , section.isMan()
                                            , variants ) );
                }
                
            } catch ( Exception e ) {}
            
        } // for products
            
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
