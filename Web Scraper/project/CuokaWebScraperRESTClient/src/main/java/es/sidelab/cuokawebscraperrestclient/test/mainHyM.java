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

public class mainHyM {

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
                                    , true
                                    , variants ) );
            }
            
        } // for products
        
        Product p = productList.get( 1 );
        
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
            
            System.out.println( "" );            
        }
        
    }
    
    public static String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }     
}
