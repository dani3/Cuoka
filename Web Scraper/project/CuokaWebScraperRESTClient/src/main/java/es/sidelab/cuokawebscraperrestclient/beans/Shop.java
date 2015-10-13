package es.sidelab.cuokawebscraperrestclient.beans;

import java.net.URL;
import java.util.List;

/**
 * @class Clase que representa una tienda, contendrá la URL, 
 *        una lista de secciones y un scraper específico. 
 * 
 * @author Daniel Mancebo Aldea
 */
public class Shop 
{
    private String name;
    private URL url;
    private List<Section> sections;
    
    public Shop() {}

    public Shop( String name, URL url, List<Section> sections ) 
    {
        this.name = name;
        this.url = url;
        this.sections = sections;
    }

    public void setUrl( URL url ) { this.url = url; }
    public void setName( String name ) { this.name = name; }
    public void setSections( List<Section> sections ) { this.sections = sections; }   
    
    public String getName() { return name; }
    public URL getURL() { return url; }
    public List<Section> getSections() { return sections; }
}
