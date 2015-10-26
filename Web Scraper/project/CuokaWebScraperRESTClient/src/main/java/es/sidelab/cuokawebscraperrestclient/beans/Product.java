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
    private String imagePath;
    private String link;
    private boolean man;
    
    public Product() {}

    public Product( double price
            , String name
            , String shop
            , String section
            , String imageURL
            , String link
            , boolean man ) 
    {
        this.price = price;
        this.name = name;
        this.shop = shop;
        this.section = section;
        this.imageURL = imageURL;
        this.link = link;
        this.man = man;
    }  
    
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setImageURL( String imageURL ) { this.imageURL = imageURL; }
    public void setImagePath( String imagePath ) { this.imagePath = imagePath; }
    public void setLink( String link ) { this.link = link; }
    public void setMan( boolean man ) { this.man = man; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getImageURL() { return this.imageURL; }
    public String getImagePath() { return this.imagePath; }
    public String getLink() { return this.link; }
    public boolean isMan() { return this.man; }
}
