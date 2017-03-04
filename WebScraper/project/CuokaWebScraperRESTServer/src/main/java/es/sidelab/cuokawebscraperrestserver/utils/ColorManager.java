package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Clase que se encarga de todo lo relacionado con los colores.
 * Contiene un mapa con todos los colores equivalentes, asi como las sugerencias.
 * @author Daniel Mancebo Aldea.
 */

@Component
public class ColorManager 
{
    // Mapa con todos los colores y sus equivalencias.
    private final Map<String, String[]> colorMap;
    // Lista de los colores masculinos a sugerir.
    private final List<String> suggestedMaleColors;
    // Lista de los colores femeninos a sugerir.
    private final List<String> suggestedFemaleColors;
    
    public ColorManager()
    {
        colorMap = new HashMap<>();
        
        colorMap.put("Amarillos", new String[]{ "Amarillo", "Amarillos", "Amarilla", "Amarillas", "Dorado", "Dorados", "Dorada", "Doradas"
                                        , "Oro", "Oros", "Arena", "Arenas", "Beige", "Beiges", "Beis", "Camel", "Nude", "Maquillaje"
                                        , "Mostaza", "Mostazas", "Limón", "Limon", "Lima", "Ámbar", "Ambar", "Indio" });
        
        colorMap.put("Azules", new String[]{ "Azul", "Azules", "Azulada", "Celeste", "Celestes", "Agua", "Aguas", "Turquí", "Turqui", "Klein"
                                        , "Turquesa", "Turquesas", "Navy", "Marino", "Marinos", "Añil", "Cobalto", "Zafiro", "Índigo", "Indigo" });
         
        colorMap.put("Beiges", new String[]{ "Beige", "Beiges", "Arena", "Arenas", "Beis", "Camel", "Nude", "Maquillaje"
                                        , "Crudo", "Crudos", "Cruda", "Crudas" });
        
        colorMap.put("Blancos", new String[]{ "Blanco", "Blancos", "Blanca", "Blancas", "Perla", "Perlas", "Hielo", "Crudo", "Crudos"
                                        , "Cruda", "Crudas", "Marfil", "Nieve" });
        
        colorMap.put("Grises", new String[]{ "Gris", "Grises", "Plata", "Platas", "Plateado", "Plateados", "Marengo", "Marengos" });
        
        colorMap.put("Marrones", new String[]{ "Marron", "Marrones", "Marrón", "Pardo", "Café", "Chocolate", "Castaño", "Borgoña", "Ocre", "Siena" });
        
        colorMap.put("Morados", new String[]{ "Morado", "Morados", "Morada", "Moradas", "Purpura", "Purpuras", "Púrpura", "Púrpuras"
                                        , "Berenjena", "Berenjenas", "Lavanda", "Lavandas", "Fucsia", "Fucsias", "Lila", "Lilas", "Malva" });
        
        colorMap.put("Negros", new String[]{ "Negro", "Negros", "Negra", "Negras", "Petroleo", "Petroleos", "Petróleo", "Petróleos" });
        
        colorMap.put("Rojos", new String[]{ "Rojo", "Rojos", "Roja", "Rojas", "Granate", "Granates", "Burdeos", "Terracota", "Terracotas", "Escarlata"
                                        , "Teja", "Tejas", "Naranja", "Naranjas", "Coral", "Corales", "Colorado", "Bermellon", "Bermellón", "Carmín", "Carmin" });
        
        colorMap.put("Rosas", new String[]{ "Rosa", "Rosas", "Fresa", "Fresas", "Frambuesa", "Frambuesas", "Salmón", "Salmon" });
        
        colorMap.put("Verdes", new String[]{ "Verde", "Verdes", "Caza", "Cazas", "Caqui", "Caquis", "Khaki", "Khakis", "Esmeralda", "Jade", "Veronés"
                                        , "Oliva", "Arlequín", "Arlequin", "Esparrago", "Espárrago" });
        
        colorMap.put("Cuadros", new String[]{ "Cuadro", "Cuadros" });
        
        colorMap.put("Rayas", new String[]{ "Raya", "Rayas" });
        
        colorMap.put("Lunares", new String[]{ "Lunares", "Moteado", "Moteados" });
        
        colorMap.put("Flores", new String[]{ "Flor", "Flores", "Floral" });
        
        colorMap.put("Estampado", new String[]{ "Estampado", "Estampados" });
        
        colorMap.put("Cuero", new String[]{ "Cuero", "Leather", "Piel", "Pieles" });
        
        /* Lista de colores sugeridos tanto en masculino como en femenino (IMPORTANTE que estén en el mismo orden y posición */
        suggestedMaleColors = Arrays.asList(new String[] { "a cuadros", "de cuadros", "de rayas", "a rayas", "de cuero", "de piel", "liso"
                                    , "amarillo", "dorado", "azul", "celeste", "blanco", "gris", "plateado", "marrón", "morado", "negro"
                                    , "rojo", "rosa", "granate", "naranja", "verde", "de lunares", "de flores" });
        
        suggestedFemaleColors = Arrays.asList(new String[] { "a cuadros", "de cuadros", "de rayas", "a rayas", "de cuero", "de piel", "lisa"
                                    , "amarilla", "dorada", "azul", "celeste", "blanca", "gris", "plateada", "marrón", "morada", "negra"
                                    , "roja", "rosa", "granate", "naranja", "verde", "de lunares", "de flores" });
    }
    
