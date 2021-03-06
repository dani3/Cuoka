package es.sidelab.cuokawebscraperrestserver.utils;

import es.sidelab.cuokawebscraperrestserver.properties.Properties;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

/**
 * Clase que maneja las correspondencias entre secciones. 
 * @author Daniel Mancebo Aldea
 */

@Component
public class SectionManager 
{
    private static final Log LOG = LogFactory.getLog(SectionManager.class);
    
    // Mapa con todas las secciones y sus equivalencias.
    private Map<String, String[]> sectionsMap;
    // Lista de secciones sugeridas.
    private List<String> suggestedSections;
    // Lista de secciones de hombre.
    private List<String> maleSections;
    
    public SectionManager()
    {
        refreshProperties();
    }
    
    /**
     * Metodo que actualiza el mapa y el resto de ED's.
     */
    public final void refreshProperties()
    {
        sectionsMap       = new HashMap<>();
        suggestedSections = new ArrayList<>();
        maleSections      = new ArrayList<>();
        
        try 
        {
            BufferedReader br = new BufferedReader(
                new FileReader(
                    new File(Properties.PROPERTIES_PATH + Properties.SECTIONS_FILE)));
            
            String line;
            while((line = br.readLine()) != null)
            {
                String key = line.split(":")[0];
                String[] values = line.split(":")[1].split(",");
                
                sectionsMap.put(key, values);
            }
            
        } catch (FileNotFoundException ex) {
            LOG.error("[SECTION_MANAGER] Error abriendo el fichero de secciones (" + ex.getMessage() + ")");
        } catch (IOException ex) {
            LOG.error("[SECTION_MANAGER] Error leyendo el fichero de secciones (" + ex.getMessage() + ")");
        }
        
        try 
        {
            BufferedReader br = new BufferedReader(
                new FileReader(
                    new File(Properties.PROPERTIES_PATH + Properties.SUGGESTED_SECTIONS_FILE)));
            
            String line;
            while((line = br.readLine()) != null)
            {
                suggestedSections.add(line.trim().replace("\n", ""));
            }
        
        } catch (FileNotFoundException ex) {
            LOG.error("[SECTION_MANAGER] Error abriendo el fichero de sugerencias de secciones (" + ex.getMessage() + ")");
        } catch (IOException ex) {
            LOG.error("[SECTION_MANAGER] Error leyendo el fichero de sugerencias de secciones (" + ex.getMessage() + ")");
        }
        
        try 
        {
            BufferedReader br = new BufferedReader(
                new FileReader(
                    new File(Properties.PROPERTIES_PATH + Properties.SECTIONS_MALE_FILE)));
            
            String line;
            while((line = br.readLine()) != null)
            {
                maleSections.add(line.trim().replace("\n", ""));
            }
            
        } catch (FileNotFoundException ex) {
            LOG.error("[SECTION_MANAGER] Error abriendo el fichero de secciones de hombre (" + ex.getMessage() + ")");
        } catch (IOException ex) {
            LOG.error("[SECTION_MANAGER] Error leyendo el fichero de secciones de hombre (" + ex.getMessage() + ")");
        }
    }
    
    /**
     * Metodo que recopila todos las secciones equivalentes a las recibidas.
     * @param sections: lista de secciones.
     * @return lista de secciones equivalentes a las recibidas.
     */
    public List<String> getEquivalentSections(List<String> sections)
    {
        List<String> sectionList = new ArrayList<>();
        
        for (String section : sections)
        {
            sectionList.addAll(Arrays.asList(sectionsMap.get(section)));
        }
        
        return sectionList;
    }
    
    /**
     * Metodo que dado un string determina si es una seccion.
     * @param keyword: palabra a buscar.
     * @return la seccion si se encuentra, null EOC.
     */
    public String getSection(String keyword)
    {
        Set<Entry<String, String[]>> entrySet = sectionsMap.entrySet();
        
        // Buscamos primero secciones con poca tolerancia
        for (Entry<String, String[]> entry : entrySet)
        {
            for (String section : entry.getValue())
            {
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(section
                                        , keyword) >= Properties.MAX_SIMILARITY_THRESHOLD)
                {
                    LOG.debug("[SECTION_MANAGER] Seccion encontrada: " + entry.getKey());
                    
                    return entry.getKey();
                }
            }
        }
        
        // Si no encontramos nada, aumentamos la tolerancia
        for (Entry<String, String[]> entry : entrySet)
        {
            for (String section : entry.getValue())
            {
                if (org.apache.commons.lang3.StringUtils
                                .getJaroWinklerDistance(section
                                        , keyword) >= Properties.MEDIUM_SIMILARITY_THRESHOLD)
                {
                    LOG.debug("[SECTION_MANAGER] Seccion encontrada: " + entry.getKey());
                    
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
    public List<String> getSectionsStartingWith(String word)
    {
        List<String> sections = new ArrayList<>();
        
        for (String section : suggestedSections)
        {
            if (section.toUpperCase().startsWith(word.toUpperCase()))
            {
                sections.add(section);
            }

            if (sections.size() == Properties.MAX_SUGGESTIONS)
            {
                return sections;
            }
        }    
        
        if (sections.isEmpty())
        {
            for (String section : suggestedSections)
            {
                if (section.toUpperCase().contains(word.toUpperCase()))
                {
                    sections.add(section);
                }

                if (sections.size() == Properties.MAX_SUGGESTIONS)
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
    public boolean getSectionGender(String section)
    {
        for (String male : maleSections)
        {
            if (section.equals(male))
            {
                return true;
            }
        }
        
        return false;
    }
}
