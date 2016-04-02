package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.Printer;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class mainHyM 
{    
    public static void main(String[] args) throws Exception 
    {        
        String url = "http://www2.hm.com/";
        String path = "C:\\Users\\Dani\\Documents\\shops\\HyM_true\\true\\";
        String sectionName = "Americanas";
        List<Product> productList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks( path + sectionName + ".html" , url );
            
        for ( String productLink : productsLink )
        {
            String pathProduct = "C:\\Users\\Dani\\Documents\\shops\\HyM_true\\true\\Americanas_PRODUCTO.html";
            
            File file = PythonManager.executeRenderProduct( productLink, path, pathProduct );
            
            Document document = Jsoup.parse( file, "UTF-8" );
            
            // Obtener los atributos propios del producto
            String link = productLink;
            String name = document.select( "h1.product-item-headline" ).first().ownText(); 
            String price = document.select( "div.product-item-price span" ).first().ownText().replaceAll( "\\u20AC", "" ).replaceAll( ",", "." ).trim();
            String reference = productLink.substring( productLink.indexOf( "." ) + 1 , productLink.lastIndexOf( "." ) );
            String description = document.select( "p.product-detail-description-text" ).first().ownText().replaceAll( "\n", " " );
            
            Printer.print(price);
            
            if ( description.length() > 255 )
                description = description.substring(0, 255);
            
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

                    List<Image> imagesURL = new ArrayList<>();
                    Elements images = document.select( "div.product-detail-thumbnails li img" );
                    for ( Element img : images )
                        imagesURL.add( new Image( fixURL( img.attr( "src" ).replaceAll( "/product/thumb" , "/product/main" ) ) ) );
                                                 
                    variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
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
            
            // CRUCIAL llamar al recolector de basura
            System.gc();
                
            PythonManager.deleteFile( pathProduct );
            
        } // for products
        
        Product p = productList.get( 2 );
        
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
    
    private static String fixURL( String url )
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
                  
        Elements products = document.select( "h3.product-item-headline a" );
        
        for( Element element : products )
        {
            links.add( fixURL( shopUrl + element.attr( "href" ) ) );
        }
        
        return links;
    }
}
