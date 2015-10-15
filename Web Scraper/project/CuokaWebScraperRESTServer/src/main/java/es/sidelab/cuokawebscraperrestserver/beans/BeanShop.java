package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

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
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    private List<BeanSection> sections;
    private boolean offline;
    
    public BeanShop() {}

    public BeanShop( String name, URL url, List<BeanSection> sections, boolean offline ) 
    {
        this.name = name;
        this.url = url;
        this.sections = sections;
        this.offline = offline;
    }

    public String getName() { return name; }
    public void setName( String name ) { this.name = name; }
    public URL getUrl() { return url; }
    public void setUrl( URL url ) { this.url = url; }
    public List<BeanSection> getSections() { return this.sections; }
    public void setSections( List<BeanSection> sections ) { this.sections = sections; } 
    public void setOffline( boolean offline ) { this.offline = offline; }
    public boolean isOffline() { return this.offline; }
}
