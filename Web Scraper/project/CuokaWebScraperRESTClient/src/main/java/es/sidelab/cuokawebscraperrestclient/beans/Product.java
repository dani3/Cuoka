package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.Calendar;
import java.util.List;

/**
 * @class Clase que representa un producto. Si este tiene un solo color, la lista 
 *        colors contendra solo un objeto ColorVariant.
 * 
 * @author Daniel Mancebo Aldea
 */

public class Product 
{
    private double price;
    private String name;
    private String shop;
    private String section;
    private String link;
    private boolean man;
    private boolean newness;
    private Calendar insertDate;
    private List<ColorVariant> colors;
    
    public Product() {}

    public Product( double price
            , String name
            , String shop
            , String section
            , String link
            , boolean man
            , List<ColorVariant> colors ) 
    {
        this.price = price;
        this.name = name;
        this.shop = shop;
        this.section = section;
        this.link = link;
        this.man = man;
        this.colors = colors;
        this.newness = false;
        this.insertDate = Calendar.getInstance();
    }  
    
    public void setPrice( double price ) { this.price = price; }
    public void setName( String name ) { this.name = name; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setLink( String link ) { this.link = link; }
    public void setMan( boolean man ) { this.man = man; }
    public void setColors( List<ColorVariant> colors ) { this.colors = colors; }
    public void setNewness( boolean newness ) { this.newness = newness; }
    public void setInsertDate( Calendar insertDate ) { this.insertDate = insertDate; }
    
    public double getPrice() { return this.price; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getLink() { return this.link; }
    public boolean isMan() { return this.man; }
    public List<ColorVariant> getColors() { return this.colors; }
    public boolean isNewness() { return this.newness; }
    public Calendar getInsertDate() { return this.insertDate; }
}
