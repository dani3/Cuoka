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
    public List<Product> scrap( Shop shop, Section section, String filePath ) throws IOException
    {                
        // Lista con los links de cada producto
        List<String> productsLink = getListOfLinks( filePath, shop.getURL().toString() );
        
        int prodOK = 0;
        int prodNOK = 0;
          
        // Recorremos todos los productos y sacamos sus atributos
        int colorId = 1;
        for ( String productLink : productsLink )
        {
            try 
            {
                List<ColorVariant> variants = new ArrayList<>();

                // Obtener el HTML del producto conectandonos al link que hemos sacado antes (atributo 'href')
                Document document = Jsoup.connect( productLink )
                                         .timeout( Properties.TIMEOUT )
                                         .header( "Accept-Language", "es" )
                                         .ignoreHttpErrors( true ).get();

                // Obtener los atributos propios del producto
                String link = productLink;
                String name = document.select( "#product-information h1" ).first().ownText(); 
                String price = document.select( "strong.product-price" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = document.select( "div.m_tabs_cont p.patron" ).first().ownText().replaceAll("Ref:", "");
                String description = document.select( "div.m_tabs_cont div p" ).first().ownText().replaceAll( "\n", " "); 
                
                if ( description.length() > 255 )
                    description = description.substring(0, 255);
                
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
                            String colorURL = null;
                            
                            if ( color.select( "img" ).first() != null )
                                colorURL = fixURL( color.select( "img" ).first().attr( "src" ) ); 
                            
                            // Por si acaso los colores se llaman igual, ponemos un numero al final del color para que no se repitan.
                            String colorName = color.select( "img" ).first().attr( "alt" ).toUpperCase();
                            for ( Element sameColor : colors )
                            {
                                if ( sameColor.select( "img" ).first().attr( "alt" ).toUpperCase().equals( colorName ) )
                                {
                                    colorName = colorName.concat(Integer.toString(colorId++));
                                }
                            }

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
                    String colorURL = fixURL( color.select( "img" ).attr( "src" ) );
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
                if ( ! containsProduct( productList, reference, section.getName() ) )
                {
                    if ( ! colors.isEmpty() )
                    {
                        productList.add( new Product( Double.parseDouble( price )
                                                , name
                                                , shop.getName()
                                                , section.getName()
                                                , link 
                                                , description
                                                , section.isMan()
                                                , variants ) );  
                        prodOK++;
                        
                    } else
                        prodNOK++;
                    
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
                
            } catch ( Exception ex ) { ex.printStackTrace(); prodNOK++; }
            
        } // for products
        
        ActivityStatsManager.updateProducts( shop.getName(), section, prodOK, prodNOK );
            
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
                  
        Elements products = document.select( "ul.product-listing div.content_product a" );
        
        for( Element element : products )
        {
            links.add( fixURL( shopUrl + element.attr( "href" ) ) );
        }
        
        return links;
    }
    
    /*
     * Metodo que devuelve true si el producto esta ya en la lista
     */
    private static boolean containsProduct( List<Product> productList, String reference, String section )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) && ( p.getSection().equals( section ) ) )
                    return true;
        
        return false;
    }
}
