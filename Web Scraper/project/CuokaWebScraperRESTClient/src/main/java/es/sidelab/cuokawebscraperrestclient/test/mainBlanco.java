
package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mainBlanco 
{
    public static void main(String[] args) throws Exception 
    {        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
        
        File html = new File( "C:\\Users\\Dani\\Dropbox\\Cuoka\\scrapers_files\\Blanco_true\\false\\Blanco_Shorts_false.html" );
        
        Document document = Jsoup.parse( html, "UTF-8" );
        
        // Guardamos los links de los productos
        Elements products = document.select( "div.cell-1 a.cell-link" );
            
        for ( Element element : products )
        {
            document = Jsoup.connect( "https://www.blanco.com/" +  element.attr( "href" ) )
                                .header( "Accept-Language", "es" )
                                .timeout( Properties.TIMEOUT )
                                .ignoreHttpErrors( true ).get();
            
            // Obtener todos los atributos propios del producto
            String link = "https://www.blanco.com/" + element.attr( "href" );
            String name = document.select( "h1.product-name" ).first().ownText().toUpperCase(); 
            String price = document.select( "p.product-price" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "p.product-number" ).first().ownText().replaceAll( "Product: ", "" );
            
            System.out.println( link );
            System.out.println( name );
            System.out.println( price );
            System.out.println( reference );
            
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
