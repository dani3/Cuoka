package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.beans.ColorVariant;
import es.sidelab.cuokawebscraperrestserver.beans.Product;
import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

/**
 * @class Clase que gestionara todo lo relacionado con las imagenes de los productos.
 * @author Daniel Mancebo Aldea
 */

public class ImageManager 
{
    private static final Log LOG = LogFactory.getLog( ImageManager.class );
    
    /**
     * Metodo que descarga las imagenes de los productos, las deja en la ruta predefinida
     * y actualiza los paths de los productos.
     * @param products: Lista de productos de la que se quieren descargar las imagenes.
     * @param shop: Tienda a la que pertenecen los productos.
     * @return Lista de productos con los paths actualizados.
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
                        String path = Properties.PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName() + "_" + k + ".jpg";
                        String pathSmall = Properties.IMAGE_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName() + "_" + k + "_" + "Small.jpg";
                        LOG.info( "Comprobando la imagen: " + path );

                        if ( ! FileManager.existsFile( pathSmall ) )
                        {
                            LOG.info( "La imagen no existe, descargando" );
                            boolean ok = downloadImage( cv.getImages().get( k ).getUrl(), pathSmall.replaceAll( "_Small" , "" ) );

                            if ( ok )
                            {
                                LOG.info( "Imagen descargada correctamente" );
                                product.getColors().get( j )
                                        .getImages().get( k ).setPath( path );
                            } 
                            
                        } else {
                            LOG.info( "La imagen ya existe" );
                            product.getColors().get( j )
                                        .getImages().get( k ).setPath( path );                        
                        }   
                        
                    } // for images
                } // if images != null
                
                // Descargar los iconos si es necesario
                String color_path = Properties.PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName().replaceAll( " " , "_" ) + "_ICON.jpg";
                String path = Properties.COLOR_PATH + shop + "/" + shop + "_" + product.getSection() 
                                + "_" + cv.getReference() + "_" + cv.getColorName().replaceAll( " " , "_" ) + "_ICON.jpg";
                if ( ! FileManager.existsFile( path ) )
                {
                    boolean ok = downloadImage( cv.getColorURL(), path );
                    if ( ok )
                        product.getColors().get( j ).setColorPath( color_path );
                    
                } else
                    product.getColors().get( j ).setColorPath( color_path );
                
            } // for colors      
            
            productsUpdated.add( product );
            
        } // for products
        
        LOG.info( "Todas las imagenes se han descargado correctamente, se reescalan" );
        resizeImages( shop );        
        
        LOG.info( "Se reescalan los iconos de los colores" );
        resizeColors( shop );        
        
        return productsUpdated;
    }
    
    /**
     * Metodo que descarga una imagen y la almacena en la ruta especificada.
     * @param imageURL: url de la imagen a descargar.
     * @param path: path donde se quiere dejar la imagen.
     * @return true si todo ha ido correctamente.
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
            LOG.error( "URL: " + imageURL );
            LOG.error( ex.toString() );
            
            return false;
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error en la conexion" );
            LOG.error( ex.toString() );
            
            return false;
        }
                        
        return true;
    }
    
    /**
     * Metodo que llama a un script de python para reescalar las imagenes de una tienda.
     * @param shop: Nombre de la tienda de la que se quieren reescalar las imagenes.
     */
    private static void resizeImages( String shop )
    {
        try 
        {   
            double ASPECT_RATIO = 0.0f;
            
            if ( shop.equalsIgnoreCase( "Pedro Del Hierro" ) )
                ASPECT_RATIO = Properties.PDH_ASPECT_RATIO;
            
            if ( shop.equalsIgnoreCase( "Springfield" ) )
                ASPECT_RATIO = Properties.SPRINGFIELD_ASPECT_RATIO;
            
            if ( shop.equalsIgnoreCase( "HyM" ) )
                ASPECT_RATIO = Properties.HYM_ASPECT_RATIO;
            
            if ( shop.equalsIgnoreCase( "Blanco" ) )
                ASPECT_RATIO = Properties.BLANCO_ASPECT_RATIO;
            
            // El script tiene que estar en el mismo path que el jar
            Runtime.getRuntime().exec( new String[]{ "sudo"
                        , "/usr/bin/python"
                        , "resizeProducts.py"
                        , Properties.IMAGE_PATH + shop
                        , Double.toString( ASPECT_RATIO )
                        , Integer.toString( Properties.IMAGE_HEIGHT_L )
                        , Integer.toString( Properties.IMAGE_HEIGHT_S ) } );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error al ejecutar el script 'resizeProducts.py'" );
            LOG.error( ex.getMessage() );            
        }
    }
    
    /**
     * Metodo que llama a un script de python para reescalar los iconos de los colores.
     * @param shop: Nombre de la tienda de la que se quieren reescalar los iconos.
     */
    private static void resizeColors( String shop )
    {
        try 
        {      
            // El script tiene que estar en el mismo path que el jar
            Runtime.getRuntime().exec( new String[]{ "sudo"
                        , "/usr/bin/python"
                        , "resizeColors.py"
                        , Properties.COLOR_PATH + shop
                        , Integer.toString( Properties.ICON_WIDTH )
                        , Integer.toString( Properties.ICON_HEIGHT ) } );
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error al ejecutar el script 'resizeColors.py'" );
            LOG.error( ex.getMessage() );            
        }
    }
}
