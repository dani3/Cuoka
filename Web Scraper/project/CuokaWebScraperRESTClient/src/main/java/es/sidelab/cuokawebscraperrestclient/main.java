package es.sidelab.cuokawebscraperrestclient;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class main 
{
    private static final String URL = "http://spf.com/es/tienda/man/abrigos";
    
    public static void main( String[] args ) 
    {
        try {
            // Obtener el HTML
            Document document = Jsoup.connect( URL ).get();
            // Obtener el link de 'Ver todos'
            Element page = document.select( "div.pagination a" ).last();
            // Obtener el nuevo HTML con todos los productos
            document = Jsoup.connect( "http://spf.com" + page.attr( "href" ) ).get();
            
            // Obtener el campo info de todos los productos
            Elements products = document.select( "ul.product-listing li div div.product-main-info a" );
            
            for ( Element product : products )
                System.out.println( product.attr( "href" ) );
            
        } catch ( IOException ex ) {
            Logger.getLogger( main.class.getName()).log(Level.SEVERE, null, ex );
        }
    }
    
}
