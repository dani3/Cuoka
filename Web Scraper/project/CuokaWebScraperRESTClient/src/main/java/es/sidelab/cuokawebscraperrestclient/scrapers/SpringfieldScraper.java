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
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @class Scraper especifico para Springfield
 * @author Daniel Mancebo Aldea
 */

public class SpringfieldScraper implements Scraper 
{
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    private static final Logger LOG = Logger.getLogger( SpringfieldScraper.class );
    
    @Override
    public List<Product> scrap( Shop shop, Section section, String htmlPath ) throws IOException
    {        
        List<String> pages = new ArrayList<>();
        
        int prodOK = 0;
        int prodNOK = 0;
        
        boolean finished = false;
        int i = 0;
        
        File html = new File( htmlPath );
        
        // Parseamos el html producido por python
        Document document = Jsoup.parse( html, "UTF-8" );
        
        // Nos quedamos con las paginas si las hay
        Elements pagesElements = document.select( "ul.pagination__list li.pagination__list-item a" );
        
        // Si hay varias paginas...
        for ( Element page : pagesElements )
            pages.add( page.attr( "href" ).concat( "&format=ajax" ) );
            
        // Si solo hay una pagina, solo se iterara una vez
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
                            for ( Element img : images )
                                imagesURL.add( new Image( img.select( "img" ).first().attr( "src" ) ) );                            

                            // Añadimos un nuevo ColorVariant a la lista 
                            variants.add( new ColorVariant( reference, colorName, colorURL, imagesURL ) );
                        }

                        if ( ! colors.isEmpty() )
                        {
                            productList.add( new Product( Double.parseDouble( price )
                                                , name
                                                , shop.getName()
                                                , section.getName()
                                                , link 
                                                , section.isMan()
                                                , variants ) );
                            prodOK++;
                        }
                        else
                            prodNOK++;
                    }

                } catch ( Exception e ) {prodNOK++;}

            } // for products
            
            // Si hay varias paginas, nos conectamos a la que toque
            if ( ! pagesElements.isEmpty() )
            {
                document = Jsoup.connect( pages.get( i++ ) ).timeout( Properties.TIMEOUT ).get();
                
                // Sacamos nuevas paginas si las hay
                pagesElements = document.select( "ul.pagination__list li.pagination__list-item a" );
                for ( Element page : pagesElements )
                {
                    // Anadimos solo las que no esten ya, siempre que no sea la primera
                    if ( ! pages.contains( page.attr( "href" ).concat( "&format=ajax" ) ) && 
                       ( ! page.ownText().equals( "1" ) ) )
                    {
                        pages.add( page.attr( "href" ).concat( "&format=ajax" ) );
                    }
                }
                
                finished = ( i >= pages.size() );
                
            } else 
                finished = true;
            
        } // while
        LOG.info("prodOK: " + prodOK);
        LOG.info("prodNOK: " + prodNOK);
        ActivityStatsManager.updateProducts(shop.getName(), section, prodOK, prodNOK );   
        return productList;
    }
    
    /*
     * Metodo que arregla la URL, añade el protocolo si no esta presente, y codifica los espacios
     */
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
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
