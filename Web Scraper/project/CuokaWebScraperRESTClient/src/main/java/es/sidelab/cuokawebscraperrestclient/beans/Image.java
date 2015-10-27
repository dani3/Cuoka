package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa una imagen de un producto
 * @author Daniel Mancebo Aldea
 */

public class Image 
{
    private String url;
    private String path;
    
    public Image() {}

    public Image( String url ) 
    {
        this.url = url;
    }

    public String getUrl() { return url; }
    public String getPath() { return path; }

    public void setUrl( String url ) { this.url = url; }
    public void setPath( String path ) { this.path = path; }
}
