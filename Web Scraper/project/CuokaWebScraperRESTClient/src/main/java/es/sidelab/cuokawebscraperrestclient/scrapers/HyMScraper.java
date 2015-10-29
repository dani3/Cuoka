package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @class Scraper especifico para HyM, la pagina esta desarrollada con AJAX
 * @author Daniel Mancebo
 */

public class HyMScraper implements GenericScraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException 
    {      
        // Obtener el HTML
        Document document = Jsoup.connect( section.getURL().toString() )
                                    .timeout( Properties.TIMEOUT ).get();
          
        // Obtener los links a todos los productos
        Elements elements = document.select( "h3.product-item-headline > a" );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( Element element : elements )
        {
            // Obtener el HTML del producto
            document = Jsoup.connect( shop.getURL().toString()
                            + element.attr( "href" ) ).timeout( Properties.TIMEOUT )
                                                      .ignoreHttpErrors( true ).get();

            // Obtener los atributos propios del producto
            String link = shop.getURL().toString() + element.attr( "href" );
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
                    String colorLink = shop.getURL().toString() + "es_es/productpage." + color.select( "input" ).attr( "data-articlecode" ) + ".html";
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
                    
                productList.add( new Product( reference
                                    , Double.parseDouble( price )
                                    , name
                                    , shop.getName()
                                    , section.getName()
                                    , link 
                                    , section.isMan()
                                    , variants ) );
            }
        }
            
        return productList;
    }
    
    /*
     * Metodo que arregla la URL, aÃ±ade el protocolo si no esta presente, y codifica los espacios
     */
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url.replace( " " , "%20" );
    }    
    
    /*
     * Metodo que devuelve true si el producto esta ya en la lista
     */
    private boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
