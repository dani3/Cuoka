package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import static es.sidelab.cuokawebscraperrestclient.test.mainTest.fixURL;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @class Scraper especifico para Springfield
 * @author Daniel Mancebo Aldea
 */

public class SpringfieldScraper implements GenericScraper 
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException
    {        
        // Obtener el HTML
        Document document = Jsoup.connect( section.getURL().toString() )
                                    .timeout( Properties.TIMEOUT ).get();
            
        // Obtener el link de 'Ver todos'
        Element seeAll = document.select( "div.pagination a" ).last();
            
        // Comprobar que existe el link de 'Ver todos'
        if ( seeAll != null )
            document = Jsoup.connect( shop.getURL().toString() 
                           + seeAll.attr( "href" ) ).timeout( Properties.TIMEOUT ).get();            
            
        // Obtener el campo info de todos los productos
        Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
        for ( Element element : products )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( shop.getURL().toString()
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
        
            // Obtener los atributos del producto
            String link = shop.getURL().toString() + element.attr( "href" );
            String name = document.select( "h1" ).first().ownText();
            String price = document.select( "div.product-price-block strong" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "span.patron" ).first().ownText().replaceAll( "Ref: " , "" );
            
            // Los productos con la misma referencia se ignoran ya que ya se han insertado
            if ( ! containsProduct( productList, reference ) )
            {
                List<ColorVariant> colorList = new ArrayList<>();
                
                // Obtener los colores disponibles
                Elements colors = document.select( "ul.product_colors > li a" );
                for ( Element color : colors )
                {                    
                    // Sacamos el identificador del color de la URL ('?color=XXXXX'); hara falta mas adelante para filtrar las imagenes
                    String idColor = color.attr( "href" )
                                        .substring( color.attr( "href" ).indexOf( "=" ) + 1 
                                            , color.attr( "href" ).length() );
                    
                    // Obtenemos el nombre del color y la URL del icono
                    String colorName = color.select( "img" ).attr( "alt" ).toUpperCase();
                    String colorURL = fixURL( color.select( "img" ).attr( "src" ) );     
                    
                    // Si hay varios colores, nos quedamos solo con las imagenes del color actual
                    Elements images;
                    if ( colors.size() > 1 )
                        images = document.select( "#product_image_list li.color_" + idColor + " a" );
                    else
                        images = document.select( "#product_image_list a" );
                   
                    // Sacamos las URLs de las imagenes anteriores
                    List<Image> imagesURL = new ArrayList<>();
                    for ( Element img : images )
                        imagesURL.add( new Image( fixURL( img.attr( "href" ) ) ) );
                    
                    // Añadimos un nuevo ColorVariant a la lista 
                    colorList.add( new ColorVariant( colorName, colorURL, imagesURL ) );
                }
                
                Product p = new Product( reference
                                    , Double.parseDouble( price )
                                    , name
                                    , "Springfield"
                                    , "Abrigos" 
                                    , link 
                                    , true 
                                    , colorList );
                    
                productList.add( p );

            } // for colors
        } // for products
            
        return productList;
    }
    
    /*
     * Metodo que arregla la URL, añade el protocolo si no esta presente, y codifica los espacios
     */
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    } 
    
    /*
     * Metodo que devuelve true si el producto esta ya en la lista
     */
    private boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            if ( p.getReference().equals( reference ) )
                return true;
        
        return false;
    }
}
