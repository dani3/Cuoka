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
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
    
    public static void main(String[] args) throws Exception {
        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
      
        // Obtener el HTML
        Document document = Jsoup.connect( "http://www2.hm.com/es_es/hombre/compra-por-producto/camisas.html" )
                                    .timeout( Properties.TIMEOUT ).get();
        
        // Obtener los links a todos los productos
        Elements elements = document.select( "h3.product-item-headline > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : elements )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( "http://www2.hm.com/"
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT )
                                                      .ignoreHttpErrors( true ).get();

            // Obtener los atributos propios del producto
            String link = "http://www2.hm.com/" + element.attr( "href" );
            String name = document.select( "h1.product-item-headline" ).first().ownText(); 
            String price = document.select( "div.product-item-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
            String reference = element.attr( "href" ).substring( element.attr( "href" ).indexOf( "." ) + 1 , element.attr( "href" ).lastIndexOf( "." ) );
            
            //System.out.println( name.ownText() );
            //System.out.println( price.ownText() );
            //System.out.println( image.attr( "src" ) );
            System.out.println( reference );
            
            if ( ! containsProduct( productList, reference ) )
            {
                // Obtener los colores
                List<ColorVariant> variants = new ArrayList<>();
                Elements colors = document.select( "div.product-colors ul.inputlist li > label" );
                for ( Element color : colors )
                {
                    // Nos conectamos al producto de cada color
                    String colorLink = "http://www2.hm.com/" + "es_es/productpage." + color.select( "input" ).attr( "data-articlecode" ) + ".html";
                    document = Jsoup.connect( colorLink ).timeout( Properties.TIMEOUT )
                                                          .ignoreHttpErrors( true ).get();

                    String colorReference = color.select( "input" ).attr( "data-articlecode" );
                    String colorName = color.attr( "title" ).toUpperCase();
                    String colorURL = fixURL( color.select( "div img" ).attr( "src" ) );

                    System.out.println( "ColorReference: " + colorReference );
                    System.out.println( "Color name: " + colorName );
                    System.out.println( "ColorURL: " + colorURL );

                    List<Image> imagesURL = new ArrayList<>();
                    Elements images = document.select( "div.product-detail-thumbnails li img" );
                    for ( Element img : images )
                    {
                        imagesURL.add( new Image( fixURL( img.attr( "src" ).replaceAll( "/product/thumb" , "/product/main" ) ) ) );
                        System.out.println( fixURL( img.attr( "src" ).replaceAll( "/product/thumb" , "/product/main" ) ) );
                    }

                    variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                }
                
                // Creamos y añadimos el producto a la lista concurrente               
                productList.add( new Product( reference
                                    , Double.parseDouble( price )
                                    , name
                                    , "Blanco"
                                    , "Vestidos"
                                    , link
                                    , false
                                    , variants ) );
            } // if contains           
        }
        
        for ( Product p : productList ) 
        {
            System.out.println( p.getName() + ": " + p.getColors().size() );
            for ( ColorVariant cv : p.getColors() )
                System.out.println( cv.getImages().size() );
        }
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }     
}
