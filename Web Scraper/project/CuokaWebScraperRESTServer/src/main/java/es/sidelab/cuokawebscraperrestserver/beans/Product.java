package es.sidelab.cuokawebscraperrestserver.beans;

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
 * @class Bean que representa un producto
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "PRODUCT" )
public class Product 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "" )
    private long id;
    
    @Column( name = "PRICE" )
    private double price;
    
    @Column( name = "NAME" )
    private String name;
    
    @Column( name = "SHOP" )
    private String shop;
    
    @Column( name = "SECTION" )
    private String section;
    
    @Column( name = "LINK" )
    private String link;
    
    @Column( name = "MAN" )
    private boolean man;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    @Column( name = "COLORS" )
    private List<ColorVariant> colors;
    
    public Product() {}
    
    @Override
    public String toString() 
    {
        return ( "Name: " + this.name
             + "\nShop: " + this.shop
             + "\nSection: " + this.shop
             + "\nPrice: " + this.price
             + "\nNumero de colores: " + this.colors.size() );
    }
    
    public boolean isOkay()
    {
        if ( ( price <= 0.0f ) || ( this.name == null ) || ( this.name.isEmpty() ) || ( this.shop == null ) ||
             ( this.shop.isEmpty() ) || ( this.section == null ) || ( this.section.isEmpty() ) || ( this.link == null ) ||
             ( this.link.isEmpty() ) || ( this.colors == null ) || ( this.colors.isEmpty() ) )
        {
            return false;
        } 
        
        for ( ColorVariant color : this.colors )
        {
            if ( ( color.getColorName() == null ) || ( color.getColorName().isEmpty() ) ||
                 ( color.getColorURL() == null ) || ( color.getColorURL().isEmpty() ) ||
                 ( color.getReference() == null ) || ( color.getReference().isEmpty() ) )
            {
                return false;
            }
        }
        
        return true;
    }
    
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setLink( String link ) { this.link = link; }
    public void setMan( boolean man ) { this.man = man; }
    public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getLink() { return this.link; }
    public boolean isMan() { return this.man; }
    public List<ColorVariant> getColors() { return this.colors; }
    public long getId() { return this.id; }
}
