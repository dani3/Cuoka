package es.sidelab.cuokawebscraperrestserver.beans;

import java.util.Calendar;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Clase que representa los productos que han sido añadidos en el pasado
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "HISTORIC_PRODUCT" )
public class HistoricProduct 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
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

    public HistoricProduct() {}

    public HistoricProduct( String shop
                , String section
                , String reference
                , String color
                , Calendar insertDate ) 
    {
        this.shop = shop;
        this.section = section;
        this.reference = reference;
        this.insertDate = insertDate;
        this.color = color;
    }

    public String getColor() { return color; }
    public String getReference() { return reference; }
    public Calendar getInsertDate() { return insertDate; }
    public String getSection() { return section; }
    public String getShop() { return shop; }

    public void setColor( String color ) { this.color = color; }
    public void setShop( String shop ) { this.shop = shop; }
    public void setSection( String section ) { this.section = section; }
    public void setReference( String reference ) { this.reference = reference; }
    public void setInsertDate( Calendar insertDate ) { this.insertDate = insertDate; }
}
