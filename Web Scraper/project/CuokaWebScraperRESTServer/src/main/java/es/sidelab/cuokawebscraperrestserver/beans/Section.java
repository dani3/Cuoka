package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Bean que representa una categoria de una tienda
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "SECTION" )
public class Section 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "NAME" )
    private String name;
    
    @Column( name = "URL" )
    private URL url;
    
    @Column( name = "MAN" )
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
