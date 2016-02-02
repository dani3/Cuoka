package com.wallakoala.wallakoala.Beans;

import java.io.Serializable;
import java.util.List;

/**
 * @class Clase ColorVariant que representa un color de un producto.
 * Created by Daniel Mancebo on 11/12/2015.
 */

public class ColorVariant implements Serializable
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

    public boolean isOkay()
    {
        if ((this.reference == null) || (this.reference.isEmpty()))
            return false;

        if ((this.colorName == null) || (this.colorName.isEmpty()))
            return false;

        if ((this.colorPath == null) || (this.colorPath.isEmpty()))
            return false;

        for (Image image : this.images)
        {
            if (!image.isOkay())
                return false;
        }

        return true;
    }
}
