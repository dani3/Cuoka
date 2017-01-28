package es.sidelab.cuokawebscraperrestclient.utils;

import es.sidelab.cuokawebscraperrestclient.beans.Section;
import es.sidelab.cuokawebscraperrestclient.beans.Shop;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase que analiza el resultado del proceso de scraping de cada tienda.
 * Contiene un mapa con todos los posibles errores que pueden ocurrir.
 * @author Daniel Mancebo Aldea
 */
public class ScrapingAnalyzer 
{
    private final Map<String, Integer> scrapingResults;
    private final Shop shop;
    private final Section section;
    
    /**
     * Constructor que recibe la tienda y la seccion.
     * @param shop: tienda.
     * @param section: seccion.
     */
    public ScrapingAnalyzer(Shop shop, Section section)
    {
        scrapingResults = new HashMap<>();
        
        this.shop = shop;
        this.section = section;
    }
    
    /**
     * Metodo que guarda en el mapa un nuevo error.
     * @param error: error encontrado.
     */
    public void saveError(String error)
    {
        Integer count = scrapingResults.get(error);
        
        if (count == null) 
        {
            scrapingResults.put(error, 1);
            
        } else {
            scrapingResults.put(error, ++count);
        }            
    }
    
    /**
     * Metodo que escribe en fichero los resultados del proceso de scraping.
     */
    public void printResults()
    {
        Printer.printResults(shop, section, scrapingResults);
    }
}
