package es.sidelab.cuokawebscraperrestserver.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.sidelab.cuokawebscraperrestserver.utils.ImageManager;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Bean que representa un producto
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table(name = "PRODUCT")
public class Product implements Comparator
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    
    @Column(name = "PRICE")
    private double price;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "SHOP")
    private String shop;
    
    @Column(name = "SECTION")
    private String section;
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "LINK")
    private String link;
    
    @JsonIgnore
    @Column(name = "MAN")
    private boolean man;
    
    @JsonIgnore
    @Column(name = "ASPECT_RATIO")
    private float aspectRatio;
    
    @ElementCollection
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Column(name = "COLORS")
    private List<ColorVariant> colors;
    
    @JsonIgnore
    @Column(name = "INSERT_DATE")
    private Calendar insertDate;
    
    public Product() {}
    
    @JsonProperty("price")
    public void setPrice(double price) { this.price = price; }
    @JsonProperty("name")
    public void setName(String name) { this.name = name; }
    @JsonProperty("shop")
    public void setShop(String shop) { this.shop = shop; }
    @JsonProperty("section")
    public void setSection(String section) { this.section = section; }
    @JsonProperty("description")
    public void setDescription(String description) { this.description = description; }
    @JsonProperty("link")
    public void setLink(String link) { this.link = link; }    
    @JsonProperty
    public void setMan(boolean man) { this.man = man; }
    @JsonProperty("colors")
    public void setColors(List<ColorVariant> colors) { this.colors = colors; }  
    @JsonIgnore
    public void setInsertDate(Calendar insertDate) { this.insertDate = insertDate; }
    @JsonIgnore
    public void setAspectRatio(float aspectRatio) { this.aspectRatio = aspectRatio; }
        
    @JsonProperty("1")
    public double getPrice() { return this.price; }
    @JsonProperty("2")
    public String getName() { return this.name; }
    @JsonProperty("3")
    public String getShop() { return this.shop; }
    @JsonProperty("4")
    public String getSection() { return this.section; }
    @JsonProperty("5")
    public String getLink() { return this.link; }    
    @JsonIgnore
    public boolean isMan() { return this.man; }
    @JsonProperty("6")
    public List<ColorVariant> getColors() { return this.colors; }
    @JsonProperty("7")
    public String getDescription() { return this.description; }  
    @JsonProperty("8")
    public long getId() { return this.id; } 
    @JsonProperty("9")
    public float getAspectRatio() { return this.aspectRatio; } 
    @JsonIgnore
    public Calendar getInsertDate() { return this.insertDate; }
    
    public void update(Product product, boolean equal)
    {
        this.price = product.price;
        this.link = product.link;
        this.description = product.description;
        this.colors = product.colors;
        this.aspectRatio = ImageManager.getAspectRatio(shop);
        
        if (!equal)
        {
            this.insertDate = Calendar.getInstance();
        }
    }

    @Override
    public int compare(Object origin, Object other) 
    {
        Product thisProduct = (Product)origin;
        Product otherProduct = (Product)other;
        
        // Si se cumple todo, es el mismo producto.
        if ((thisProduct.shop.equals(otherProduct.shop)) && 
            (thisProduct.section.equals(otherProduct.section)) && 
            (thisProduct.name.equals(otherProduct.name)) && 
            (thisProduct.man == otherProduct.man) &&
            (thisProduct.colors.get(0).getReference().equals(otherProduct.colors.get(0).getReference())))
        {
            return (thisProduct.price == otherProduct.price) ? 0 : 1;
        }
     
        return -1;
    }
    
    @Override
    public String toString() 
    {
        return ("Name: " + this.name
             + "\nShop: " + this.shop
             + "\nSection: " + this.shop
             + "\nPrice: " + this.price
             + "\nNumero de colores: " + this.colors.size());
    }
}
