package es.sidelab.cuokawebscraperrestserver.utils;

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
    private Map<String, List<String>> sectionsMap;
    
    public SectionManager()
    {
        
    }
    
    public List<String> getEquivalentSections( String section )
    {
        return null;
    }
}
