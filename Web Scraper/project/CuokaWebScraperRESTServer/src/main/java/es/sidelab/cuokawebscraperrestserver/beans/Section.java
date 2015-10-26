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
    private boolean man;
    
    public Section() {}
    
    @Override
    public String toString()
    {
        return ( "Name: " + this.name + " | URL: " + this.url.toString() + " | Seccion hombre " + this.man + "\n" );
    }

    public String getName() { return this.name; }
    public URL getUrl() { return this.url; } 
    public boolean isMan() { return this.man; }
    
    public void setName( String name ) { this.name = name; }
    public void setUrl( URL url ) { this.url = url; }    
    public void setMan( boolean man ) { this.man = man; }
}
