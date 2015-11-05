package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @class Clase que representa una talla
 * @author Daniel Mancebo Aldea
 */

@Entity
public class Size 
{
    @Id
    @GeneratedValue( strategy = GenerationType.AUTO )
    private long id;
    private String size;
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
