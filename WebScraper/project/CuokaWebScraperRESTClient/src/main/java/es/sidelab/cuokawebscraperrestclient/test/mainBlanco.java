package es.sidelab.cuokawebscraperrestclient.test;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.FileManager;
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

public class mainBlanco 
{
    public static void main(String[] args) throws Exception 
    {        
        String url = "https://www.blanco.com/";
        Section section = new Section( "Camisas", "C:\\Users\\Dani\\Documents\\shops\\Blanco_true\\false\\", false );
        List<Product> productList = new ArrayList<>();
        List<String> productMultiColorList = new ArrayList<>();
        
        List<String> productsLink = getListOfLinks( section.getPath() + section.getName() + ".html" , url );
            
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
            List<ColorVariant> variants = new ArrayList<>();
            List<Image> imagesURL = new ArrayList<>();
            
            // Hay dos product-color-selector repetidos, nos quedamos solo con uno
            Element colorList = document.select( "div.product-color-selector" ).first();            
            Element color = colorList.select( "span" ).first();

            // Si este producto tiene varios colores, lo anadimos a la lista para tenerlo guardado.
            if ( colorList.select( "span" ).size() > 1 )
            {
                for ( int i = 0; i < colorList.select( "span" ).size(); i++ )
                {
                    productMultiColorList.add( productLink );
                }
            }
            
            String colorName = color.ownText().toUpperCase();

            Elements images = document.select( "#product-gallery-list img" );
            for ( Element img : images ) 
            {
                imagesURL.add( new Image( fixURL( url + img.attr( "src" ) ) ) );
            }

            variants.add( new ColorVariant( reference, colorName, null, imagesURL ) );         

            productList.add( new Product( Double.parseDouble( price )
                                    , name
                                    , ""
                                    , ""
                                    , link 
                                    , description
                                    , true
                                    , variants ) );            
            
        } // for products
        
        // Escribimos los links de los productos con varios colores.
        FileManager.writeLinksToFile( productMultiColorList, section );
        // Ejecutamos el script que renderiza todos los productos
        PythonManager.executeRenderColors( section );
        
        int cont = 0;
        for( String multiColorProduct : productMultiColorList )
        {
            String pathProduct = section.getPath() + section.getName() + "_COLOR_" + cont + ".html";
            
            try 
            {
                File file = new File( pathProduct );
            
                // Esperamos a que python cree el fichero
                while ( ! file.exists() ) {}

                // Se realiza una espera de medio segundo para que no pillemos el fichero nada mas ser creado (estara vacio)
                Thread.sleep( 1000 );
                file = new File( pathProduct );
                
                Printer.print( "Scraping color: " + pathProduct );
            
                Document document = Jsoup.parse( file, "ISO-8859-1" );
                
                String reference = document.select( "p.product-number" ).first().ownText()
                                                                                .replaceAll( "[^0-9]", "" );
                
                // Hay dos product-color-selector repetidos, nos quedamos solo con uno
                Element colorList = document.select( "div.product-color-selector" ).first();            
                Element color = colorList.select( "span" ).first();
                
                String colorName = color.ownText().toUpperCase();

                Elements images = document.select( "#product-gallery-list img" );
                List<Image> imagesURL = new ArrayList<>();
                for ( Element img : images ) 
                {
                    imagesURL.add( new Image( fixURL( url + img.attr( "src" ) ) ) );
                }
                
                ColorVariant cv = new ColorVariant( reference, colorName, null, imagesURL );
                
                int pos = containsProduct( productList, reference );
                if( pos >= 0 )
                    productList.get( pos ).getColors().add( cv );
                   
                else 
                    throw new Exception( "Product not found (" + reference + ")" );
               
                
            } catch( Exception e ) {
                e.printStackTrace();
                
            } finally {
                cont++;
                
            }
            
        } // for multiColor
        
        Printer.print( Integer.toString( productList.size() ) );
        
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
    
    private static int containsProduct( List<Product> productList, String reference )
    {
        for ( int i = 0; i < productList.size(); i++ )
            for ( ColorVariant cv : productList.get( i ).getColors() )
                if ( cv.getReference().equals( reference ) )
                    return i;
        
        return -1;
    }
}
