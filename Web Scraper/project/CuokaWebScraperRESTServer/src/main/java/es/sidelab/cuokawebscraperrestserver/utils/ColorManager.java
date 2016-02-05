package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;

/**
 * @class Clase que se encarga de los colores
 * @author Daniel Mancebo Aldea
 */

public class ColorManager 
{
    /**
     * Metodo que averigua el nombre del color de un producto.
     * @param color_name: nombre del color
     * @return nombre del fichero al que pertenece el color.
     */
    public static String findOutColor( String color_name )
    {
        File[] colors = new File( Properties.PREDEFINED_COLORS_PATH ).listFiles();
        
        for ( File file : colors )
        {
            String fileName = file.getName();
            
            if ( fileName.contains( color_name.toUpperCase() ) )
                return fileName;
        }
        
        return null;
    }
}
