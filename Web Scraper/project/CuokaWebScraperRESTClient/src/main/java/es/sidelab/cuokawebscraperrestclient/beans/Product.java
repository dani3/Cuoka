package es.sidelab.cuokawebscraperrestclient.beans;

/**
 * @class Clase que representa un producto.
 * @author Daniel Mancebo Aldea
 */

public class Product 
{
    private double price;
    private String name;
    private String shop;
    private String section;
    private String imageURL;
    
    public Product() {}

    public Product( double price, String name, String shop, String section, String imageURL ) 
    {
        this.price = price;
        this.name = name;
        this.shop = shop;
        this.section = section;
        this.imageURL = imageURL;
    }  
    
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setImageURL( String imageURL ) { this.imageURL = imageURL; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getImageURL() { return this.imageURL; }
}
