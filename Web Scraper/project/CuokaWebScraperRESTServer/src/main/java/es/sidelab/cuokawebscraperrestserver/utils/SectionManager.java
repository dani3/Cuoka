package es.sidelab.cuokawebscraperrestserver.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        
        sectionsMap.put( "Abrigos", new String[]{ "Cazadora"
                                            , "Capa" 
                                            , "Bomber"
                                            , "Chaqueta" 
                                            , "Chaqueton"
                                            , "Abrigo" } );
        
        sectionsMap.put( "Americanas", new String[]{ "Chaqueta"
                                            , "Blazer"
                                            , "Chaqueton"
                                            , "Chaquetón" 
                                            , "Cazadora" 
                                            , "Bomber"
                                            , "Americana" } );
        
        sectionsMap.put( "Camisas", new String[]{ "Blusa"
                                            , "Camisola"
                                            , "Bluson"
                                            , "Blusón" 
                                            , "Polo"
                                            , "Camisa" } );
        
        sectionsMap.put( "Camisetas", new String[]{ "Top"
                                            , "Bodies"
                                            , "Polo"
                                            , "Camiseta" } );
        
        sectionsMap.put( "Chaquetas", new String[]{ "Cazadora"
                                            , "Bomber"
                                            , "Chaquetón"
                                            , "Abrigo"
                                            , "Chaqueta" } );
        
        sectionsMap.put( "Jeans", new String[]{ "Vaqueros"
                                            , "Jeans" } );
        
        sectionsMap.put( "Jerseis", new String[]{ "Jersey"
                                            , "Sudadera"
                                            , "Cardigan"
                                            , "Jersei" } );
        
        sectionsMap.put( "Pantalones", new String[]{ "Vaqueros"
                                            , "Jeans"
                                            , "Chinos"
                                            , "Pantalón" 
                                            , "Pantalon" 
                                            , "Pantalones" } );
        
        sectionsMap.put( "Shorts", new String[]{ "Shorts"
                                            , "Pantalones cortos"
                                            , "Bermuda"} );
        
        sectionsMap.put( "Trajes", new String[]{ "Chaqueta"
                                            , "Blazer"
                                            , "Chaqueton"
                                            , "Chaquetón" 
                                            , "Cazadora" 
                                            , "Bomber"
                                            , "Americana"
                                            , "Trajes" } );
        
        sectionsMap.put( "Vestidos", new String[]{ "Vestidos" } );
        
        sectionsMap.put( "Faldas", new String[]{ "Faldas"
                                            , "Shorts"
                                            , "Bermuda" } );
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
}
