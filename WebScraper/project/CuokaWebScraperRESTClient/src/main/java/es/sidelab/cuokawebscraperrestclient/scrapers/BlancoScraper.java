package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.ActivityStatsManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.log4j.Logger;
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
    public List<Product> scrap( Shop shop, Section section, String filePath ) throws IOException
    {        
        // Lista con los links de cada producto
        List<String> productsLink = getListOfLinks( filePath, shop.getURL().toString() );
        
        int prodOK = 0;
        int prodNOK = 0;
            
        for ( String productLink : productsLink )
        {
            try 
            {
                Document document = Jsoup.connect( productLink )
                                         .header( "Accept-Language", "es" )
                                         .timeout( Properties.TIMEOUT )
                                         .ignoreHttpErrors( true ).get();

                // Obtener todos los atributos propios del producto
                String link = productLink;
                String name = document.select( "h1.product-name" ).first().ownText().toUpperCase(); 
                String reference = document.select( "p.product-number" ).first().ownText().replaceAll( "Product: ", "" );
                String description = document.select( "p.product-description" ).first().ownText().replaceAll( "\n", " " );
                String price = document.select( "p.product-price" ).first().ownText().replaceAll( "€", "" ).trim();
                String decimals = document.select( "p.product-price small" ).first().ownText().replaceAll( ",", "." ).trim();
                price = price + decimals;
                
                if ( description.length() > 255 )
                    description = description.substring(0, 255);
                
                // Obtenemos los colores del producto
                boolean first = true;
                List<ColorVariant> variants = new ArrayList<>();
                
                // Hay dos product-color-selector repetidos, nos quedamos solo con uno
                Element colorList = document.select( "div.product-color-selector" ).first();            
                Elements colors = colorList.select( "span" );
                for ( Element color : colors )
                {
                    List<Image> imagesURL = new ArrayList<>();

                    String colorName = color.ownText().toUpperCase();

                    // De Blanco no podemos acceder a las imagenes de los colores alternativos, solo las del color principal
                    if ( first )
                    {
                        Elements images = document.select( "#product-gallery-list img" );
                        for ( Element img : images )
                            imagesURL.add( new Image( fixURL( shop.getURL().toString() + img.attr( "src" ) ) ) );

                        first = false;

                        variants.add( new ColorVariant( reference, colorName, null, imagesURL ) );
                    }
                }

                if ( ! colors.isEmpty() )
                {
                    prodOK++;
                    
                    productList.add( new Product( Double.parseDouble( price )
                                            , name
                                            , shop.getName()
                                            , section.getName()
                                            , link 
                                            , description
                                            , section.isMan()
                                            , variants ) );
                } else
                    prodNOK++;
                
            } catch ( Exception e ) { prodNOK++; }
            
        } // for products
        
        ActivityStatsManager.updateProducts(shop.getName(), section, prodOK, prodNOK );
    
        return productList;
    }
    
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    } 
    
    @Override
    public List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException
    {
        List<String> links = new ArrayList<>();        
        
        File html = new File( htmlPath );
        Document document = Jsoup.parse( html, "UTF-8" );
                  
        Elements products = document.select( "div.products-list a" );
        
        for( Element element : products )
        {
            links.add( fixURL( shopUrl + element.attr( "href" ) ) );
        }
        
        return links;
    }
}
