package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.util.Map;

/**
 * Clase que escribe por pantalla o formatea los resultados.
 * @author Daniel Mancebo Aldea
 */

public class Printer 
{    
    /**
     * Metodo que pinta por pantalla.
     * @param message mensaje a escribir.
     */
    public static void print(String message)
    {
        System.out.println(message);
    }
    
    /**
     * Metodo que formatea los resultados en un string.
     * @param shop: tienda.
     * @param section: seccion.
     * @param results: mapa de resultados.
     * @return string con las estadisticas.
     */
    public synchronized static String formatResults(Shop shop, Section section, Map<String, Integer> results)
    {       
        StringBuilder body = new StringBuilder();
        
        if (!results.entrySet().isEmpty())
        {
            body.append("-----------------------------------------------------\n");
            body.append(" - ").append(section.getName()).append(" de ").append((section.isMan()) ? "hombre" : "mujer").append("\n");
            for (Map.Entry<String, Integer> entrySet : results.entrySet())
            {
                body.append("  · ").append(entrySet.getKey()).append(": ").append(entrySet.getValue()).append("\n");
            }
        }
        
        return body.toString();
    }
}
