package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.Calendar;
import java.util.List;

/**
 * Clase que representa un producto. Si este tiene un solo color, la lista 
 *       colors contendra solo un objeto ColorVariant.
 * 
 * @author Daniel Mancebo Aldea
 */

public class Product 
{
    private double price;
    private double discount;
    private String name;
    private String shop;
    private String section;
    private String link;
    private String description;
    private boolean man;
    private boolean newness;
    private Calendar insertDate;
    private List<ColorVariant> colors;
    
    public Product() {}

    public Product(double price
            , double discount
            , String name
            , String shop
            , String section
            , String link
            , String description
            , boolean man
            , List<ColorVariant> colors) 
    {
        this.price = price;
        this.discount = discount;
        this.name = name;
        this.shop = shop;
        this.section = section;
        this.description = description;
        this.link = link;
        this.man = man;
        this.colors = colors;
        this.newness = false;
        this.insertDate = Calendar.getInstance();
    }  
    
    public void setPrice(double price) { this.price = price; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setName(String name) { this.name = name; }
    public void setShop(String shop) { this.shop = shop; }
    public void setSection(String section) { this.section = section; }
    public void setDescription(String description) { this.description = description; }
    public void setLink(String link) { this.link = link; }
    public void setMan(boolean man) { this.man = man; }
    public void setColors(List<ColorVariant> colors) { this.colors = colors; }
    public void setNewness(boolean newness) { this.newness = newness; }
    public void setInsertDate(Calendar insertDate) { this.insertDate = insertDate; }
    
    public double getPrice() { return this.price; }
    public double getDiscount() { return this.discount; }
    public String getName() { return this.name; }
    public String getShop() { return this.shop; }
    public String getSection() { return this.section; }
    public String getDescription() { return this.description; }
    public String getLink() { return this.link; }
    public boolean isMan() { return this.man; }
    public List<ColorVariant> getColors() { return this.colors; }
    public boolean isNewness() { return this.newness; }
    public Calendar getInsertDate() { return this.insertDate; }
}
