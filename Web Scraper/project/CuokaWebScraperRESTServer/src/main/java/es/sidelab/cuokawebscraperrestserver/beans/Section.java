package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @class Bean que representa una categoria de una tienda
 * @author Daniel Mancebo Aldea
 */

@Entity
public class Section 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String name;
    private URL url;
    
    public Section() {}
    
    public Section( String name, URL url )
    {
        this.name = name;
        this.url = url;
    }
    
    @Override
    public String toString()
    {
        return ( "Name: " + this.name + " | URL: " + this.url.toString() + "\n" );
    }

    public String getName() { return name; }
    public URL getUrl() { return url; } 
    
    public void setName( String name ) { this.name = name; }
    public void setUrl( URL url ) { this.url = url; }    
}
