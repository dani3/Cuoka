package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mainSpringfield {

    private static boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }
    
    public static void main(String[] args) throws Exception {
        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
        
        // Obtener el HTML
        Document document = Jsoup.connect( "http://spf.com/es/tienda/man/abrigos" )
                                    .timeout( Properties.TIMEOUT ).get();
            
        // Obtener el link de 'Ver todos'
        Element seeAll = document.select( "div.pagination a" ).last();
            
        // Comprobar que existe el link de 'Ver todos'
        if ( seeAll != null )
            document = Jsoup.connect( "http://spf.com/" 
                           + seeAll.attr( "href" ) ).timeout( Properties.TIMEOUT ).get();            
            
        // Obtener el campo info de todos los productos
        Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
        for ( Element element : products )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( "http://spf.com/"
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
        
            // Obtener los atributos del producto
            String link = "http://spf.com/" + element.attr( "href" );
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
                    // (regulinchi, si el primer producto que aparece es del segundo color, esto no funciona)
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
                    
                    // Añadimos un nuevo ColorVariant a la lista 
                    variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                }
                    
                productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , ""
                                    , ""
                                    , link 
                                    , true
                                    , variants ) );
            }
            
        } // for products
        
        Product p = productList.get( 2 );
        
        System.out.println( "-------- INFO PRODUCTO ----------" );
        System.out.println( "Nombre: " + p.getName() );
        System.out.println( "Link: " + p.getLink() );
        System.out.println( "Precio: " + p.getPrice() + " €" );
        System.out.println( "-------- INFO COLORES -----------" );
        for ( ColorVariant cv : p.getColors() )
        {
            System.out.println( " - Color: " + cv.getColorName() );
            System.out.println( " - Icono: " + cv.getColorURL() );
            System.out.println( " - Referencia: " + cv.getReference() );
            for ( Image image : cv.getImages() )
                System.out.println( " - " + image.getUrl() );
            
            System.out.println( "" );            
        }
        
    }
    
}
