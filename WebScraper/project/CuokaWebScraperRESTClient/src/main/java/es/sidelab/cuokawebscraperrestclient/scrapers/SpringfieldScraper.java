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
 * Scraper especifico para Springfield
 * @author Daniel Mancebo Aldea
 */

public class SpringfieldScraper implements Scraper 
{
    private static final Logger LOG = Logger.getLogger( SpringfieldScraper.class );
    
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException
    {       
        // Lista con los links de cada producto
        String htmlPath = section.getPath() + section.getName() + ".html";
        // Sacamos los links de los productos
        List<String> productsLink = getListOfLinks( htmlPath, shop.getURL().toString() );
        
        int prodOK = 0;
        int prodNOK = 0;
            
        for ( String productLink : productsLink )
        {
            try {
                LOG.info( "Scraping: " + productLink );
                
                // Obtener el HTML del producto
                Document document = Jsoup.connect( productLink )
                                         .timeout( Properties.TIMEOUT )
                                         .header( "Accept-Language", "es" )
                                         .ignoreHttpErrors( true ).get();

                // Obtener los atributos del producto
                String link = productLink;
                // El nombre se pasa a mayusculas
                String name = document.select( "div.c02__product > h1.c02__product-name" ).first().ownText()
                                                                                                  .toUpperCase();
                // Del precio solo nos quedamos con los numeros
                String price = document.select( "div.small-only > span.c02__pricing-item" ).first().ownText()
                                                                                                   .replaceAll( "[^,.0-9]", "" )
                                                                                                   .replaceAll( ",", "." )
                                                                                                   .trim();
                // De la referencia eliminamos todo lo que no sean numeros
                String reference = document.select( "div.c02__article-number" ).first().ownText()
                                                                                       .replaceAll( "[^0-9]" , "" );
                // En la descripcion sustituimos los saltos de linea por espacios
                String description = document.select( "div.c02__product-description" ).first().ownText()
                                                                                              .replaceAll( "\n" , " " );

                // En BD no podemos guardar un string de mas de 255 caracteres, si es mas grande lo acortamos
                if ( description.length() > 255 )
                    description = description.substring( 0, 255 );

                // Los productos con la misma referencia se ignoran ya que ya se han tenido que insertar antes
                if ( ! containsProduct( productList, reference ) )
                {
                    List<ColorVariant> variants = new ArrayList<>();

                    // Obtener los colores disponibles
                    Elements colors = document.select( "ul.c02__swatch-list > li" );

                    for ( Element color : colors )
                    {                    
                        // Sacamos el link del color y nos conectamos
                        String colorLink = color.select( "a.swatchanchor" ).attr( "href" );
                        document = Jsoup.connect( colorLink )   
                                    .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();

                        // Obtenemos el nombre del color y la URL del icono 
                        String colorName = color.select("span.screen-reader-text").first().ownText().toUpperCase();
                        String colorURL = color.select( "span.c02__square" ).attr( "style" );

                        if ( ( colorURL != null ) && ( ! colorURL.isEmpty() ) )
                            colorURL = colorURL.replace( "background: url(" , "" ).replace( ")", "" );  
                        else
                            colorURL = null;                     

                        // Si hay varios colores, nos quedamos solo con las imagenes del color actual
                        Elements images = document.select( "div.c01--navdots div.c01__media" );

                        // Sacamos las URLs de las imagenes anteriores
                        List<Image> imagesURL = new ArrayList<>();
                        for ( Element img : images )
                            imagesURL.add( new Image( img.select( "img" ).first().attr( "src" ) ) );                            

                        // Añadimos un nuevo ColorVariant a la lista 
                        variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                    }

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
                    }
                    else
                        prodNOK++;
                }

            } catch ( Exception e ) { 
                LOG.error( "Excepcion en producto: " + productLink + " (" + e.toString() + ")" );
                
                prodNOK++; 
            }

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
                  
        Elements products = document.select( "a.c05__thumb-link" );
        
        for( Element element : products )
        {
            links.add( fixURL( element.attr( "href" ) ) );
        }
        
        return links;
    }

    private boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
