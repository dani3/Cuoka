package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.Calendar;
import javax.persistence.Column;
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
@Table(name = "SHOP")
public class Shop 
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
    
    @Column(name = "INSERT_DATE")
    private Calendar insertDate;
    
    @Column(name = "PRODUCTS")
    private int products;
    
    public Shop() {}
    
    public Shop(String name, boolean man, boolean woman, Calendar insertDate, int products)
    {
        this.name = name;
        this.man = man;
        this.woman = woman;
        this.insertDate = insertDate;
        this.products = products;
    }
    
    public String getName()   { return this.name; }
    public boolean getMan()   { return this.man; }
    public boolean getWoman() { return this.woman; }
    public Calendar getInsertDate() { return this.insertDate; }
    public int getProducts() { return this.products; }
    
    public void setName(String name)    { this.name = name; }
    public void setMan(boolean man)     { this.man = man; }
    public void setWoman(boolean woman) { this.woman = woman; }
    public void setInsertDate(Calendar insertDate) { this.insertDate = insertDate; }
    public void setProducts(int products) { this.products = products; }
}
