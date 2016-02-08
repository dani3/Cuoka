package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
 * @class Clase que representa las distintas versiones de color de un producto.
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "COLOR_VARIANT" )
public class ColorVariant 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "REFERENCE" )
    private String reference;
    
    @Column( name = "NAME" )
    private String name;
    
    @JsonIgnore
    @Column( name = "URL" )
    private String colorURL;
    
    @Column( name = "PATH" )
    private String path;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    @JsonIgnore
    @Column( name = "IMAGES" )
    private List<Image> images;
    
    @Column( name = "NUM_IMAGES" )
    private short numberOfImages;

    
    public ColorVariant() {}    
    
    @JsonIgnore
    public List<Image> getImages() { return this.images; }
    @JsonIgnore
    public String getColorURL() { return this.colorURL; }
    @JsonProperty( "1" )
    public String getReference() { return this.reference; }
    @JsonProperty( "2" )
    public String getName() { return this.name; }
    @JsonProperty( "3" )
    public short getNumberOfImages() { return this.numberOfImages; }   
    @JsonProperty( "4" )
    public String getPath() { return this.path; }    

    @JsonProperty( "reference" )
    public void setReference( String reference ) { this.reference = reference; }
    @JsonProperty( "name" )
    public void setName( String name ) { this.name = name; }
    @JsonProperty( "numberOfImages" )
    public void setNumberOfImages( short numberOfImages ) { this.numberOfImages = numberOfImages; }   
    @JsonProperty( "path" )
    public void setPath( String path ) { this.path = path; }      
    @JsonProperty
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    @JsonProperty
    public void setImages( List<Image> images ) { this.images = images; }
}
