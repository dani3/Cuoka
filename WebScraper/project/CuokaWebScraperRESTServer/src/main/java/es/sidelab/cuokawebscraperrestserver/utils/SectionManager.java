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
 * Clase que maneja las correspondencias entre secciones. 
 * @author Daniel Mancebo Aldea
 */

@Component
public class SectionManager 
{
    private Map<String, String[]> sectionsMap;
    private List<String> suggestedSections;
    private List<String> maleSections;
    
    public SectionManager()
    {
        sectionsMap       = new HashMap<>();
        suggestedSections = new ArrayList<>();
        maleSections      = new ArrayList<>();
        
        sectionsMap.put( "Abrigos", new String[]{ "Cazadora", "Cazadoras", "Capa", "Capas", "Bomber", "Bombers", "Parka", "Parkas"
                                            , "Chaqueta", "Chaquetas", "Chaqueton", "Chaquetones", "Abrigo", "Abrigos", "Cardigan", "Cardigans" } );
        
        sectionsMap.put( "Americanas", new String[]{ "Chaqueta", "Chaquetas", "Blazer", "Blazers", "Chaleco", "Chalecos"
                                            , "Cazadora", "Cazadoras", "Bomber", "Bombers", "Americana", "Americanas" } );
        
        sectionsMap.put( "Camisas", new String[]{ "Blusa", "Blusas", "Camisola", "Camisolas"
                                            , "Bluson", "Blusones", "Camisa", "Camisas" } );
        
        sectionsMap.put( "Polos", new String[]{ "Polo", "Polos" } );
        
        sectionsMap.put( "Camisetas", new String[]{ "Top", "Tops", "Bodies", "Camiseta", "Camisetas", "Henley", "T-Shirt" } );
        
        sectionsMap.put( "Jerseis", new String[]{ "Jersey", "Jerseys", "Sudadera", "Sudaderas", "Cardigan", "Cardigans", "Jersei", "Jerseis" } );
        
        sectionsMap.put( "Pantalones", new String[]{ "Vaqueros", "Vaquero", "Jeans", "Jean", "Chinos", "Chinos", "Pantalon", "Pantalones", "Leggins" } );
        
        sectionsMap.put( "Shorts", new String[]{ "Shorts", "Short" , "Pantalones cortos", "Pantalon corto", "Bermuda", "Bermudas" } );
        
        sectionsMap.put( "Trajes", new String[]{ "Chaqueta", "Chaquetas", "Blazer", "Blazers", "Americana", "Americanas", "Traje", "Trajes" } );
        
        sectionsMap.put( "Vestidos", new String[]{ "Vestido", "Vestidos" } );
        
        sectionsMap.put( "Faldas", new String[]{ "Falda", "Faldas", "Shorts", "Short", "Bermuda", "Bermudas" } );
        
        sectionsMap.put( "Ponchos y Monos", new String[]{ "Mono", "Monos", "Kimono", "Kimonos", "Quimono", "Quimonos"
                                        , "Peto", "Petos", "Poncho", "Poncho" } );
        
        sectionsMap.put( "Sudaderas", new String[]{ "Sudadera", "Sudaderas", "Jersey", "Jerseys", "Jersei", "Jerseis" } );
        
        sectionsMap.put( "Sport", new String[]{ "Leggin", "Leggins", "Sport", "Gym", "Gimnasia", "Jogging", "Easywear"
                                        , "Deportivo", "Deportivos", "Sportwear", "Beachwear" } );
        
        sectionsMap.put( "Zapatos", new String[]{ "Zapato", "Zapatos", "Calzado", "Bota", "Botas", "Botin", "Botines", "Sandalia", "Sandalias"
                                        , "Manoletina", "Manoletinas", "Bailarina", "Zapatilla", "Zapatillas", "Zueco", "Zuecos", "Cuñas"
                                        , "Chancla", "Chanclas", "Chancleta", "Chancletas" } );
        
        suggestedSections = Arrays.asList( new String[] { "Cazadora", "Bomber", "Chaqueta", "Chaquetón", "Abrigo", "Blazer"
                                        , "Americana", "Blusa", "Camisa", "Camiseta", "Polo", "Top", "Vaqueros", "Jeans", "Jersey"
                                        , "Sudadera", "Cardigan", "Chinos", "Pantalones", "Pantalones cortos", "Bermuda", "Shorts", "Traje" 
                                        , "Vestido", "Falda", "Chaleco", "Mono", "Kimono", "Quimono", "Peto", "Leggin", "Sport", "Gym", "Ropa deportiva"
                                        , "Poncho", "Parka", "Zapatos", "Botas", "Sandalias", "Zapatillas" } );
    
        maleSections = Arrays.asList( new String[] { "Chaquetón", "Abrigo", "Polo", "Top", "Vaqueros", "Jeans", "Jersey", "Cardigan"
                                  , "Chinos", "Pantalones", "Pantalones cortos", "Shorts", "Traje", "Vestido", "Chaleco", "Mono"
                                  , "Kimono", "Quimono", "Peto", "Leggin", "Sport", "Gym", "Poncho", "Zapatos" } );
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
    
    /**
     * Metodo que devuelve un máximo de cinco secciones sugeridas.
     * @param word palabra con la que se buscan las sugerencias.
     * @return lista de secciones sugeridas.
     */
    public List<String> getSectionsStartingWith( String word )
    {
        List<String> sections = new ArrayList<>();
        
        for ( String section : suggestedSections )
        {
            if ( section.toUpperCase().startsWith( word.toUpperCase() ) )
            {
                sections.add( section );
            }

            if ( sections.size() == Properties.MAX_SUGGESTIONS )
            {
                return sections;
            }
        }    
        
        if ( sections.isEmpty() )
        {
            for ( String section : suggestedSections )
            {
                if ( section.toUpperCase().contains( word.toUpperCase() ) )
                {
                    sections.add( section );
                }

                if ( sections.size() == Properties.MAX_SUGGESTIONS )
                {
                    return sections;
                }
            }   
        }
        
        return sections;
    }
    
    /**
     * Metodo que devuelve true si la seccion es de hombre.
     * @param section: seccion a buscar.
     * @return true si la seccion es de hombre
     */
    public boolean getSectionGender( String section )
    {
        for ( String male : maleSections )
        {
            if ( section.equals( male ) )
            {
                return true;
            }
        }
        
        return false;
    }
}
