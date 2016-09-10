package es.sidelab.cuokawebscraperrestclient.scrapers;

import es.sidelab.cuokawebscraperrestclient.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestclient.beans.Image;
import es.sidelab.cuokawebscraperrestclient.beans.Product;
import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import es.sidelab.cuokawebscraperrestclient.utils.ActivityStatsManager;
import es.sidelab.cuokawebscraperrestclient.utils.FileManager;
import es.sidelab.cuokawebscraperrestclient.utils.PythonManager;
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
 * Scraper especifico de Zara.
 * @author Daniel Mancebo Aldea
 */

public class ZaraScraper implements Scraper 
{
    private static final Logger LOG = Logger.getLogger( ZaraScraper.class );
    
    // Lista preparada para la concurrencia donde escribiran todos los scrapers
    private static List<Product> productList = new CopyOnWriteArrayList<>();
    
    @Override
    public List<Product> scrap( Shop shop, Section section ) throws IOException
    {        
        int prodOK = 0;
        int prodNOK = 0;
        
        int cont = 0;
        
        // Lista con los links de cada producto
        String htmlPath = section.getPath() + section.getName() + ".html";
        // Sacamos los links de cada producto
        List<String> productsLink = getListOfLinks( htmlPath, shop.getURL().toString() );
        
        // Escribimos en fichero todos los links de la seccion
        FileManager.writeLinksToFile( productsLink, section );
        // Ejecutamos el script que renderiza todos los productos
        PythonManager.executeRenderProducts( section );
          
        // Recorremos todos los productos y sacamos sus atributos
        for ( String productLink : productsLink )
        {        
            String pathProduct = section.getPath() + section.getName() + "_" + cont + ".html";
            
            try 
            {               
                File file = new File( pathProduct );
            
                // Esperamos a que python cree el fichero
                while ( ! file.exists() ) {}

                // Se realiza una espera de medio segundo para que no pillemos el fichero nada mas ser creado (estara vacio)
                Thread.sleep( 1000 );
                file = new File( pathProduct );
            
                LOG.info( "Scraping: " + pathProduct );
                
                Document document = Jsoup.parse( file, "ISO-8859-1" );
                
                // Obtener los atributos propios del producto
                String link = productLink;                 
                // El nombre se pasa a mayusculas
                String name = document.select( "div header > h1" ).first().ownText()
                                                                          .replaceAll( "\\\\[nt]", "" )
                                                                          .toUpperCase(); 
                // Del precio solo nos quedamos con los numeros
                String price = document.select( "div.price span" ).first().ownText()
                                                                          .replaceAll( "[^,.0-9]", "" )
                                                                          .replaceAll( ",", "." );
                // De la referencia eliminamos todo lo que no sean numeros
                String reference = document.select( "div.right p.reference" ).first().ownText()
                                                                                     .replaceAll( "[^0-9]", "" )
                                                                                     .replaceAll( "\\\\[nt]", "" );
                // En la descripcion eliminamos los saltos de linea y las tabulaciones
                String description = document.select( "#description p.description span" ).first().ownText()
                                                                                                 .replaceAll( "\\\\[nt]", "" ); 
               
                // En BD no podemos guardar un string de mas de 255 caracteres, si es mas grande lo acortamos
                if ( description.length() > 255 )
                    description = description.substring( 0, 255 );
                
                String colorReference = reference;
                // Se eliminan primero los espacios y luego se sustituye la barra por un espacio
                String colorName = document.select( "span.color-description" ).first().ownText().toUpperCase()
                                                                                                .trim()
                                                                                                .replaceAll( "/" , " " );
                
                List<ColorVariant> variants = new ArrayList<>();
                
                List<Image> imagesURL = new ArrayList<>();
                Elements images = document.select( "#main-images div.media-wrap" );
                for ( Element img : images ) 
                {
                    String imageURL = fixURL( img.select( "img" ).first().attr( "src" ) );

                    imagesURL.add( new Image( imageURL ) );
                }

                variants.add( new ColorVariant( colorReference, colorName, null, imagesURL ) );   
                
                if ( ! variants.isEmpty() )
                {
                    productList.add( new Product( Double.parseDouble( price )
                                        , name
                                        , shop.getName()
                                        , section.getName()
                                        , link 
                                        , description
                                        , section.isMan()
                                        , variants ) );
                    prodOK++;
                }
                else
                    prodNOK++;           
                
            } catch ( Exception e ) { 
                LOG.error( "Excepcion en producto: " + pathProduct + " (" + e.toString() + ")" );
                
                prodNOK++; 
                
            } finally {
                cont++;
                
            }
            
        } // for products
        
        System.gc();
        
        // Borramos todos los htmls de la seccion
        for ( int i = 0; i < productsLink.size(); i++ )
        {
            FileManager.deleteFile( section.getPath() + section.getName() + "_" + i + ".html" );
        }
        
        // Borramos el fichero de links
        FileManager.deleteFile( section.getPath() + section.getName() + "_LINKS.txt" );
        
        ActivityStatsManager.updateProducts( shop.getName(), section, prodOK, prodNOK );  
        
        return productList;
    }
    
    @Override
    public String fixURL( String url )
    {
        if ( url.startsWith( "//" ) )
            return "http:".concat( url ).replace( " " , "%20" );
        
        return url;
    }   
    
    @Override
    public List<String> getListOfLinks( String htmlPath, String shopUrl ) throws IOException
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
