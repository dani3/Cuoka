package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.utils.FileManager;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Daniel Mancebo Aldea
 */

public class mainZara 
{    
    public static void main(String[] args) throws Exception 
    {
        String url = "http://www.zara.com/es/";
        String sectionName = "Vestidos";
        String path = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\false\\";
        Section section = new Section( sectionName, path, true );
        List<Product> productList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks( path + sectionName + ".html" , url );
        
        // Escribimos en fichero todos los links de la seccion
        FileManager.writeLinksToFile( productsLink, section );
        // Ejecutamos el script que renderiza todos los productos
        PythonManager.executeRenderProducts( section );
        
        int cont = 0;
        for ( String productLink : productsLink )
        {        
            String pathProduct = "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\true\\Vestidos_" + cont + ".html";
            
            try 
            {               
                List<ColorVariant> variants = new ArrayList<>();               
                File file = new File( pathProduct );

                while ( ! file.exists() ) {}

                Thread.sleep( 500 );
                file = new File( pathProduct );

                Document document = Jsoup.parse( file, "UTF-8" );
                
                // Obtener los atributos propios del producto
                String link = productLink;                 
                String name = document.select( "div header > h1" ).first().ownText()
                                                                          .replaceAll( "\\\\[nt]", "" )
                                                                          .toUpperCase(); 
                String price = document.select( "div.price span" ).first().ownText()
                                                                          .replaceAll( "[^,.0-9]", "" )
                                                                          .replaceAll( ",", "." );
                String reference = document.select( "div.right p.reference" ).first().ownText()
                                                                                     .replaceAll( "[^0-9]", "" )
                                                                                     .replaceAll( "\\\\[nt]", "" );
                String description = document.select( "#description p.description span" ).first().ownText()
                                                                                                 .replaceAll( "\\\\[nt]", "" ); 
               
                // En BD no podemos guardar un string de mas de 255 caracteres, si es mas grande lo acortamos
                if ( description.length() > 255 )
                    description = description.substring( 0, 255 );
                
                String colorReference = reference;
                String colorName = document.select( "span.color-description" ).first().ownText().toUpperCase()
                                                                                                .trim()
                                                                                                .replaceAll( "/" , " " );
                
                System.out.println(colorName);
                
                List<Image> imagesURL = new ArrayList<>();
                Elements images = document.select( "#main-images div.media-wrap" );
                for ( Element img : images ) 
                {
                    String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) );

                    imagesURL.add( new Image( imageURL ) );
                }

                variants.add( new ColorVariant( colorReference, colorName, null, imagesURL ) );   
                
                productList.add( new Product( Double.parseDouble( price )
                                            , name
                                            , ""
                                            , ""
                                            , link 
                                            , description
                                            , true
                                            , variants ) );            
                
            } catch ( Exception e ) { 
                e.printStackTrace(); 
                
            } finally {
                
                cont++;
            }
            
        } // for products
        
        System.gc();
        for ( int i = 0; i < productsLink.size(); i++ )
        {
            FileManager.deleteFile( "C:\\Users\\Dani\\Documents\\shops\\Zara_true\\true\\Vestidos_" + cont + ".html" );
        }
        
        // Borramos el fichero de links
        FileManager.deleteFile( section.getPath() + section.getName() + "_LINKS.txt" );
        
        System.out.println( productList.size() );
        
        Product p = productList.get( 0 );
        
        System.out.println( "-------- INFO PRODUCTO ----------" );
        System.out.println( "Nombre: " + p.getName() );
        System.out.println( "Link: " + p.getLink() );
        System.out.println( "Description: " + p.getDescription());
        System.out.println( "Precio: " + p.getPrice() );
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
    
    private static List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException
    {
        List<String> links = new ArrayList<>();        
        
        File html = new File( htmlPath);
        Document document = Jsoup.parse( html, "UTF-8" );
                  
        Elements products = document.select( "a.item" );
        
        for( Element element : products )
        {
            links.add( fixURL( element.attr( "href" ) ) );
        }
        
        return links;
    }
}
