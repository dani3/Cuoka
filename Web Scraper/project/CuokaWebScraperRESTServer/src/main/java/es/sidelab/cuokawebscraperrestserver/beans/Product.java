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
 * @class Bean que representa un producto
 * @author Daniel Mancebo Aldea
 */

@Entity
public class Product 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private double price;
    private String name;
    private String shop;
    private String section;
    private String link;
    private boolean man;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
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
}
