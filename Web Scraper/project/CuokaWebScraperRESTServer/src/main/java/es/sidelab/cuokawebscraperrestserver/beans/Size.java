package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @class Clase que representa una talla
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table( name = "SIZE" )
public class Size 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    @Column( name = "ID" )
    private long id;
    
    @Column( name = "SIZE" )
    private String size;
    
    @Column( name = "STOCK" )
    private boolean stock;
    
    public Size() {}
    
    public Size( String size, boolean stock )
    {
        this.size = size;
        this.stock = stock;
    }
    
    public String getSize() { return this.size; }
    public boolean isStock() { return this.stock; }
    
    public void setSize( String size ) { this.size = size; }
    public void setStock( boolean stock ) { this.stock = stock; }
}
