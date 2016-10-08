package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.List;

/**
 * Clase que representa las distintas versiones de color de un producto.
 * @author Daniel Mancebo Aldea
 */

public class ColorVariant 
{
    private String reference;
    private String name;
    private String colorURL;
    private String path;
    private List<Image> images;
    
    public ColorVariant() {}
    
    public ColorVariant(String reference
                , String name
                , String colorURL
                , List<Image> images)
    {
        this.reference = reference;
        this.name = name;
        this.colorURL = colorURL;
        this.images = images;
    }
    
    public String getReference() { return this.reference; }
    public String getName() { return this.name; }
    public String getColorURL() { return this.colorURL; }
    public String getPath() { return this.path; }
    public List<Image> getImages() { return this.images; }
    
    public void setReference(String reference) { this.reference = reference; }
    public void setName(String name) { this.name = name; }
    public void setColorURL(String colorURL) { this.colorURL = colorURL; }
    public void setPath(String path) { this.path = path; }
    public void setImages(List<Image> images) { this.images = images; }
}
