package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Calendar;
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
    
    @JsonIgnore
    @Column( name = "MAN" )
    private boolean man;
    
    @ElementCollection
    @OneToMany( cascade = CascadeType.ALL )
    @Column( name = "COLORS" )
    private List<ColorVariant> colors;
    
    @Column( name = "NEWNESS" )
    private boolean newness;
    
    @JsonIgnore
    @Column( name = "INSERT_DATE" )
    private Calendar insertDate;
    
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
    
    @JsonProperty
    public void setMan( boolean man ) { this.man = man; }
    public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
    public void setNewness( boolean newness ) { this.newness = newness; }
    
    @JsonIgnore
    public void setInsertDate( Calendar insertDate ) { this.insertDate = insertDate; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getLink() { return this.link; }
    
    @JsonIgnore
    public boolean isMan() { return this.man; }
    public List<ColorVariant> getColors() { return this.colors; }
    public long getId() { return this.id; }
    public boolean isNewness() { return this.newness; }
    
    @JsonIgnore
    public Calendar getInsertDate() { return this.insertDate; }
}
