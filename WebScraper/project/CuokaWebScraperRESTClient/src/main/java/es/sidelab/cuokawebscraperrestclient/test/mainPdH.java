package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.FileManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Lucia Fernandez Guzman
 */

public class mainPdH 
{    
    public static void main(String[] args) throws Exception 
    {
        String shop = "http://pedrodelhierro.com";
        List<Product> productList = new ArrayList<>();
        // Lista con los links de cada producto
        List<String> productsLink = FileManager.getListOfLinks( "C:\\Users\\Dani\\Documents\\shops\\Pedro Del Hierro_true\\false\\Americanas.txt" );
          
        // Recorremos todos los productos y sacamos sus atributos
        int colorId = 1;
        for ( String productLink : productsLink )
        {
            try 
            {
                List<ColorVariant> variants = new ArrayList<>();

                // Obtener el HTML del producto conectandonos al link que hemos sacado antes (atributo 'href')
                Document document = Jsoup.connect( productLink ).timeout( Properties.TIMEOUT )
                                                                .header( "Accept-Language", "es" )
                                                                .ignoreHttpErrors( true ).get();

                // Obtener los atributos propios del producto
                String different_price = null;
                String link = productLink;
                String name = document.select( "#product-information h1" ).first().ownText(); 
                String price = document.select( "strong.product-price" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = document.select( "div.m_tabs_cont p.patron" ).first().ownText().replaceAll("Ref:", "");
                String description = document.select( "div.m_tabs_cont div p" ).first().ownText().replaceAll( "\n", " "); 
                
                // Sacamos el descuento si lo hay
                if ( ! document.select( "strong.product-price span" ).isEmpty() )
                    different_price = document.select( "strong.product-price span" ).first()
                                                                                    .ownText()
                                                                                    .replaceAll( "€", "" )
                                                                                    .replaceAll( ",", "." ).trim();
                                
                if ( description.length() > 255 )
                    description = description.substring(0, 255);
                
                Elements colors = document.select( "ul.product_colors li" );

                // Si hay varios colores
                if ( colors.size() > 1 )
                {                    
                    // Nos quedamos con el codigo del color, para diferenciar las imagenes
                    String colorCode = link.substring( link.indexOf( "?" ) + 1 ).replace( "=" , "_" );

                    for ( Element color : colors )
                    {                        
                        if( color.className().equals( colorCode ) )
                        {
                            String colorReference = reference;
                            String colorURL = null;
                            
                            if ( color.select( "img" ).first() != null )
                                colorURL = fixURL( color.select( "img" ).first().attr( "src" ) );                                
                            
                            // Por si acaso los colores se llaman igual, ponemos un numero al final del color para que no se repitan.
                            String colorName = color.select( "img" ).first().attr( "alt" ).toUpperCase();
                            for ( Element sameColor : colors )
                            {
                                if ( sameColor.select( "img" ).first().attr( "alt" ).toUpperCase().equals( colorName ) )
                                {
                                    colorName = colorName.concat(Integer.toString(colorId++));
                                }
                            }
                            
                            List<Image> imagesURL = new ArrayList<>();
                            Elements images = document.select( "#product_image_list li" );
                            for ( Element img : images )                                     
                                if ( img.className().contains( colorCode ) )
                                {
                                    String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) ).replaceAll( "minisq" , "main2" );

                                    imagesURL.add( new Image( imageURL ) );
                                }

                            variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                        }

                    } // for colors

                } else {
                    Element color = colors.first();

                    String colorReference = reference;
                    String colorURL = fixURL( color.select( "img" ).attr( "src" ) );                    
                    String colorName = color.select( "img" ).first().attr( "alt" ).toUpperCase();

                    List<Image> imagesURL = new ArrayList<>();
                    Elements images = document.select( "#product_image_list li" );
                    for ( Element img : images )     
                    {
                        String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) ).replaceAll( "minisq" , "main2" );

                        imagesURL.add( new Image( imageURL ) );
                    }

                    variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                }

                // Si el producto es nuevo, se inserta directamente, si no, se actualiza con el nuevo color
                if ( ! containsProduct( productList, reference ) )
                {
                    productList.add( new Product( Double.parseDouble( price )
                                            , name
                                            , ""
                                            , ""
                                            , link 
                                            , description
                                            , true
                                            , variants ) );                
                } else {
                    // Buscamos el producto
                    for ( Product product : productList )
                    {
                        for ( int i = 0; i < product.getColors().size(); i++ )
                        {
                            ColorVariant color = product.getColors().get( i );

                            if ( color.getReference().equals( reference ) )
                            {
                                product.getColors().addAll( variants );                            
                                break;
                            }                        
                        }
                    }
                }
                
            } catch ( Exception ex ) { ex.printStackTrace(); }
            
        } // for products
        
        System.out.println(productList.size());
        
        Product p = productList.get( 0 );
        
        System.out.println( "-------- INFO PRODUCTO ----------" );
        System.out.println( "Nombre: " + p.getName() );
        System.out.println( "Link: " + p.getLink() );
        System.out.println( "Description: " + p.getDescription());
        System.out.println( "Precio: " + p.getPrice() + " €" );
        System.out.println( "-------- INFO COLORES -----------" );
        for ( ColorVariant cv : p.getColors() )
        {
            System.out.println( " - Color: " + cv.getName() );
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
     
    private static boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
