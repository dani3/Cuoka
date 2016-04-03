package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.Printer;
import java.io.File;
import java.io.IOException;
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
        String url = "https://www.blanco.com/";
        String path = "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\";
        String sectionName = "Camisas";
        List<Product> productList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks( path + sectionName + ".html" , url );
            
        for ( String productLink : productsLink )
        {
            Document document = Jsoup.connect( productLink )
                                     .header( "Accept-Language", "es" )
                                     .timeout( Properties.TIMEOUT )
                                     .ignoreHttpErrors( true ).get();
            
            // Obtener todos los atributos propios del producto
            String link = productLink;
            String name = document.select( "h1.product-name" ).first().ownText()
                                                                      .toUpperCase(); 
            String reference = document.select( "p.product-number" ).first().ownText()
                                                                            .replaceAll( "[^0-9]", "" );
            String description = document.select( "p.product-description" ).first().ownText()
                                                                                   .replaceAll( "\n", " " );
            String price = document.select( "p.product-price" ).first().ownText()
                                                                       .replaceAll( "[^0-9]", "" );
            String decimals = document.select( "p.product-price small" ).first().ownText()
                                                                                .replaceAll( ",", "." )
                                                                                .trim();
            price = price + decimals;
            
            if ( description.length() > 255 )
                description = description.substring( 0, 255 );
            
            // Obtenemos los colores del producto
            boolean first = true;
            List<ColorVariant> variants = new ArrayList<>();
            
            // Hay dos product-color-selector repetidos, nos quedamos solo con uno
            Element colorList = document.select( "div.product-color-selector" ).first();            
            Elements colors = colorList.select( "span" );
            for ( Element color : colors )
            {
                List<Image> imagesURL = new ArrayList<>();
                
                String colorName = color.ownText().toUpperCase();
                
                // De Blanco no podemos acceder a las imagenes de los colores alternativos, solo las del color principal
                if ( first )
                {
                    Elements images = document.select( "#product-gallery-list img" );
                    for ( Element img : images )
                        imagesURL.add( new Image( fixURL( url + img.attr( "src" ) ) ) );                    
                    
                    first = false;
            
                    variants.add( new ColorVariant( reference, colorName, null, imagesURL ) );
                }
            }
            
            productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , ""
                                    , ""
                                    , link 
                                    , description
                                    , true
                                    , variants ) );
            
            
        } // for products
        
        Printer.print(Integer.toString(productList.size()));
        
        Product p = productList.get( 0 );
        
        System.out.println( "-------- INFO PRODUCTO ----------" );
        System.out.println( "Nombre: " + p.getName() );
        System.out.println( "Link: " + p.getLink() );
        System.out.println( "Descripcion: " + p.getDescription());
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
                  
        Elements products = document.select( "div.products-list a" );
        
        for( Element element : products )
        {
            links.add( fixURL( shopUrl + element.attr( "href" ) ) );
        }
        
        return links;
    }
}
