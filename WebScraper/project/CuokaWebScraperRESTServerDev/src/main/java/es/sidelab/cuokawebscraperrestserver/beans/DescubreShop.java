package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Clase que representa una tienda.
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table(name = "DESCUBRE_SHOP")
public class DescubreShop
{    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    
    @Column(name = "NAME")
    private String name;
    
    @Column(name = "MAN")
    private boolean man;
    
    @Column(name = "WOMAN")
    private boolean woman;
    
    @Column(name = "PRODUCTS")
    private int products;
    
    @ElementCollection
    @Column(name = "STYLES")
    private List<String> styles;
    
    public DescubreShop() {}
    
    public DescubreShop(String name, boolean man, boolean woman, int products, List<String> styles)
    {
        this.name = name;
        this.man = man;
        this.woman = woman;
        this.products = products;        
        this.styles = styles;
    } 
    
    public String getName()         { return this.name; }
    public boolean getMan()         { return this.man; }
    public boolean getWoman()       { return this.woman; }
    public int getProducts()        { return this.products; }
    public List<String> getStyles() { return styles; }

    public void setStyles(List<String> styles) { this.styles = styles; }
    public void setName(String name)           { this.name = name; }
    public void setMan(boolean man)            { this.man = man; }
    public void setWoman(boolean woman)        { this.woman = woman; }
    public void setProducts(int products)      { this.products = products; }
}
