package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.utils.ActivityStatsManager;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
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
 * @class Scraper especifico de Zara
 * @author Daniel Mancebo Aldea
 */

public class ZaraScraper implements Scraper 
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    private static final Logger LOG = Logger.getLogger( SpringfieldScraper.class );
    
    @Override
    public List<Product> scrap( Shop shop, Section section, String htmlPath ) throws IOException
    {        
        List<String> pages = new ArrayList<>();
        
        int prodOK = 0;
        int prodNOK = 0;
        
        File html = new File( htmlPath );
        
        // Parseamos el html producido por python
        Document document = Jsoup.parse( html, "UTF-8" );
        
        Elements products = document.select( "#main-product-list li.product > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : products )
        {        
            String path = htmlPath.replaceAll( ".html" , "_AUX.html" );
            
            try 
            {               
                List<ColorVariant> variants = new ArrayList<>();               
                
                File file = PythonManager.executeRenderProduct( element.attr( "href" ), path );
                
                // CRUCIAL para que al abrir el fichero este todo escrito.
                Thread.sleep(500);
                
                document = Jsoup.parse( file, "UTF-8" );
                
                // Obtener los atributos propios del producto
                String different_price = null;
                String link = element.attr( "href" );                 
                String name = document.select( "div header > h1" ).first().ownText()
                                                                          .replaceAll( "\\\\[nt]", "" )
                                                                          .toUpperCase(); 
                
                String price = document.select( "span.price" ).first().attr( "data-price" )
                                                                      .replaceAll( "EUR", "" )
                                                                      .replaceAll( ",", "." )
                                                                      .trim();
                
                String reference = document.select( "div.right p.reference" ).first().ownText()
                                                                                     .replaceAll( "Ref. ", "" )
                                                                                     .replaceAll( "/", "" )
                                                                                     .replaceAll( "\\\\[nt]", "" );
                
                String description = document.select( "#description p.description span" ).first().ownText()
                                                                                                 .replaceAll( "\\\\[nt]", "" ); 
                     
                // Sacamos el descuento si lo hay
                if ( ! document.select( "strong.product-price span" ).isEmpty() )
                    different_price = document.select( "strong.product-price span" ).first()
                                                                                    .ownText()
                                                                                    .replaceAll( "€", "" )
                                                                                    .replaceAll( ",", "." ).trim();
                                
                if ( description.length() > 255 )
                    description = description.substring( 0, 255 );
                
                String colorReference = reference;
                String colorName = document.select( "div.colors label" ).first().select( "div.imgCont" ).attr( "title" )
                                                                                                        .toUpperCase()
                                                                                                        .trim()
                                                                                                        .replace('\\', '-');
                
                List<Image> imagesURL = new ArrayList<>();
                Elements images = document.select( "#main-images div.media-wrap" );
                for ( Element img : images ) 
                {
                    String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) );

                    imagesURL.add( new Image( imageURL ) );
                }

                variants.add( new ColorVariant( colorReference, colorName, null, imagesURL ) );   
                
                if ( ! variants.isEmpty() )
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
                }
                else
                    prodNOK++;           
                
            } catch ( Exception e ) { 
                prodNOK++; 
                
            } finally {
                // CRUCIAL llamar al recolector de basura
                System.gc();
                
                PythonManager.deleteFile( path );
            }
            
        } // for products
        
        ActivityStatsManager.updateProducts( shop.getName(), section, prodOK, prodNOK );  
        
        return productList;
    }
    
    /*
     * Metodo que arregla la URL, añade el protocolo si no esta presente, y codifica los espacios.
     */
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }   
}
