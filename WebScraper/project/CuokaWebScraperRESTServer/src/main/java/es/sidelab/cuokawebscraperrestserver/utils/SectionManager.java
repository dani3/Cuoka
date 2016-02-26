package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * @class Clase que maneja las correspondencias entre secciones. 
 * @author Daniel Mancebo Aldea
 */

@Component
public class SectionManager 
{
    private Map<String, String[]> sectionsMap;
    
    public SectionManager()
    {
        sectionsMap = new HashMap<>();
        
        sectionsMap.put( "Abrigos", new String[]{ "Cazadora", "Cazadoras"
                                            , "Capa", "Capas"
                                            , "Bomber", "Bombers"
                                            , "Chaqueta", "Chaquetas"
                                            , "Chaqueton", "Chaquetones"
                                            , "Chaquetón"
                                            , "Abrigo", "Abrigos" } );
        
        sectionsMap.put( "Americanas", new String[]{ "Chaqueta", "Chaquetas"
                                            , "Blazer", "Blazers"
                                            , "Chaqueton", "Chaquetones"
                                            , "Chaquetón" 
                                            , "Cazadora" 
                                            , "Bomber", "Bombers"
                                            , "Americana", "Americanas" } );
        
        sectionsMap.put( "Camisas", new String[]{ "Blusa", "Blusas"
                                            , "Camisola", "Camisolas"
                                            , "Bluson", "Blusones"
                                            , "Blusón" 
                                            , "Polo", "Polos"
                                            , "Camisa", "Camisas" } );
        
        sectionsMap.put( "Camisetas", new String[]{ "Top", "Tops"
                                            , "Bodies"
                                            , "Polo", "Polos"
                                            , "Camiseta", "Camisetas" } );
        
        sectionsMap.put( "Chaquetas", new String[]{ "Cazadora", "Cazadoras"
                                            , "Bomber", "Bombers"
                                            , "Chaqueton", "Chaquetones"
                                            , "Chaquetón"
                                            , "Abrigo", "Abrigos"
                                            , "Chaqueta", "Chaquetas" } );
        
        sectionsMap.put( "Jeans", new String[]{ "Vaqueros", "Vaquero"
                                            , "Jeans", "Jean" } );
        
        sectionsMap.put( "Jerseis", new String[]{ "Jersey", "Jerseys"
                                            , "Sudadera", "Sudaderas"
                                            , "Cardigan", "Cardigans"
                                            , "Jersei", "Jerseis" } );
        
        sectionsMap.put( "Pantalones", new String[]{ "Vaqueros", "Vaquero"
                                            , "Jeans", "Jean"
                                            , "Chinos", "Chinos"
                                            , "Pantalón" 
                                            , "Pantalon", "Pantalones" } );
        
        sectionsMap.put( "Shorts", new String[]{ "Shorts", "Short" 
                                            , "Pantalones cortos", "Pantalon corto"
                                            , "Pantalón corto"
                                            , "Bermuda", "Bermudas" } );
        
        sectionsMap.put( "Trajes", new String[]{ "Chaqueta", "Chaquetas"
                                            , "Blazer", "Blazers"
                                            , "Chaqueton", "Chaquetones"
                                            , "Chaquetón" 
                                            , "Cazadora" , "Cazadoras" 
                                            , "Bomber", "Bombers"
                                            , "Americana", "Americanas"
                                            , "Traje", "Trajes" } );
        
        sectionsMap.put( "Vestidos", new String[]{ "Vestidos", "Vestido" } );
        
        sectionsMap.put( "Faldas", new String[]{ "Faldas", "Falda"
                                            , "Shorts", "Short"
                                            , "Bermuda", "Bermudas" } );
        
        sectionsMap.put( "Polos", new String[]{ "Polo", "Polos"
                                            , "Blusa", "Blusas"
                                            , "Blusón", "Bluson", "Blusones"
                                            , "Camisola", "Camisolas" } );
        
        sectionsMap.put( "Chalecos", new String[]{ "Chaleco", "Chalecos" } );
        
        sectionsMap.put( "Monos", new String[]{ "Mono", "Monos"
                                            , "Kimono", "Kimonos"
                                            , "Quimono", "Quimonos"
                                            , "Peto", "Petos" } );
        
        sectionsMap.put( "Sport", new String[]{ "Leggin", "Leggins"
                                            , "Sport"
                                            , "Gym"
                                            , "Gimnasia"
                                            , "Jogging" 
                                            , "Easywear"
                                            , "Deportivo", "Deportivos" } );
    }
    
    /**
     * Metodo que recopila todos las secciones equivalentes a las recibidas.
     * @param sections: lista de secciones.
     * @return lista de secciones equivalentes a las recibidas.
     */
    public List<String> getEquivalentSections( List<String> sections )
    {
        List<String> sectionList = new ArrayList<>();
        
        for ( String section : sections )
        {
            sectionList.addAll( Arrays.asList( sectionsMap.get( section ) ) );
        }
        
        return sectionList;
    }
    
    /**
     * Metodo que dado un string determina si es una seccion.
     * @param keyword: palabra a buscar.
     * @return la seccion si se encuentra, null EOC.
     */
    public String getSection( String keyword )
    {
        Set<Entry<String, String[]>> entrySet = sectionsMap.entrySet();
        
        // Buscamos primero secciones con poca tolerancia
        for ( Entry<String, String[]> entry : entrySet )
        {
            for ( String section : entry.getValue() )
            {
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( section
                                        , keyword ) >= Properties.MAX_SIMILARITY_THRESHOLD )
                {
                    return entry.getKey();
                }
            }
        }
        
        // Si no encontramos nada, aumentamos la tolerancia
        for ( Entry<String, String[]> entry : entrySet )
        {
            for ( String section : entry.getValue() )
            {
                if ( org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance( section
                                        , keyword ) >= Properties.MEDIUM_SIMILARITY_THRESHOLD )
                {
                    return entry.getKey();
                }
            }
        }
        
        return null;
    }
}
