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
    
    @Column(name = "DESCRIPTION")
    private String description;
    
    @Column(name = "MAN")
    private boolean man;
    
    @Column(name = "WOMAN")
    private boolean woman;
    
    @Column(name = "URL")
    private String url;
    
    @Column(name = "URL_BANNER")
    private String urlBanner;
    
    @Column(name = "ASPECT_RATIO")
    private float aspectRatio;
    
    @ElementCollection
    @Column(name = "STYLES")
    private List<String> styles;
    
    public DescubreShop() {}
    
    public DescubreShop(
          String name
        , String description
        , boolean man
        , boolean woman
        , String url
        , String urlBanner
        , float aspectRatio
        , List<String> styles)
    {
        this.name = name;
        this.description = description;
        this.man = man;
        this.woman = woman;
        this.url = url;
        this.urlBanner = urlBanner;
        this.aspectRatio = aspectRatio;
        this.styles = styles;
    } 
    
    public String getName()         { return this.name; }
    public String getDescription()  { return this.description; }
    public boolean getMan()         { return this.man; }
    public boolean getWoman()       { return this.woman; }
    public String getUrl()          { return this.url; }
    public String getUrlBanner()    { return this.urlBanner; }
    public float getAspectRatio()   { return this.aspectRatio; }
    public List<String> getStyles() { return this.styles; }

    public void setStyles(List<String> styles)     { this.styles = styles; }
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setMan(boolean man)                { this.man = man; }
    public void setWoman(boolean woman)            { this.woman = woman; }
    public void setUrl(String url)                 { this.url = url; }
    public void setUrlBanner(String urlBanner)     { this.urlBanner = urlBanner; }
    public void setAspectRatio(float aspectRatio)  { this.aspectRatio = aspectRatio; }
}
