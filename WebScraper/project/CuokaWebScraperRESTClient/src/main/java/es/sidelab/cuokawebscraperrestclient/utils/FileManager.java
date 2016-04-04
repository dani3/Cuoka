package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * @class Clase que se encarga del tema de ficheros.
 * @author Daniel Mancebo Aldea
 */

public class FileManager 
{
    private static final Logger LOG = Logger.getLogger( FileManager.class );  
    
    /**
     * Metodo que crea un fichero de texto con todos los links recibidos.
     * @param listOfLinks: lista de links a escribir
     * @param section: seccion a la que pertenecen los productos.
     */
    public static void writeLinksToFile( List<String> listOfLinks, Section section )
    {
        BufferedWriter bw = null;
        
        try {            
            File file = new File( section.getPath() + section.getName() + "_LINKS.txt" );
            
            if( ! file.exists() )
                file.createNewFile();
            
            // El segundo parametro debe estar a false para que se sobreescriba el contenido
            FileWriter fw = new FileWriter( file, false );
            bw = new BufferedWriter( fw );
            for ( String link : listOfLinks )
            {
                bw.write( link );
                bw.write( "\n" );
            }
            
        } catch ( IOException ex ) {
            LOG.error( "Error escribiendo el fichero '" + section.getPath() + section.getName() + "_LINKS.txt'" );
            LOG.error( ex.getMessage() );
            
        } finally {
            try {
                if( bw != null )
                    bw.close();
                
            } catch ( IOException ex ) {
                LOG.error( "Error cerrando el fichero '" + section.getPath() + section.getName() + "_LINKS.txt'" );
                LOG.error( ex.getMessage() );
            }
        }
    }
    
    /**
     * Metodo que elimina un fichero dado.
     * @param path: ruta del fichero.
     * @return true si se ha borrado correctamente.
     */
    public static boolean deleteFile( String path )
    {        
        return new File( path ).delete();
    }
}
