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
    private boolean man;
    
    public Section() {}

    public void setName( String name ) { this.name = name; }
    public void setUrl( URL url ) { this.url = url; }
    public void setMan( boolean man ) { this.man = man; }
    
    public String getName() { return this.name; }
    public URL getURL() { return this.url; }
    public boolean isMan() { return this.man; }
}
