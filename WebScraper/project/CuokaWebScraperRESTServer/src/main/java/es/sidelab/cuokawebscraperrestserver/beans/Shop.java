package es.sidelab.cuokawebscraperrestserver.beans;

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
    
    public Shop() {}
    
    public Shop(String name)
    {
        this.name = name;
    }
    
    public String getName() { return this.name; }
    
    public void setName(String name) { this.name = name; }
}
