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

public class mainSpringfield 
{    
    public static void main(String[] args) throws Exception 
    {        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
        List<String> pages = new ArrayList<>();
        
        boolean finished = false;
        int i = 0;
        
        File html = new File( "C:\\Users\\Dani\\Dropbox\\Cuoka\\scrapers_files\\Springfield_true\\true\\Springfield_Camisetas_true.html" );
        
        Document document = Jsoup.parse( html, "UTF-8" );
        
        Elements pagesElements = document.select( "ul.pagination__list li.pagination__list-item a" );
        
        // Si hay varias paginas...
        for ( Element page : pagesElements )
        {
            pages.add( page.attr( "href" ).concat( "&format=ajax" ) );
            System.out.println( page.attr( "href" ).concat( "&format=ajax" ) );
        }
            
        
        while ( ! finished )
        {
            // Obtener el campo info de todos los productos
            Elements products = document.select( "div.product-name > a.name-link" );

            for ( Element element : products )
            {
                try {
                    // Obtener el HTML del producto
                    document = Jsoup.connect( element.attr( "href" ) )
                                        .timeout( Properties.TIMEOUT ).ignoreHttpErrors( true ).get();

                    // Obtener los atributos del producto
                    String link = element.attr( "href" );
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
            
            // Si hay varias paginas, nos conectamos a la que toque
            if ( ! pagesElements.isEmpty() )
            {
                if ( i < pages.size() )
                {
                    document = Jsoup.connect( pages.get( i ) ).timeout( Properties.TIMEOUT ).get();

                    // Sacamos nuevas paginas si las hay
                    pagesElements = document.select( "ul.pagination__list li.pagination__list-item a" );
                    for ( Element page : pagesElements )
                    {
                        // Anadimos solo las que no esten ya
                        if ( ! pages.contains( page.attr( "href" ).concat( "&format=ajax" ) ) && 
                           ( ! page.ownText().equals( "1" ) ) )
                        {
                            pages.add( page.attr( "href" ).concat( "&format=ajax" ) );
                            System.out.println( page.attr( "href" ).concat( "&format=ajax" ) );
                        }
                    }
                }
                
                finished = ( ++i > pages.size() );
                
            } else 
                finished = true;
            
        } // while
        
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
        
        System.out.println(productList.size());
        
    }
    
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
}
