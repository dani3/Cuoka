package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @class Clase que representa una tienda, contiene el nombre, la URL y la lista de categorias
 * @author Daniel Mancebo Aldea
 */

@Entity
public class BeanShop 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String name;
    private URL url;
    
    public BeanShop() {}

    public BeanShop( String name, URL url ) 
    {
        this.name = name;
        this.url = url;
    }

    public String getName() { return name; }

    public void setName( String name ) { this.name = name; }

    public URL getUrl() { return url; }

    public void setUrl( URL url ) { this.url = url; }    
}
