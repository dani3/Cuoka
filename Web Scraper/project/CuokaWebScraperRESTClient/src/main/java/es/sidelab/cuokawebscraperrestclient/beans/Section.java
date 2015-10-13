package es.sidelab.cuokawebscraperrestclient.beans;

import java.net.URL;

/**
 * @class Clase que representa una sección de una tienda, contendrá su URL,
 *        el nombre y el sexo.
 * 
 * @author Daniel Mancebo Aldea
 */
public class Section 
{
    private String name;
    private URL url;
    
    public Section() {}

    public Section( String name, URL url ) 
    {
        this.name = name;
        this.url = url;
    }

    public void setName( String name ) { this.name = name; }
    public void setUrl( URL url ) { this.url = url; }
    
    public String getName() { return name; }
    public URL getURL() { return url; }
}
