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
        
        List<Product> productList = new ArrayList<>();
        
        // Obtener el HTML
        Document document = Jsoup.connect( "http://spf.com/es/tienda/man/abrigos" )
                                    .timeout( Properties.TIMEOUT ).get();
            
        // Obtener el link de 'Ver todos'
        Element seeAll = document.select( "div.pagination a" ).last();
            
        // Comprobar que existe el link de 'Ver todos'
        if ( seeAll != null )
            document = Jsoup.connect( "http://spf.com" 
                           + seeAll.attr( "href" ) ).timeout( Properties.TIMEOUT ).get();            
            
        // Obtener el campo info de todos los productos
        Elements products = document.select( "ul.product-listing li div div.content_product > a" );
            
        for ( Element element : products )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( "http://spf.com"
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();
        
            // Obtener los atributos del producto
            String link = "http://spf.com" + element.attr( "href" );
            String name = document.select( "h1" ).first().ownText();
            String price = document.select( "div.product-price-block strong" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = document.select( "span.patron" ).first().ownText().replaceAll( "Ref: " , "" );
            
            //System.out.println( link );
            System.out.println( name );
            //System.out.println( price );
            System.out.println( "Referencia: " + reference );
            
            if ( ! containsProduct( productList, reference ) )
            {
                // Obtener los colores disponibles
                List<ColorVariant> colorList = new ArrayList<>();
                Elements colors = document.select( "ul.product_colors > li a" );
                for ( Element color : colors )
                {                    
                    // Nos conectamos de nuevo usando el color y sacamos el identificador del color '?color=XXXXX'
                    String idColor = color.attr( "href" )
                                        .substring( color.attr( "href" ).indexOf( "=" ) + 1 
                                            , color.attr( "href" ).length() );
                    
                    String colorName = color.select( "img" ).attr( "alt" );
                    String colorURL = fixURL( color.select( "img" ).attr( "src" ) );  
                    
                    System.out.println( "Color: " + colorName );
                    System.out.println( "ColorURL: " + colorURL );            
                    
                    Elements images;
                    if ( colors.size() > 1 )
                        images = document.select( "#product_image_list li.color_" + idColor + " a" );
                    else
                        images = document.select( "#product_image_list a" );
                   
                    
                    List<Image> imagesURL = new ArrayList<>();
                    for ( Element img : images )
                    {
                        imagesURL.add( new Image( fixURL( img.attr( "href" ) ) ) );
                        System.out.println( fixURL( img.attr( "href" ) ) );
                    }
                    
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
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }     
}
