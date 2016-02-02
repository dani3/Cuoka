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
    private short numberOfImages;
    
    public ColorVariant() {}
    
    public ColorVariant( String reference
    		, String colorName
    		, String colorPath
    		, short numberOfImages )
    {
		this.reference = reference;
		this.colorName = colorName;
		this.colorPath = colorPath;
		this.numberOfImages = numberOfImages;
	}

	public String getReference()     { return this.reference; }
    public String getColorName()     { return this.colorName; }
    public String getColorPath()     { return this.colorPath; }
    public short getNumberOfImages() { return this.numberOfImages; }

    public boolean isOkay()
    {
        if ((this.reference == null) || (this.reference.isEmpty()))
            return false;

        if ((this.colorName == null) || (this.colorName.isEmpty()))
            return false;

        if ((this.colorPath == null) || (this.colorPath.isEmpty()))
            return false;

        if (this.numberOfImages <= 0)
            return false;

        return true;
    }
}
