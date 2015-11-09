package es.sidelab.cuokawebscraperrestserver.beans;

import java.net.URL;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @class Clase que representa una tienda, contiene el nombre, la URL y la lista de categorias
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "SHOP" )
public class Shop 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "NAME" )
    private String name;
    
    @Column( name = "URL" )
    private URL url;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    @Column( name = "SECTIONS" )
    private List<Section> sections;
    
    @Column( name = "OFFLINE" )
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
    
    public boolean isOkay()
    {
        if ( ( this.name == null ) ||
             ( this.url == null ) ||
             ( this.name.equals( "" ) ) )          
            return false;
        
        if ( this.sections.isEmpty() )          
            return false;
            
        return true;
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
