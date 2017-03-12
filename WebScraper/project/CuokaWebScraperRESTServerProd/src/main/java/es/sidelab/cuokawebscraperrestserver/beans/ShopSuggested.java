package es.sidelab.cuokawebscraperrestserver.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Bean que almacena las tiendas sugeridas por un usuario.
 * @author Daniel Mancebo Aldea
 */

@Entity
@Table(name = "SHOP_SUGGESTED")
public class ShopSuggested 
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private long id;
    
    @Column(name = "SHOP")
    private String shop;
    
    @Column(name = "LINK")
    private String link;

    public ShopSuggested() {}
    
    public ShopSuggested(String shop, String link) 
    {
        this.shop = shop;
        this.link = link;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
