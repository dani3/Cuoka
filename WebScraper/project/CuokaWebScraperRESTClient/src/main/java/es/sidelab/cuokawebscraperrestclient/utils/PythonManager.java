package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;

/**
 * @class Clase que gestiona todas las llamadas a scripts de Python.
 * @author Daniel Mancebo Aldea
 */

public class PythonManager 
{
    private static final Logger LOG = Logger.getLogger( PythonManager.class );
    
    /**
     * Metodo que ejecuta el script 'renderSections.py'.
     * @return true si se ha ejecutado correctamente.
     * @throws IOException 
     * @throws java.lang.InterruptedException 
     */
    public static boolean executeRenderSections() throws IOException, InterruptedException
    {
        File[] folders = new File( Properties.SHOPS_PATH ).listFiles();
        
        // Recorremos las tiendas
        for ( File folder : folders )
        {
            if ( folder.isDirectory() )
            {
                // Sacamos el nombre de la tienda ('tienda_online')
                String folderName = folder.getName();
                
                if ( folderName.contains( "true" ) )
                {  
                    // Recorremos hombre y mujer si esta online
                    File[] subFolders = new File( Properties.SHOPS_PATH + "\\" + folderName ).listFiles();
                    
                    for( File subFolder : subFolders )
                    {
                        if ( subFolder.isDirectory() )
                        {
                            // Sacamos si es hombre o mujer
                            String man = subFolder.getName();                            
                            String path = Properties.SHOPS_PATH + folderName + "\\" + man + "\\";
                            
                            // Borramos los txt antes
                            for ( File file : subFolder.listFiles() )
                            {
                                if ( file.getName().contains( ".txt" ) )
                                {
                                    file.delete();
                                }
                            }
                            
                            // Encapsulamos el path entre comillas para que los espacios no afecten                            
                            LOG.info( "Ejecutando: " + "python "
                                                    + "\"" + path + "renderSections.py" + "\""  );
                            
                            // Ejecutamos el script de la tienda
                            Process p = Runtime.getRuntime().exec( "python "
                                                    + "\"" + path + "renderSections.py" + "\""  );
                            
                            Thread.sleep( 2500 );
                            
                            // Nos quedamos esperando hasta que termine
                            File file = new File( path + "done.dat" );
                            while ( ! file.exists() ) 
                            {
                                file = new File( path + "done.dat" );
                            }
                            
                            file.delete();
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Metodo que ejecuta el script 'RenderProduct'.
     * @param url: URL del producto.
     * @param path: fichero html donde se debe dejar el resultado.
     * @return file del html.
     * @throws IOException 
     */
    public static File executeRenderProduct( String url, String path ) throws IOException
    {                
        Process p = Runtime.getRuntime().exec( new String[]{ "python",
                            Properties.RENDER_SCRIPT + "renderProduct.py", 
                            url, path } );
           
        File file = new File( path );     
        while ( ! file.exists() ) 
        {
            file = new File( path );
        }
        
        return file;
    }
    
    /**
     * Metodo que elimina un fichero html dado.
     * @param path: ruta del fichero html.
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteFile( String path )
    {        
        return new File( path ).delete();
    }
}
