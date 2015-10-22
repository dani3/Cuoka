package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
    private String name;
    private String shop;
    private String section;
    private double price;
    private String imageURL;
    private String imagePath;
    
    public Product() {}
    
    @Override
    public String toString() 
    {
        return ( "Name: " + this.name
             + "\nShop: " + this.shop
             + "\nSection: " + this.shop
             + "\nPrice: " + this.price );
    }
    
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public double getPrice() { return this.price; }
    public String getImageURL() { return this.imageURL; }
    public String getImagePath() { return this.imagePath; }
    public long getId() { return this.id; }
    
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setPrice( double price ) { this.price = price; }
    public void setImageURL( String imageURL ) { this.imageURL = imageURL; }
    public void setImagePath( String imagePath ) { this.imagePath = imagePath; }
}
