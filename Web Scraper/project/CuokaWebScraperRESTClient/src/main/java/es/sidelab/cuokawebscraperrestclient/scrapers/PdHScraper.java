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
 * @class Scraper especifico para Pedro Del Hierro
 * @author Daniel Mancebo Aldea
 */

public class PdHScraper implements Scraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section, String htmlPath ) throws IOException
    {        
        File html = new File( htmlPath );
        Document document = Jsoup.parse( html, "UTF-8" );
        
        Elements products = document.select( "ul.product-listing li div.content_product > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : products )
        {
            try 
            {
                List<ColorVariant> variants = new ArrayList<>();

                // Obtener el HTML del producto conectandonos al link que hemos sacado antes (atributo 'href')
                document = Jsoup.connect( shop.getURL().toString()
                                + element.attr( "href" ) ).timeout( Properties.TIMEOUT )
                                                          .ignoreHttpErrors( true ).get();

                // Obtener los atributos propios del producto
                String link = shop.getURL().toString() + element.attr( "href" );
                String name = document.select( "#product-information h1" ).first().ownText(); 
                String price = document.select( "strong.product-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = document.select( "div.m_tabs_cont p.patron" ).first().ownText().replaceAll("Ref:", "");

                Elements colors = document.select( "ul.product_colors li" );

                // Si hay varios colores
                if ( colors.size() > 1 )
                {
                    // Nos quedamos con el codigo del color, para diferenciar las imagenes
                    String colorCode = link.substring( link.indexOf( "?" ) + 1 ).replace( "=" , "_" );

                    for ( Element color : colors )
                    {                        
                        if( color.className().equals( colorCode ) )
                        {
                            String colorReference = reference;
                            String colorURL = fixURL( color.select( "img" ).first().attr( "src" ) );
                            String colorName = color.select( "img" ).first().attr( "alt" ).toUpperCase();

                            List<Image> imagesURL = new ArrayList<>();
                            Elements images = document.select( "#product_image_list li" );
                            for ( Element img : images )                                     
                                if ( img.className().contains( colorCode ) )
                                {
                                    String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) ).replaceAll( "minisq" , "main2" );

                                    imagesURL.add( new Image( imageURL ) );
                                }

                            variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                        }

                    } // for colors

                } else {
                    Element color = colors.first();

                    String colorReference = reference;
                    String colorURL = fixURL( color.select( "img" ).first().attr( "src" ) );
                    String colorName = color.select( "img" ).first().attr( "alt" ).toUpperCase();

                    List<Image> imagesURL = new ArrayList<>();
                    Elements images = document.select( "#product_image_list li" );
                    for ( Element img : images )     
                    {
                        String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) ).replaceAll( "minisq" , "main2" );

                        imagesURL.add( new Image( imageURL ) );
                    }

                    variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                }

                // Si el producto es nuevo, se inserta directamente, si no, se actualiza con el nuevo color
                if ( ! containsProduct( productList, reference ) )
                {
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
                    
                } else {
                    // Buscamos el producto
                    for ( Product product : productList )
                    {
                        for ( int i = 0; i < product.getColors().size(); i++ )
                        {
                            ColorVariant color = product.getColors().get( i );

                            if ( color.getReference().equals( reference ) )
                            {
                                product.getColors().addAll( variants );                            
                                break;
                            }                        
                        }
                    }
                }
                
            } catch ( Exception ex ) {}
            
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
    
    private static boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
