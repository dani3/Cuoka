package es.sidelab.cuokawebscraperrestclient.beans;

import java.util.List;

/**
 * @class Clase que representa las disintas versiones de un producto
 * @author Daniel Mancebo Aldea
 */

public class Color 
{
    private String colorName;
    private String colorURL;
    private String colorPath;
    private List<String> imagesURL;
    
    public Color() {}
    
    public Color( String colorName, String colorURL, List<String> imagesURL )
    {
        this.colorName = colorName;
        this.colorURL = colorURL;
        this.imagesURL = imagesURL;
    }
    
    public String getColorName() { return this.colorName; }
    public String getColorURL() { return this.colorURL; }
    public String getColorPath() { return this.colorPath; }
    public List<String> getImagesURL() { return this.imagesURL; }
    
    public void setColorName( String colorName ) { this.colorName = colorName; }
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImagesURL( List<String> imagesURL ) { this.imagesURL = imagesURL; }
}
