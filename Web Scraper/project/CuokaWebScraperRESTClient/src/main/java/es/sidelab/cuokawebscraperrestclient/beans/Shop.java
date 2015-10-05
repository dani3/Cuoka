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
    private String _name;
    private URL _url;
    private List<Section> _sections;

    public Shop( String name, URL url, List<Section> sections ) 
    {
        this._name = name;
        this._url = url;
        this._sections = sections;
    }
    
    public String getName() { return _name; }
    public URL getURL() { return _url; }
    public List<Section> getSections() { return _sections; }
}
