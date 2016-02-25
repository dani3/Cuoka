package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.properties.Properties;
import java.io.File;
import java.io.IOException;

/**
 * @class Clase que gestiona todas las llamadas a scripts de Python.
 * @author Daniel Mancebo Aldea
 */

public class PythonManager 
{
    /**
     * Metodo que ejecuta el script 'RenderPages'.
     * @return true si se ha ejecutado correctamente.
     * @throws IOException 
     */
    public static boolean executeRenderPages() throws IOException
    {
        Process p = Runtime.getRuntime().exec( "python "
                            + Properties.RENDER_SCRIPT + "renderPages.py" );
        
        File file = new File( Properties.DONE_FILE_PYTHON );
        while ( ! file.exists() ) 
        {
            file = new File( Properties.DONE_FILE_PYTHON );
        }
        
        return file.delete();
    }
    
    /**
     * Metodo que ejecuta el script 'RenderProduct'.
     * @param url: URL del producto.
     * @param html: fichero html donde se debe dejar el resultado.
     * @return file del html.
     * @throws IOException 
     */
    public static File executeRenderProduct( String url, String html ) throws IOException
    {
        File file = new File( html );
        
        Runtime.getRuntime().exec( "python "
                            + Properties.RENDER_SCRIPT + "renderProduct.py " 
                            +  url + " " + html );
                
        while ( ! file.exists() ) 
        {
            file = new File( html );
        }
        
        return file;
    }
    
    /**
     * Metodo que elimina un fichero html dado.
     * @param html: ruta del fichero html.
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteFile( String html )
    {
        return new File( html ).delete();
    }
}
