package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @class Clase que representa una categoria de una tienda, contiene el nombre y la URL
 * @author Daniel Mancebo Aldea
 */

@Entity
public class BeanSection 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String name;
    private URL url;
    
    public BeanSection() {}
    
    public BeanSection( String name, URL url )
    {
        this.name = name;
        this.url = url;
    }

    public String getName() { return name; }

    public void setName( String name ) { this.name = name; }

    public URL getUrl() { return url; }

    public void setUrl( URL url ) { this.url = url; }    
}
