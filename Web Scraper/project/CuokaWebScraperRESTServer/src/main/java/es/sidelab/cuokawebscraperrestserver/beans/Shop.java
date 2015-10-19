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
public class Shop 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String name;
    private URL url;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    private List<Section> sections;
    private boolean offline;
    
    public Shop() {}

    public Shop( String name, URL url, List<Section> sections, boolean offline ) 
    {
        this.name = name;
        this.url = url;
        this.sections = sections;
        this.offline = offline;
    }
    
    @Override
    public String toString()
    {
        String aux = "";
        for ( Section section : this.sections )
            aux = aux.concat( "  - " + section.toString() + "\n" );
        
        
        return ( "Name: " + this.name 
             + "\nURL: " + this.url.toString()
             + "\nOnline: " + !this.offline
             + "\nSections:\n" + aux );
    }

    public String getName() { return name; }
    public URL getUrl() { return url; }
    public List<Section> getSections() { return this.sections; }
    public boolean isOffline() { return this.offline; }
    
    public void setName( String name ) { this.name = name; }    
    public void setUrl( URL url ) { this.url = url; }
    public void setSections( List<Section> sections ) { this.sections = sections; } 
    public void setOffline( boolean offline ) { this.offline = offline; }
}
