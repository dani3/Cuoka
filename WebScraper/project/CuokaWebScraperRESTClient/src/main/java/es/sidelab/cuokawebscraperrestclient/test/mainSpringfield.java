package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mainSpringfield 
{    
    public static void main(String[] args) throws Exception 
    {        
        String url = "http://myspringfield.com/es/es/";
        String sectionName = "Jeans";
        String path = "C:\\Users\\Dani\\Documents\\shops\\Springfield_true\\false\\";
        List<Product> productList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks( path + sectionName + ".html" , url );
        
        for ( String productLink : productsLink )
        {
            try {
                // Obtener el HTML del producto
                Document document = Jsoup.connect( productLink )
                                         .timeout( Properties.TIMEOUT )
                                         .header( "Accept-Language", "es" )
                                         .ignoreHttpErrors( true ).get();

                // Obtener los atributos del producto
                String link = productLink;
                String name = document.select( "div.c02__product > h1.c02__product-name" ).first().ownText().toUpperCase();
                String price = document.select( "div.small-only > span.c02__pricing-item" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = document.select( "div.c02__article-number" ).first().ownText().replaceAll( "Ref. " , "" ).trim();
                String description = document.select( "div.c02__product-description" ).first().ownText().replaceAll( "\n" , " " );

                if ( description.length() > 255 )
                    description = description.substring(0, 255);

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
                        String colorName = color.select( "span.screen-reader-text" ).first().ownText().toUpperCase();
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

                    productList.add( new Product( Double.parseDouble( price )
                                        , name
                                        , ""
                                        , ""
                                        , link 
                                        , description
                                        , true
                                        , variants ) );
                }

            } catch ( Exception e ) { e.printStackTrace(); }

        } // for products
        
        Product p = productList.get( 5 );
        
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
        
        System.out.println( productList.size() );
        
    }
    
    private static boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
    
    private static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }
    
    private static List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException
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
}
