package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import es.sidelab.cuokawebscraperrestclient.utils.ActivityStatsManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @class Scraper especifico para HyM.
 * @author Daniel Mancebo Aldea
 */

public class HyMScraper implements Scraper
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException 
    {    
        // Lista con los links de cada producto
        String htmlPath = section.getPath() + section.getName() + ".html";
        List<String> productsLink = getListOfLinks( htmlPath, shop.getURL().toString() );
        
        int prodOK = 0;
        int prodNOK = 0;
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( String productLink : productsLink )
        {
            try 
            {
                // Obtener el HTML del producto
                Document document = Jsoup.connect( productLink ).timeout( Properties.TIMEOUT )
                                                                .header( "Accept-Language", "es" )
                                                                .ignoreHttpErrors( true ).get();

                // Obtener los atributos propios del producto
                String link = productLink;
                String name = document.select( "h1.product-item-headline" ).first().ownText().toUpperCase(); 
                String price = document.select( "div.product-item-price span" ).first().ownText().replaceAll( "€", "" ).replaceAll( ",", "." ).trim();
                String reference = productLink.substring( productLink.indexOf( "." ) + 1 , productLink.lastIndexOf( "." ) );
                String description = document.select( "p.product-detail-description-text" ).first().ownText().replaceAll( "\n", " " );
                
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
                        String colorLink = shop.getURL().toString() + "es_es/productpage." + color.select( "input" ).attr( "data-articlecode" ) + ".html";
                        document = Jsoup.connect( colorLink ).timeout( Properties.TIMEOUT )
                                                             .ignoreHttpErrors( true ).get();

                        String colorReference = color.select( "input" ).attr( "data-articlecode" );
                        String colorName = color.attr( "title" ).toUpperCase().replaceAll( "/" , " " );
                        // Esto no produce excepcion, se puede enviar la URL como null
                        String colorURL = fixURL( color.select( "div img" ).attr( "src" ) );

                        // Sacamos las imagenes, solo sacar la URL de las miniaturas, asi que tenemos
                        // que cambiar en la URL thumb por main para sacar la imagen grande
                        List<Image> imagesURL = new ArrayList<>();
                        Elements images = document.select( "div.product-detail-thumbnails li img" );
                        for ( Element img : images )
                            imagesURL.add( new Image( fixURL( img.attr( "src" ).replaceAll( "/product/thumb" , "/product/main" ) ) ) );

                        variants.add( new ColorVariant( colorReference, colorName, colorURL, imagesURL ) );
                    
                    }
                    
                    if ( ! colors.isEmpty() )
                    {    
                        prodOK++;
                        productList.add( new Product( Double.parseDouble( price )
                                            , name
                                            , shop.getName()
                                            , section.getName()
                                            , link 
                                            , description
                                            , section.isMan()
                                            , variants ) );
                    }
                    else
                        prodNOK++;
                    
                } 
                
            } catch ( Exception e ) { prodNOK++; }
        }
        
        ActivityStatsManager.updateProducts(shop.getName(), section, prodOK, prodNOK ); 
        
        return productList;
    }
    
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url.replace( " " , "%20" );
    }    
    
    @Override
    public List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException
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
    
    private boolean containsProduct( List<Product> productList, String reference )
    {
        for ( Product p : productList )
            for ( ColorVariant cv : p.getColors() )
                if ( cv.getReference().equals( reference ) )
                    return true;
        
        return false;
    }
}
