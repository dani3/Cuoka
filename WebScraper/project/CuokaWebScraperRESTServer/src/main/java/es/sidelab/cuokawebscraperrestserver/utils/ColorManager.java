package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * @class Clase que se encarga de los colores.
 * @author Daniel Mancebo Aldea
 */

@Component
public class ColorManager 
{
    private Map<String, String[]> colorMap;
    private List<String> suggestedMaleColors;
    private List<String> suggestedFemaleColors;
    
    public ColorManager()
    {
        colorMap              = new HashMap<>();
        suggestedMaleColors   = new ArrayList<>();
        suggestedFemaleColors = new ArrayList<>();
        
        colorMap.put( "Amarillos", new String[]{ "Amarillo", "Amarillos", "Amarilla", "Amarillas", "Dorado", "Dorados", "Dorada", "Doradas"
                                        , "Oro", "Oros", "Arena", "Arenas", "Beige", "Beiges", "Beis", "Camel", "Nude", "Maquillaje"
                                        , "Mostaza", "Mostazas" } );
        
        colorMap.put( "Azules", new String[]{ "Azul", "Azules", "Azulada", "Celeste", "Celestes", "Agua", "Aguas"
                                        , "Turquesa", "Turquesas", "Navy", "Marino", "Marinos" } );
         
        colorMap.put( "Beiges", new String[]{ "Beige", "Beiges", "Arena", "Arenas", "Beis", "Camel", "Nude", "Maquillaje"
                                        , "Crudo", "Crudos", "Cruda", "Crudas" } );
        
        colorMap.put( "Blancos", new String[]{ "Blanco", "Blancos", "Blanca", "Blancas", "Perla", "Perlas", "Hielo", "Crudo", "Crudos"
                                        , "Cruda", "Crudas" } );
        
        colorMap.put( "Grises", new String[]{ "Gris", "Grises", "Plata", "Platas", "Plateado", "Plateados", "Marengo", "Marengos" } );
        
        colorMap.put( "Marrones", new String[]{ "Marron", "Marrones", "Marrón" } );
        
        colorMap.put( "Morados", new String[]{ "Morado", "Morados", "Morada", "Moradas", "Purpura", "Purpuras", "Púrpura", "Púrpuras"
                                        , "Berenjena", "Berenjenas", "Lavanda", "Lavandas", "Fucsia", "Fucsias", "Lila", "Lilas" } );
        
        colorMap.put( "Negros", new String[]{ "Negro", "Negros", "Negra", "Negras", "Petroleo", "Petroleos", "Petróleo", "Petróleos" } );
        
        colorMap.put( "Rojos", new String[]{ "Rojo", "Rojos", "Roja", "Rojas", "Granate", "Granates", "Burdeos", "Terracota", "Terracotas"
                                        , "Teja", "Tejas", "Naranja", "Naranjas", "Coral", "Corales" } );
        
        colorMap.put( "Rosas", new String[]{ "Rosa", "Rosas", "Fresa", "Fresas", "Frambuesa", "Frambuesas" } );
        
        colorMap.put( "Verdes", new String[]{ "Verde", "Verdes", "Caza", "Cazas", "Caqui", "Caquis", "Khaki", "Khakis" } );
        
        suggestedMaleColors = Arrays.asList( new String[] { "a cuadros", "de cuadros", "de rayas", "a rayas", "liso", "amarillo", "dorado", "azul"
                                    , "celeste", "blanco", "gris", "plateado", "marrón", "morado", "negro", "rojo", "rosa", "granate", "naranja", "verde" } );
        
        suggestedFemaleColors = Arrays.asList( new String[] { "a cuadros", "de cuadros", "de rayas", "a rayas", "lisa","amarilla", "dorada", "azul"
                                    , "celeste", "blanca", "gris", "plateada", "marrón", "morada", "negra", "roja", "rosa", "granate", "naranja", "verde" } );
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
    
    /**
     * Metodo que dado un string determina si es un color.
     * @param keyword: palabra a buscar.
     * @return el color si se encuentra, null EOC.
     */
    public String getColor( String keyword )
    {
        Set<Map.Entry<String, String[]>> entrySet = colorMap.entrySet();
        
        // Buscamos primero colores con poca tolerancia
        for ( Map.Entry<String, String[]> entry : entrySet )
        {
            for ( String color : entry.getValue() )
            {
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( color
                                        , keyword ) >= Properties.MAX_SIMILARITY_THRESHOLD )
                {
                    return entry.getKey();
                }
            }
        }
        
        // Si no encontramos nada, aumentamos la tolerancia
        for ( Map.Entry<String, String[]> entry : entrySet )
        {
            for ( String color : entry.getValue() )
            {
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( color
                                        , keyword ) >= Properties.MEDIUM_SIMILARITY_THRESHOLD )
                {
                    return entry.getKey();
                }
            }
        }
        
        return null;
    }
    
    /**
     * Metodo que devuelve un color sugerido.
     * @param word: palabra relacionada con el color.
     * @return color sugerido.
     */
    public List<String> getColorStartingWith( String word )
    {        
        List<String> suggestedColors = new ArrayList<>();
        
        for ( String color : suggestedMaleColors )
        {
            if ( color.toUpperCase().startsWith( word.toUpperCase() ) )
            {
                suggestedColors.add( color );
            }
            
            if ( suggestedColors.size() >= Properties.MAX_SUGGESTIONS )
            {
                return suggestedColors;
            }
        }   
        
        for ( String color : suggestedMaleColors )
        {
            if ( color.toUpperCase().contains( word.toUpperCase() ) && ( ! suggestedColors.contains( color ) )  )
            {
                suggestedColors.add( color );
            }
            
            if ( suggestedColors.size() >= Properties.MAX_SUGGESTIONS )
            {
                return suggestedColors;
            }
        }
        
        return suggestedColors;
    }
    
    /**
     * Metodo que devuelve la version femenina del color.
     * @param color: color a convertir.
     * @return color convertido a femenino.
     */
    public String getFemaleColor( String color )
    {
        for ( String male : suggestedMaleColors )
        {
            if ( color.equals( male ) )
            {
                return suggestedFemaleColors.get( suggestedMaleColors.indexOf( color ) );
            }
        }
        
        return color;
    }
}
