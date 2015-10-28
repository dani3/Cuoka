/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

public class mainTest {

    private static boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            if ( p.getReference().equals( reference ) )
                return true;
        
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
      
        // Obtener el HTML
        Document document = Jsoup.connect( "http://www.suiteblanco.com/es/es_es/vestidos.html" )
                                    .timeout( Properties.TIMEOUT ).get();
        
        // Guardamos los links de los productos
        Elements elements = document.select( "h2.product-name > a" );
            
        for ( Element element : elements )
        {
            document = Jsoup.connect( element.attr( "href" ) )
                               .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
            
            // Obtener todos los atributos del producto
            String link = element.attr( "href" );
            String name = document.select( "div.product-name span" ).first().ownText(); 
            String price = document.select( "span.regular-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
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
                
                System.out.println( "Nombre color: " + colorName );
                System.out.println( "Icono del color: " + colorURL );
                
                // De Blanco no podemos acceder a las imagenes de los colores alternativos, solo el del principal
                if ( first )
                {
                    Elements images = document.select( "div.product-image-gallery img" );
                    imagesURL = new ArrayList<>();
                    for ( Element img : images )
                    {
                        imagesURL.add( new Image( fixURL( img.attr( "src" ) ) ) );
                        System.out.println( fixURL( img.attr( "src" ) ) );
                    }
                    
                    first = false;
                }
                
                variants.add( new ColorVariant( colorName, colorURL, imagesURL ) );
            }
            
            //System.out.println( name );
            //System.out.println( price );
            System.out.println( "Referencia: " + reference );
            
            // Creamos y añadimos el producto a la lista concurrente               
            productList.add( new Product( reference
                                    , Double.parseDouble( price )
                                    , name
                                    , "Blanco"
                                    , "Vestidos"
                                    , link
                                    , false
                                    , variants ) );
        }
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }     
}
