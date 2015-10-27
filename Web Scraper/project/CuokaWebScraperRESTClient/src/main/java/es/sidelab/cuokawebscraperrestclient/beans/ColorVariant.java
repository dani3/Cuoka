package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.List;

/**
 * @class Clase que representa las disintas versiones de color de un producto.
 * @author Daniel Mancebo Aldea
 */

public class ColorVariant 
{
    private String colorName;
    private String colorURL;
    private String colorPath;
    private List<Image> images;
    
    public ColorVariant() {}
    
    public ColorVariant( String colorName, String colorURL, List<Image> images )
    {
        this.colorName = colorName;
        this.colorURL = colorURL;
        this.images = images;
    }
    
    public String getColorName() { return this.colorName; }
    public String getColorURL() { return this.colorURL; }
    public String getColorPath() { return this.colorPath; }
    public List<Image> getImages() { return this.images; }
    
    public void setColorName( String colorName ) { this.colorName = colorName; }
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImages( List<Image> images ) { this.images = images; }
}