    /**
     * Metodo que averigua el nombre del color de un producto.
     * Busca el nombre en la ruta con todos nuestros colores.
     * Primero realiza una busqueda del color simple, y luego busca el compuesto.
     * @param color_name: nombre del color.
     * @return nombre del fichero al que pertenece el color.
     */
    public static String findOutColor(String color_name)
    {
        File[] files = new File(Properties.PREDEFINED_COLORS_PATH).listFiles();
        
        List<String> colors_simple = new ArrayList<>();
        List<String> colors_compound = new ArrayList<>();
        
        // Metemos en dos listas los colores que se encuentren, los compuestos y los simples.
        for (File file : files)
        {
            String fileName = file.getName();
            
            String[] colors = fileName.split("_");
            for (String color : colors)
            {
                if (color.contains("-"))
                {
                    colors_compound.add(color.replaceAll("-", " "));                
                } else if ((! color.contains("-")) && (! color.contains("ICON"))) {
                    colors_simple.add(color);
                }
            }
        }
        
        // Buscamos primero en los colores simples
        for (String color : colors_simple)
        {
            if (StringUtils.containsIgnoreCase(color_name, color))
            {
                String color_found = color;
                
                // Si lo encontramos, buscamos en los colores compuestos
                for (String color_compound : colors_compound)
                {
                    if (StringUtils.containsIgnoreCase(color_compound, color_name))
                    {
                        color_found = color_compound;
                    }
                }
                
                return color_found.replace(" " , "-");
            }
        }
        
        return null;
    }
    
    /**
     * Metodo que recopila todos los colores equivalentes a los recibidos.
     * @param colors: lista de colores.
     * @return lista de colores equivalentes a los recibidos.
     */
    public List<String> getEquivalentColors(List<String> colors)
    {
        List<String> colorList = new ArrayList<>();
        
        for (String color : colors)
        {
            colorList.addAll(Arrays.asList(colorMap.get(color)));
        }
        
        return colorList;
    }
    
    /**
     * Metodo que dado un string determina si es un color.
     * @param keyword: palabra a buscar.
     * @return el color si se encuentra, null EOC.
     */
    public String getColor(String keyword)
    {
        Set<Map.Entry<String, String[]>> entrySet = colorMap.entrySet();
        
        // Buscamos primero colores con poca tolerancia
        for (Map.Entry<String, String[]> entry : entrySet)
        {
            for (String color : entry.getValue())
            {
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(color
                                        , keyword) >= Properties.MAX_SIMILARITY_THRESHOLD)
                {
                    return entry.getKey();
                }
            }
        }
        
        // Si no encontramos nada, aumentamos la tolerancia
        for (Map.Entry<String, String[]> entry : entrySet)
        {
            for (String color : entry.getValue())
            {
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(color
                                        , keyword) >= Properties.MEDIUM_SIMILARITY_THRESHOLD)
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
    public List<String> getColorStartingWith(String word)
    {        
        List<String> suggestedColors = new ArrayList<>();
        
        for (String color : suggestedMaleColors)
        {
            if (color.toUpperCase().startsWith(word.toUpperCase()))
            {
                suggestedColors.add(color);
            }
            
            if (suggestedColors.size() >= Properties.MAX_SUGGESTIONS)
            {
                return suggestedColors;
            }
        }   
        
        for (String color : suggestedMaleColors)
        {
            if (color.toUpperCase().contains(word.toUpperCase()) && (! suggestedColors.contains(color)) )
            {
                suggestedColors.add(color);
            }
            
            if (suggestedColors.size() >= Properties.MAX_SUGGESTIONS)
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
    public String getFemaleColor(String color)
    {
        for (String male : suggestedMaleColors)
        {
            if (color.equals(male))
            {
                return suggestedFemaleColors.get(suggestedMaleColors.indexOf(color));
            }
        }
        
        return color;
    }
}
