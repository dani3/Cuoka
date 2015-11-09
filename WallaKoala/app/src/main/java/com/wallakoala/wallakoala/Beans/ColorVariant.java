package com.wallakoala.wallakoala.Beans;

import java.util.List;

public class ColorVariant 
{
    private String reference;
    private String colorName;
    private String colorURL;
    private String colorPath;    
    private List<Image> images;
    private List<Size> sizes;
    
    public ColorVariant() {}
    
    public ColorVariant( String reference
    		, String colorName
    		, String colorURL
    		, String colorPath
    		, List<Image> images
    		, List<Size> sizes ) 
    {
		this.reference = reference;
		this.colorName = colorName;
		this.colorURL = colorURL;
		this.colorPath = colorPath;
		this.images = images;
		this.sizes = sizes;
	}

	public String getReference() { return this.reference; }
    public String getColorName() { return this.colorName; }
    public String getColorURL() { return this.colorURL; }
    public String getColorPath() { return this.colorPath; }
    public List<Image> getImages() { return this.images; }
    public List<Size> getSizes() { return this.sizes; }
    
    public void setReference( String reference ) { this.reference = reference; }
    public void setColorName( String colorName ) { this.colorName = colorName; }
    public void setColorURL( String colorURL ) { this.colorURL = colorURL; }
    public void setColorPath( String colorPath ) { this.colorPath = colorPath; }
    public void setImages( List<Image> images ) { this.images = images; }
    public void setSizes( List<Size> sizes ) { this.sizes = sizes; }
}
