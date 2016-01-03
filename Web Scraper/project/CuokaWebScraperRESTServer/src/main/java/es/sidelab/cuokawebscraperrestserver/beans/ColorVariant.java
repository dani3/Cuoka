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
    private String colorName;
    
    @JsonIgnore
    @Column( name = "URL" )
    private String colorURL;
    
    @Column( name = "PATH" )
    private String colorPath;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    @Column( name = "IMAGES" )
    private List<Image> images;

    
    public ColorVariant() {}
    
    public String getReference() { return this.reference; }
    public String getColorName() { return this.colorName; }
    
    @JsonIgnore
    public String getColorURL() { return this.colorURL; }
    public String getColorPath() { return this.colorPath; }
    public List<Image> getImages() { return this.images; }

    public void setReference( String reference ) { this.reference = reference; }
    public void setColorName( String colorName ) { this.colorName = colorName; }
    
    @JsonProperty
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImages( List<Image> images ) { this.images = images; }
}
