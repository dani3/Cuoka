package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @class Clase que gestionara todo lo relacionado con las imagenes de los productos
 * @author Daniel Mancebo Aldea
 */

public class ImageManager 
{
    private static final Log LOG = LogFactory.getLog( ImageManager.class );
    
    /*
     * Metodo que descarga si es necesario las imagenes del producto y los iconos de los colores
     */
    public static List<Product> downloadImages( List<Product> products, String shop )
    {
        List<Product> productsUpdated = new ArrayList<>();
        
        // Creamos los directorios si es necesario
        FileManager.createProductsDirectory( shop );
        
        for ( int i = 0; i < products.size(); i++ ) 
        {
            Product product = products.get( i );
            
            for ( int j = 0; j < product.getColors().size(); j++ )
            {
                ColorVariant cv = product.getColors().get( j );
                
                // Descargar las imagenes si es necesario
                if ( cv.getImages() != null )
                {
                    for ( int k = 0; k < cv.getImages().size(); k++ )
                    {
                        String path = Properties.IMAGE_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName() + "_" + k + ".jpg";
                        String pathSmall = Properties.IMAGE_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName() + "_" + k + "_" + "Small.jpg";
                        String pathLarge = Properties.IMAGE_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName() + "_" + k + "_" + "Large.jpg";
                        LOG.info( "Comprobando la imagen: " + path );

                        if ( ! FileManager.existsFile( pathSmall ) )
                        {
                            LOG.info( "La imagen no existe, descargando" );
                            boolean ok = downloadImage( cv.getImages().get( k ).getUrl(), path );

                            if ( ok )
                            {
                                LOG.info( "Imagen descargada correctamente" );
                                product.getColors().get( j )
                                        .getImages().get( k ).setPathLargeSize( pathLarge );
                                product.getColors().get( j )
                                        .getImages().get( k ).setPathSmallSize( pathSmall );
                            } 
                            
                        } else {
                            LOG.info( "La imagen ya existe" );
                            product.getColors().get( j )
                                        .getImages().get( k ).setPathLargeSize( pathLarge );
                            product.getColors().get( j )
                                        .getImages().get( k ).setPathSmallSize( pathSmall );                            
                        }   
                        
                    } // for images
                } // if images != null
                
                // Descargar los iconos si es necesario
                String path = Properties.COLOR_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName().replaceAll( " " , "_" ) + "_ICON.jpg";
                if ( ! FileManager.existsFile( path ) )
                {
                    boolean ok = downloadImage( cv.getColorURL(), path );
                    if ( ok )
                        product.getColors().get( j ).setColorPath( path );
                } else
                    product.getColors().get( j ).setColorPath( path );
                
            } // for colors      
            
            productsUpdated.add( product );
            
        } // for products
        
        LOG.info( "Todas las imagenes se han descargado correctamente, se reescalan" );
        resizeImages( shop );        
        
        LOG.info( "Se reescalan los iconos de los colores" );
        resizeColors( shop );        
        
        return productsUpdated;
    }
    
    /*
     * Metodo que descarga la imagen del producto y le baja la resolucion a 350x500
     */
    private static boolean downloadImage( String imageURL, String path )
    {
        InputStream in = null;
        ByteArrayOutputStream out = null;
        FileOutputStream fos = null;
                
        try 
        {            
            URL url = new URL( imageURL );
            
            in = new BufferedInputStream( url.openStream() );
            
            out = new ByteArrayOutputStream();
            byte[] buffer = new byte[ 1024 ];

            int i = 0;
            while( ( i = in.read( buffer ) ) != -1 )
                out.write( buffer, 0, i );

            fos = new FileOutputStream( path );
            fos.write( out.toByteArray() ); 

            fos.close();
            out.close();
            in.close();
                        
        } catch ( MalformedURLException ex ) {
            LOG.error( "ERROR: Error al formar la URL de la imagen" );
            LOG.error( ex.getMessage() );
            
            return false;
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error en la conexion" );
            LOG.error( ex.getMessage() );
            
            return false;
        }
                        
        return true;
    }
    
    /*
     * Metodo que ejecuta un script en python para reescalar las imagenes de una tienda
     */
    private static void resizeImages( String shop )
    {
        try 
        {                      
            Runtime.getRuntime().exec( new String[]{ "sudo"
                        , "/usr/bin/python"
                        , "resizeProducts.py"
                        , Properties.IMAGE_PATH + shop + "/"
                        , Integer.toString( Properties.IMAGE_WIDTH_L )
                        , Integer.toString( Properties.IMAGE_HEIGHT_L )
                        , Integer.toString( Properties.IMAGE_WIDTH_S )
                        , Integer.toString( Properties.IMAGE_HEIGHT_S ) } );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error al ejecutar el script 'resizeProducts.py'" );
            LOG.error( ex.getMessage() );
            
        }
    }
    
    /*
     * Metodo que ejecuta un script en python que reescala todos los iconos de los colores
     */
    private static void resizeColors( String shop )
    {
        try 
        {            
            Runtime.getRuntime().exec( new String[]{ "sudo"
                        , "/usr/bin/python"
                        , "resizeColors.py"
                        , Properties.COLOR_PATH + shop + "/"
                        , Integer.toString( Properties.ICON_WIDTH )
                        , Integer.toString( Properties.ICON_HEIGHT ) } );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error al ejecutar el script 'resizeColors.py'" );
            LOG.error( ex.getMessage() );
            
        }
    }
    
    private static boolean checkConnectivity( URL url )
    {
        try {
            HttpURLConnection urlConn = ( HttpURLConnection ) url.openConnection();
            
            int i = 0;
            while ( i++ < 5 )
            {
                urlConn.connect();
                
                if ( ( HttpURLConnection.HTTP_OK == urlConn.getResponseCode() ) )
                    return true;
            }
            
        } catch ( IOException e ) {
            LOG.info( e.getMessage() );
        }  
        
        return false;
    }
    
    /*
     * Metodo que cambia la resolucion de la imagen a 350x500
     */
    /*private static void resizeImage( String imagePath ) throws IOException
    {     
        // Creamos una BufferedImage donde guardamos la imagen original
        BufferedImage original = ImageIO.read( new FileInputStream( imagePath ) );
        
        // Creamos una Image reescalando la original y una BufferedImage
        Image resized = original.getScaledInstance( Properties.IMAGE_WIDTH_S
                                        , Properties.IMAGE_HEIGHT_S
                                        , Image.SCALE_FAST );       
        BufferedImage bImgResized = new BufferedImage( Properties.IMAGE_WIDTH_S
                                            , Properties.IMAGE_HEIGHT_S
                                            , original.getType() );       
        
        // Utilizamos Graphics2D para guardar la imagen reescalada (Image) en un BufferedImage
        Graphics2D bGr = bImgResized.createGraphics();
        bGr.drawImage( resized, 0 , 0, null );
        bGr.dispose();
        
        // Guardamos en fichero la imagen reescalada con el mismo nombre
        ImageIO.write( bImgResized, "jpg", new FileOutputStream( imagePath ) );
    }*/
}
