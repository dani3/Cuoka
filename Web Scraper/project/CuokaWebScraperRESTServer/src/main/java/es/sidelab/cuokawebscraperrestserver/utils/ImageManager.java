package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.tomcat.util.http.fileupload.FileUtils;

/**
 * @class Clase que gestionara todo lo relacionado con las imagenes de los productos
 * @author Daniel Mancebo Aldea
 */

public class ImageManager 
{
    private static final Log LOG = LogFactory.getLog( ImageManager.class );
    
    public static String downloadImageFromURL( Product product )
    {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        FileOutputStream fos = null;
        
        File folder = new File( System.getProperty( "user.dir" ) 
                + "/" + Properties.IMAGE_PATH + product.getShop() );
        File imageName = new File( product.getShop()
                            + "_" + product.getSection()
                            + "_" + product.getId() );        
        try 
        {            
            URL url = new URL( product.getImageURL() );
            in = new BufferedInputStream( url.openStream() );
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[ 1024 ];
            
            int i = 0;
            while( ( i = in.read( buffer ) ) != -1 )
                out.write( buffer, 0, i );
            
            fos = new FileOutputStream( System.getProperty( "user.dir" ) + "/" 
                    + Properties.IMAGE_PATH + product.getShop() + "/" + imageName.getName() + ".png" );
            fos.write( out.toByteArray() ); 
            
            fos.close();
            out.close();
            in.close();
                        
        } catch ( MalformedURLException ex ) {
            LOG.info( "ERROR: Error al formar la URL de la imagen" );
            LOG.info( ex.getMessage() );
            
            return null;
            
        } catch ( IOException ex ) {
            LOG.info( "ERROR: Error en la conexion" );
            LOG.info( ex.getMessage() );
            
            return null;
        }
        
        // No deberia llegar aqui                
        return ( folder.getName() + "/" + imageName.getName() );
    }
    
    public static void deleteProducts( String shop )
    {
        try 
        {
            File folder = new File( System.getProperty( "user.dir" ) + "/" + Properties.IMAGE_PATH + shop );
            
            // Comprobamos si existe el directorio, si no, se crea
            if ( ! folder.exists())
                folder.mkdirs();
            
            // Eliminamos las imagenes antiguas
            FileUtils.cleanDirectory( folder );
            
        } catch ( IOException ex ) {
            
        }        
    }
}
