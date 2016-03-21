package es.sidelab.cuokawebscraperrestclient.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Clase que gestiona todo lo relacionado con ficheros.
 * @author Daniel Mancebo Aldeas
 */

public class FileManager 
{
    private static final Logger LOG = Logger.getLogger( FileManager.class );
    
    /**
     * Metodo que lee el fichero de links creado por el script 'renderSections'
     * y devuelve la lista de links.
     * @param file: fichero de texto.
     * @return lista con todos los links del fichero, NULL si hay error.
     */
    public static List<String> getListOfLinks( String file )
    {
        BufferedReader br = null;
        List<String> productsLink = new ArrayList<>();
        
        try 
        {
            br = new BufferedReader( new FileReader( file ) );
            
            String line = null;
            while ( ( line = br.readLine() ) != null )
            {
                if ( ! line.isEmpty() )
                    productsLink.add( line );
            }
            
        } catch ( FileNotFoundException ex ) {
            LOG.error( "ERROR: Fichero '" + file + "' no encontrado" );
            
            return null;
            
        } catch ( IOException ex ) {
            LOG.error( "ERROR: Error con el fichero '" + file + "'" );
            
            return null;
            
        } finally {
            try 
            {
                if ( br != null )
                    br.close();
                
            } catch (IOException ex) {
                LOG.error( "ERROR: Error cerrando el fichero '" + file + "'" );
                
                return null;
            }
        }
        
        return productsLink;
    }
    
}
