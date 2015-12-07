package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author Dani
 */

@Entity
@Table( name = "HISTORIC_PRODUCT" )
public class HistoricProduct 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "" )
    private long id;
    
    @Column( name = "SHOP" )
    private String shop;
    
    @Column( name = "SECTION" )
    private String section;
    
    @Column( name = "REFERENCE" )
    private String reference;
    
    @Column( name = "COLOR" )
    private String color;
    
    @Column( name = "INSERT_DATE" )
    private Calendar insertDate;

    public HistoricProduct(String shop, String section, String reference, String color, Calendar insertDate) 
    {
        this.shop = shop;
        this.section = section;
        this.reference = reference;
        this.insertDate = insertDate;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Calendar getInsertDate() {
        return insertDate;
    }

    public void setInsertDate(Calendar insertDate) {
        this.insertDate = insertDate;
    }
}
