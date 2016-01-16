package com.wallakoala.wallakoala.Beans;

import java.util.List;

/**
 * @class Clase ColorVariant que representa un color de un producto.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class ColorVariant
{
    private String reference;
    private String colorName;
    private String colorPath;    
    private List<Image> images;
    
    public ColorVariant() {}
    
    public ColorVariant( String reference
    		, String colorName
    		, String colorPath
    		, List<Image> images )
    {
		this.reference = reference;
		this.colorName = colorName;
		this.colorPath = colorPath;
		this.images = images;
	}

	public String getReference()   { return this.reference; }
    public String getColorName()   { return this.colorName; }
    public String getColorPath()   { return this.colorPath; }
    public List<Image> getImages() { return this.images; }
    
    public void setReference( String reference ) { this.reference = reference; }
    public void setColorName( String colorName ) { this.colorName = colorName; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImages( List<Image> images )  { this.images = images; }
}
