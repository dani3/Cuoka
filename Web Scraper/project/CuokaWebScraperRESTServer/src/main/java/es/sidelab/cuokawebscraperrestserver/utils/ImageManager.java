package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    /*
     * Metodo que descarga la imagen del producto y le baja la resolucion a 350x500
     */
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
                        
        } catch ( MalformedURLException ex ) {
            LOG.error( "ERROR: Error al formar la URL de la imagen" );
            LOG.error( ex.getMessage() );
            
            return null;
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error en la conexion" );
            LOG.error( ex.getMessage() );
            
            return null;
        }
                        
        return ( "/images/" + product.getShop() + "/" + imageName.getName() + ".jpg" );
    }
    
    /*
     * Metodo que elimina las imagenes originales de una tienda para que no ocupen espacio
     */
    public static void deleteOriginalImages( String shop )
    {
        try 
        {
            File folder = new File( Properties.IMAGE_PATH + shop );
            
            // Eliminamos las imagenes antiguas
            FileUtils.cleanDirectory( folder );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error la eliminas las imagenes" );
            LOG.error( ex.getMessage() );
        }    
    }
    
    /*
     * Metodo que borra todas las imagenes guardadas de una tienda.
     */
    public static void deleteProducts( String shop )
    {
        try 
        {
            File folder = new File( Properties.IMAGE_PATH + shop );
            File folderResized = new File( Properties.RESIZED_IMAGE_PATH + shop );
            
            // Comprobamos si existe el directorio, si no, se crea
            if ( ! folder.exists())
                folder.mkdirs();
            
            if ( ! folderResized.exists())
                folderResized.mkdirs();
            
            // Eliminamos las imagenes antiguas
            FileUtils.cleanDirectory( folder );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error la eliminas las imagenes" );
            LOG.error( ex.getMessage() );
        }        
    }
    
    /*
     * Metodo que ejecuta un script hecho en python que reescala todas las imagenes de una tienda
     */
    public static void resizeImages( String shop )
    {
        try {
            Runtime.getRuntime().exec( new String[]{"sudo", "/usr/bin/python", "resize.py", shop} );
            
        } catch ( IOException ex ) {
            Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * Metodo que cambia la resolucion de la imagen a 350x500
     */
    private static void resizeImage( String imagePath ) throws IOException
    {     
        // Creamos una BufferedImage donde guardamos la imagen original
        BufferedImage original = ImageIO.read( new FileInputStream( imagePath ) );
        
        // Creamos una Image reescalando la original y una BufferedImage
        Image resized = original.getScaledInstance( Properties.IMAGE_WIDTH
                                        , Properties.IMAGE_HEIGHT
                                        , Image.SCALE_FAST );       
        BufferedImage bImgResized = new BufferedImage( Properties.IMAGE_WIDTH
                                            , Properties.IMAGE_HEIGHT
                                            , original.getType() );       
        
        // Utilizamos Graphics2D para guardar la imagen reescalada (Image) en un BufferedImage
        Graphics2D bGr = bImgResized.createGraphics();
        bGr.drawImage( resized, 0 , 0, null );
        bGr.dispose();
        
        // Guardamos en fichero la imagen reescalada con el mismo nombre
        ImageIO.write( bImgResized, "jpg", new FileOutputStream( imagePath ) );
    }
}
