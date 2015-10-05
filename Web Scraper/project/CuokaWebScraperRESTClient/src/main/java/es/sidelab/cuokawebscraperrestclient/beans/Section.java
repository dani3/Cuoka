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
    private String _name;
    private URL _url;

    public Section( String name, URL url ) 
    {
        this._name = name;
        this._url = url;
    }
    
    public String getName() { return _name; }
    public URL getURL() { return _url; }
}
