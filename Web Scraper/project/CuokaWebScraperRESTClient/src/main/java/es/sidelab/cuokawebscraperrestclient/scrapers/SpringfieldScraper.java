package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.beans.Size;
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
 * @class Scraper especifico para Springfield
 * @author Daniel Mancebo Aldea
 */

public class SpringfieldScraper implements ScraperInterface 
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
            String name = document.select( "h1" ).first().ownText().toUpperCase();
            String price = document.select( "div.product-price-block strong" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "span.patron" ).first().ownText().replaceAll( "Ref: " , "" );
            
            // Los productos con la misma referencia se ignoran ya que ya se han tenido que insertar antes
            if ( ! containsProduct( productList, reference ) )
            {
                List<ColorVariant> variants = new ArrayList<>();
                
                // Obtener los colores disponibles
                Elements colors = document.select( "ul.product_colors > li" );
                for ( Element color : colors )
                {                    
                    // Sacamos el identificador del color de la URL ('?color=XXXXX'); hara falta mas adelante para filtrar las imagenes
                    String idColor = color.select( "a" ).attr( "href" )
                                        .substring( color.select( "a" ).attr( "href" ).indexOf( "=" ) + 1 
                                            , color.select( "a" ).attr( "href" ).length() );
                    
                    // Obtenemos el nombre del color y la URL del icono
                    String colorName = color.attr( "title" ).toUpperCase();
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
                    
                    // Sacamos los tamaños disponibles, en Springfield las tallas no disponibles no vienen en el HTML
                    Elements elements = document.select( "ul.product_sizes.color_" + idColor + " li");
                    List<Size> sizes = new ArrayList<>();
                    for( Element size : elements )
                        sizes.add( new Size( size.select( "label" ).text(), true ) );
                    
                    // Añadimos un nuevo ColorVariant a la lista 
                    variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL, sizes ) );
                }
                    
                productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , shop.getName()
                                    , section.getName()
                                    , link 
                                    , section.isMan()
                                    , variants ) );
            }
            
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
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
