package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.List;

/**
 * @class Clase que representa un producto. Si este tiene un solo color, la lista colors contendra solo un objeto Color
 * @author Daniel Mancebo Aldea
 */

public class Product 
{
    private String reference;
    private double price;
    private String name;
    private String shop;
    private String section;
    private String link;
    private boolean man;
    private List<Color> colors;
    private List<Image> images;
    
    public Product() {}

    public Product( String reference
            , double price
            , String name
            , String shop
            , String section
            , String link
            , boolean man
            , List<Color> colors
            , List<Image> images ) 
    {
        this.price = price;
        this.name = name;
        this.shop = shop;
        this.section = section;
        this.link = link;
        this.man = man;
        this.colors = colors;
        this.images = images;
        this.reference = reference;
    }  
    
    public void setReference( String reference ) { this.reference = reference; }
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setLink( String link ) { this.link = link; }
    public void setMan( boolean man ) { this.man = man; }
    public void setImages( List<Image> images ) { this.images = images; }
    public void setColors( List<Color> colors ) { this.colors = colors; }
    
    public String getReference() { return this.reference; }
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getLink() { return this.link; }
    public boolean isMan() { return this.man; }
    public List<Color> getColors() { return this.colors; }
    public List<Image> getImages() { return this.images; }
}
