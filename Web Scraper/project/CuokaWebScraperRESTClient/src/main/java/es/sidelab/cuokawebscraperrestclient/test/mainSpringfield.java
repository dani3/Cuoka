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
    
    public static void main(String[] args) throws Exception 
    {        
        // Lista preparada para la concurrencia donde escribiran todos los scrapers
        List<Product> productList = new ArrayList<>();
        List<String> pages = new ArrayList<>();
        
        // Añadimos la primera página
        pages.add( "http://myspringfield.com/es/es/man/camisas" );
        
        Document document = Jsoup.connect( "http://myspringfield.com/es/es/man/camisas" )
                                        .timeout( Properties.TIMEOUT ).get();    
        
        // Sacamos las páginas, si las hay
        Elements pageElements = document.select( "ul.pagination__list > li.pagination__list-item" );
        
        // En el caso de que haya paginas, las metemos en la lista
        if ( ! pageElements.isEmpty() )
        {
            pageElements.remove( pageElements.size() - 1 );
            for ( Element page : pageElements )
                if ( ! page.hasClass( "pagination__list-item--current" ) & ! page.hasClass( "first-last" ) )
                    if ( ! page.select("a").text().equals("") )
                        pages.add( page.select("a").attr("href").concat( "&format=ajax" ) );
        }
        
        for ( String page : pages )
        {
            // Obtener el HTML
            document = Jsoup.connect( page ).timeout( Properties.TIMEOUT ).get();           

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
                            String colorName = color.select("span.screen-reader-text").first().ownText().toUpperCase();
                            String colorURL = color.select( "span.c02__square" ).attr( "style" ).replace( "background: url(" , "").replace( ")", "" );                      

                            // Si hay varios colores, nos quedamos solo con las imagenes del color actual
                            Elements images = document.select( "div.c01--navdots div.c01__media" );

                            // Sacamos las URLs de las imagenes anteriores
                            List<Image> imagesURL = new ArrayList<>();
                            for ( Element img : images ){
                                imagesURL.add( new Image( img.select( "img" ).first().attr( "src" ) ) );
                                System.out.println( img.select( "img" ).first().attr( "src" ) );
                            }

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

                } catch ( Exception e ) {}

            } // for products
        } // for paginas
        
        Product p = productList.get( 5 );
        
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
    
}
