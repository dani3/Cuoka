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
    @JsonIgnore
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "PRICE" )
    private double price;
    
    @Column( name = "NAME" )
    private String name;
    
    @Column( name = "SHOP" )
    private String shop;
    
    @Column( name = "SECTION" )
    private String section;
    
    @Column( name = "DESCRIPTION" )
    private String description;
    
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
    
    @JsonProperty( "price" )
    public void setPrice( double price ) { this.price = price; }
    @JsonProperty( "name" )
    public void setName( String name ) { this.name = name; }
    @JsonProperty( "shop" )
    public void setShop( String shop ) { this.shop = shop; }
    @JsonProperty( "section" )
    public void setSection( String section ) { this.section = section; }
    @JsonProperty( "description" )
    public void setDescription( String description ) { this.description = description; }
    @JsonProperty( "link" )
    public void setLink( String link ) { this.link = link; }    
    @JsonProperty
    public void setMan( boolean man ) { this.man = man; }
    @JsonProperty( "colors" )
    public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
    @JsonProperty( "newness" )
    public void setNewness( boolean newness ) { this.newness = newness; }    
    @JsonIgnore
    public void setInsertDate( Calendar insertDate ) { this.insertDate = insertDate; }
        
    @JsonProperty( "1" )
    public double getPrice() { return this.price; }
    @JsonProperty( "2" )
    public String getName() { return this.name; }
    @JsonProperty( "3" )
    public String getShop() { return this.shop; }
    @JsonProperty( "4" )
    public String getSection() { return this.section; }
    @JsonProperty( "5" )
    public String getLink() { return this.link; }    
    @JsonIgnore
    public boolean isMan() { return this.man; }
    @JsonProperty( "6" )
    public List<ColorVariant> getColors() { return this.colors; }
    public long getId() { return this.id; }
    @JsonProperty( "7" )
    public boolean isNewness() { return this.newness; }    
    @JsonProperty( "8" )
    public String getDescription() { return this.description; }  
    @JsonIgnore
    public Calendar getInsertDate() { return this.insertDate; }
}
