package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * @class Clase que se encarga de los colores.
 * @author Daniel Mancebo Aldea
 */

@Component
public class ColorManager 
{
    private Map<String, String[]> colorMap;
    
    public ColorManager()
    {
        colorMap = new HashMap<>();
        
        colorMap.put( "Amarillos", new String[]{ "Amarillo"
                                        , "Dorado"
                                        , "Oro"
                                        , "Arena"
                                        , "Beige" 
                                        , "Beis"
                                        , "Camel" 
                                        , "Nude"
                                        , "Maquillaje"
                                        , "Mostaza"
                                        , "Crudo" } );
        
        colorMap.put( "Azules", new String[]{ "Azul"
                                        , "Celeste"
                                        , "Agua"
                                        , "Turquesa"
                                        , "Navy" } );
        
        colorMap.put( "Beiges", new String[]{ "Beige"
                                        , "Arena"
                                        , "Beis"
                                        , "Camel"
                                        , "Nude"
                                        , "Maquillaje"
                                        , "Crudo" } );
        
        colorMap.put( "Blancos", new String[]{ "Blanco"
                                        , "Perla" } );
        
        colorMap.put( "Grises", new String[]{ "Gris"
                                        , "Plata" 
                                        , "Plateado" } );
        
        colorMap.put( "Marrones", new String[]{ "Marron"
                                        , "Marrón" } );
        
        colorMap.put( "Morados", new String[]{ "Morado"
                                        , "Purpura"
                                        , "Púrpura"
                                        , "Berenjena"
                                        , "Lavanda"
                                        , "Fucsia" 
                                        , "Lila" } );
        
        colorMap.put( "Negros", new String[]{ "Negro"
                                        , "Petroleo" 
                                        , "Petróleo" } );
        
        colorMap.put( "Rojos", new String[]{ "Rojo"
                                        , "Granate" 
                                        , "Burdeos" 
                                        , "Teja"
                                        , "Naranja"
                                        , "Coral" } );
        
        colorMap.put( "Rosas", new String[]{ "Rosa"
                                        , "Fresa"
                                        , "Frambuesa" } );
        
        colorMap.put( "Verdes", new String[]{ "Verde"
                                        , "Caza"
                                        , "Caqui" 
                                        , "Khaki" } );
    }
    
    /**
     * Metodo que averigua el nombre del color de un producto.
     * @param color_name: nombre del color.
     * @return nombre del fichero al que pertenece el color.
     */
    public static String findOutColor( String color_name )
    {
        File[] files = new File( Properties.PREDEFINED_COLORS_PATH ).listFiles();
        
        List<String> colors_simple = new ArrayList<>();
        List<String> colors_compound = new ArrayList<>();
        
        // Metemos en dos listas los colores que se encuentren, los compuestos y los simples.
        for ( File file : files )
        {
            String fileName = file.getName();
            
            String[] colors = fileName.split( "_" );
            for ( String color : colors )
            {
                if ( color.contains( "-" ) )
                    colors_compound.add( color.replaceAll( "-", " " ) );
                
                else if ( ( ! color.contains( "-" ) ) && ( ! color.contains( "ICON" ) ) )
                    colors_simple.add( color );
                
            }
        }
        
        // Buscamos primero en los colores simples
        for ( String color : colors_simple )
        {
            if ( color_name.contains( color ) )
            {
                String color_found = color;
                
                // Si lo encontramos, buscamos en los colores compuestos
                for ( String color_compound : colors_compound )
                {
                    if ( color_compound.contains( color_name ) )
                    {
                        color_found = color_compound;
                    }
                }
                
                return color_found.replace( " " , "-" );
            }
        }
        
        return null;
    }
    
    /**
     * Metodo que recopila todos los colores equivalentes a los recibidos.
     * @param colors: lista de colores.
     * @return lista de colores equivalentes a los recibidos.
     */
    public List<String> getEquivalentColors( List<String> colors )
    {
        List<String> colorList = new ArrayList<>();
        
        for ( String color : colors )
        {
            colorList.addAll( Arrays.asList( colorMap.get( color ) ) );
        }
        
        return colorList;
    }
}
