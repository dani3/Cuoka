package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * @class Clase que representa las distintas versiones de color de un producto.
 * @author Daniel Mancebo Aldea
 */

@Entity
public class ColorVariant 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String reference;
    private String colorName;
    private String colorURL;
    private String colorPath;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    private List<Image> images;
    
    public ColorVariant() {}
    
    public String getReference() { return this.reference; }
    public String getColorName() { return this.colorName; }
    public String getColorURL() { return this.colorURL; }
    public String getColorPath() { return this.colorPath; }
    public List<Image> getImages() { return this.images; }
    
    public void setReference( String reference ) { this.reference = reference; }
    public void setColorName( String colorName ) { this.colorName = colorName; }
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImages( List<Image> images ) { this.images = images; }
}
