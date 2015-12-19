
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

public class testBlanco {

    public static void main(String[] args) throws Exception {
        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
        
        // Obtener el HTML
        Document document = Jsoup.connect( "http://www.suiteblanco.com/es/es_es/partes-de-abajo/faldas.html" )
                                    .timeout( Properties.TIMEOUT ).get();
        
        // Guardamos los links de los productos
        Elements products = document.select( "h2.product-name > a" );
            
        for ( Element element : products )
        {
            document = Jsoup.connect( element.attr( "href" ) )
                               .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
            
            // Obtener todos los atributos propios del producto
            String link = element.attr( "href" );
            String name = document.select( "div.product-name span" ).first().ownText().toUpperCase(); 
            String price = document.select( "span.price" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
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
                        if ( ! img.attr( "id" ).equals( "image-main" ) )
                            imagesURL.add( new Image( fixURL( img.attr( "src" ) ) ) );
                    
                    first = false;
                }
            
                variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
            }
            
            productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , ""
                                    , ""
                                    , link 
                                    , true
                                    , variants ) );
            
            
        } // for products
        
        Product p = productList.get( 1 );
        
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
          
            
            System.out.println( "\n" );            
        }
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }   
    
}
