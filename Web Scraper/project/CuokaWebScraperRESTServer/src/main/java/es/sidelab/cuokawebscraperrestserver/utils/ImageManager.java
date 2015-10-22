package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.imageio.ImageIO;
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
        
        File folder = new File( Properties.IMAGE_PATH + product.getShop() );
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
            
            fos = new FileOutputStream( Properties.IMAGE_PATH 
                    + product.getShop() + "/" + imageName.getName() + ".jpg" );
            fos.write( out.toByteArray() ); 
            
            fos.close();
            out.close();
            in.close();
            
            resizeImage( Properties.IMAGE_PATH 
                    + product.getShop() + "/" + imageName.getName() + ".jpg" );
                        
        } catch ( MalformedURLException ex ) {
            LOG.info( "ERROR: Error al formar la URL de la imagen" );
            LOG.info( ex.getMessage() );
            
            return null;
            
        } catch ( IOException ex ) {
            LOG.info( "ERROR: Error en la conexion" );
            LOG.info( ex.getMessage() );
            
            return null;
        }
                        
        return ( folder.getName() + "/" + imageName.getName() );
    }
    
    public static void deleteProducts( String shop )
    {
        try 
        {
            File folder = new File( Properties.IMAGE_PATH + shop );
            
            // Comprobamos si existe el directorio, si no, se crea
            if ( ! folder.exists())
                folder.mkdirs();
            
            // Eliminamos las imagenes antiguas
            FileUtils.cleanDirectory( folder );
            
        } catch ( IOException ex ) {
            
        }        
    }
    
    private static void resizeImage( String imagePath ) throws IOException
    {        
        BufferedImage original = ImageIO.read( new FileInputStream( imagePath ) );
        
        int newWidth = 350;
        int newHeight = 500;
        
        Image resized = original.getScaledInstance( newWidth, newHeight, Image.SCALE_FAST );       
        BufferedImage bImgResized = new BufferedImage( newWidth, newHeight, original.getType() );       
        
        Graphics2D bGr = bImgResized.createGraphics();
        bGr.drawImage( resized, 0 , 0, null );
        bGr.dispose();
        
        ImageIO.write( bImgResized, "jpg", new FileOutputStream( imagePath ) );
    }
}
