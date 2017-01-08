package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Clase que escribe por pantalla o fichero.
 * @author Daniel Mancebo Aldea
 */

public class Printer 
{
    private static final Logger LOG = Logger.getLogger(Printer.class);  
    
    /**
     * Metodo que pinta por pantalla.
     * @param message mensaje a escribir.
     */
    public static void print(String message)
    {
        System.out.println(message);
    }
    
    /**
     * Metodo que escribe los resultados en un fichero.
     * @param shop: tienda.
     * @param section: seccion.
     * @param results: mapa de resultados.
     */
    public synchronized static void printResults(Shop shop, Section section, Map<String, Integer> results)
    {       
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        
        File resultsFile = FileManager.createResultsFile(shop, day, month, year);
        if (resultsFile != null)
        {
            BufferedWriter writer = null;
            
            try 
            {
                if (!results.entrySet().isEmpty())
                {
                    writer = new BufferedWriter(new FileWriter(resultsFile, true));

                    writer.write("-----------------------------------------------------\n");
                    writer.write(" - " + section.getName() + " de " + ((section.isMan()) ? "hombre" : "mujer") + "\n");
                    for (Map.Entry<String, Integer> entrySet : results.entrySet())
                    {
                        writer.write("  · " + entrySet.getKey() + ": " + entrySet.getValue() + "\n");
                    }
                }
                
            } catch (IOException ex) {
                LOG.error("Error escribiendo el fichero de resultados");
                LOG.error(ex.getMessage());
                
            } finally {
                try 
                {
                    writer.close();
                    
                } catch (IOException ex) {
                    LOG.error("Error cerrando el fichero de resultados");
                    LOG.error(ex.getMessage());
                }
            }
        }
    }
}
